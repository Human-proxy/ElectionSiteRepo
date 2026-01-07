## Database Caching
This document explains how we implemented caching for database queries in the election backend to improve performance and reduce load on the database.

### What we want to solve

- The frontend asks the backend for election data and party lists. If the backend asks the database every time, the DB gets slow and noisy.
- So we cache results on the server: the first request reads from the DB, then later requests get the same data from memory (much faster).

### Where the cache lives

- File: `src/main/java/.../config/CacheConfig.java` — this sets up the caches.
- We use a simple in-memory cache provided by Spring (`ConcurrentMapCacheManager`). That means cache lives inside the running Java process.

Important: this cache is per JVM. If you run two backend instances, each one has its own cache.
### The caches we have

- `parties` — all parties (used for lists)
- `electedParties` — parties that actually won seats (used on the homepage)
- `elections` — full election objects (e.g. TK2023)
- `top4` — top-4 constituencies for the dashboard

Each cache is just a name in `CacheConfig`. Service methods are tagged with `@Cacheable` so Spring stores and returns results automatically.

### Which methods use the cache

- `PartyService#getElectedParties()` → cached in `electedParties`
- `PartyService#getAllParties()` → cached in `parties`
- `DutchElectionService#getElectionById(electionId)` → cached in `elections` with key `election-<id>` (for example `election-TK2023`)
- `ConstituencyService#getTop4Constituencies()` → cached in `top4` (key `top4-TK2023`)

Because caching is on the service layer, the controllers call services as usual — they don't need to know about caching.

### How cache eviction works

- We clear the caches automatically every hour using a scheduled task (`@Scheduled` + `@CacheEvict`) in `CacheConfig`. This keeps data reasonably fresh.
- If you update data (import new election data), you'd typically evict the affected cache entries programmatically or with `@CacheEvict` on the import method.

Quick example to evict a cache entry in code:

```java
cacheManager.getCache(CacheConfig.ELECTION_CACHE).evict("election-TK2023");
```

Or, annotate a method that updates data:

```java
@CacheEvict(value = {CacheConfig.ELECTION_CACHE, CacheConfig.TOP4_CACHE}, allEntries = true)
public void importAndPersistElection(...) { ... }
```

### How you can test caching locally (fast)

1) Start the backend:

```bash
cd election-backend
./mvnw spring-boot:run
```

2) Open one terminal and watch the server logs. (If you want, temporarily enable SQL logging — see below.)

3) Call the same endpoint two times quickly. Example:

```bash
curl -sS http://localhost:8080/api/parties/homepage | jq .
curl -sS http://localhost:8080/api/parties/homepage | jq .
```

What to expect:
- First call: may show a Hibernate SQL SELECT in the backend logs (cache miss).
- Second call: should be fast and not trigger the same SELECT (cache hit).

Note: your browser will still show two HTTP requests (caching is server-side). Use server logs to confirm whether the DB was hit.

### Quick troubleshooting

- If you still see SELECTs every time:
  - Check that the service method is annotated with `@Cacheable` and the cache name matches `CacheConfig`.
  - Make sure you're not evicting the cache somewhere else.
  - Remember the scheduled eviction clears caches every hour — test right away after a request.

- To see SQL logs for debugging, add temporarily to `application.properties`:

```
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

Remove or disable those settings after debugging — they produce a lot of output.

### How to add caching to other pages / methods (quick guide for teammates)

1) Pick what to cache

- Decide which service method returns data that is read frequently but changes rarely (example: `getResultsForConstituency`, `getAllMunicipalities`, or `getElectionSummary`).

2) Choose a cache name and add it to `CacheConfig` (optional but tidy)

- Open `src/main/java/.../config/CacheConfig.java` and add a public static final String constant for your cache, for example:

```java
public static final String CONSTITUENCY_RESULTS_CACHE = "constituencyResults";
```

This keeps names consistent across the project.

3) Annotate the service method with `@Cacheable`

- Put `@Cacheable(value = CacheConfig.CONSTITUENCY_RESULTS_CACHE)` on the service method that fetches the data.

Example:

```java
@Service
public class ConstituencyService {

  @Cacheable(value = CacheConfig.CONSTITUENCY_RESULTS_CACHE, key = "'const-' + #id")
  public ConstituencyResults getResultsForConstituency(String id) {
    // heavy DB operation
  }
}
```

- Notes about the key: you can let Spring use a default key (method arguments), or write a SpEL expression like `"'const-' + #id"` to make it explicit.

4) Evict or update cache after writes

- If you have a method that updates data (for instance, an import or edit), use `@CacheEvict` or `@CachePut` on that method so the cache stays correct.

Examples:

```java
// remove an entry after update
@CacheEvict(value = CacheConfig.CONSTITUENCY_RESULTS_CACHE, key = "'const-' + #id")
public void updateConstituencyResults(String id, NewData data) { ... }

// or update the cached value and return the new value
@CachePut(value = CacheConfig.CONSTITUENCY_RESULTS_CACHE, key = "'const-' + #id")
public ConstituencyResults saveAndReturnUpdated(String id, NewData data) { ... }
```

5) Rebuild and test

- Rebuild the backend and call the endpoint twice (see "How you can test caching locally" above). Confirm the DB is hit only once.

6) Watch out for self-invocation 

- Spring caching works via proxies. The method you annotate should be `public` and called from another bean for the proxy to apply. If a method in the same class calls the cached method (self-invocation), the cache interceptor is bypassed.
- If you need internal calls to be cached, either extract the cached logic into another `@Service` bean.

---
A few final tips

- Keep cached responses small where possible — serializing very large objects can be slow and memory-heavy.
- Prefer caching at the service layer (not the controller) so your controllers stay thin and logic is reusable.

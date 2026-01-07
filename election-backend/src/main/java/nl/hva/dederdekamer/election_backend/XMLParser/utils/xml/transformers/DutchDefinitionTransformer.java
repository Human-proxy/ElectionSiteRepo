package nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Election;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.DefinitionTransformer;

/**
 * Compact transformer for Verkiezingsdefinitie that works with the slim Election model.
 *
 * - Captures header (meta/basics/contest/seats/threshold) once.
 * - Builds a lightweight regions/committees tree.
 * - Collects Registered Parties (unique names).
 * - Stores everything under election.metadata["definition"] for the FE.
 *
 *   EMLHandler provides a flat map:
 *   attributes => "Tag-Attribute" (e.g., "Region-RegionCategory")
 *   plain tags  => "Tag"          (e.g., "RegionName")
 */
public class DutchDefinitionTransformer implements DefinitionTransformer {

    // ---- Lightweight DTOs for metadata.definition ----
    public static class Definition {
        public String id; // typically election id
        public Meta meta = new Meta();
        public Basics basics = new Basics();
        public Contest contest = new Contest();
        public Seats seats = new Seats();
        public ManagingAuthority managingAuthority = new ManagingAuthority();
        public List<String> parties = new ArrayList<>();
        public List<Region> regions = new ArrayList<>();
    }

    public static class Meta {
        public String emlId;
        public String schemaVersion;
        public String transactionId;
        public String issueDate;
        public String creationDateTime;
        public String canonicalizationAlgorithm;
    }

    public static class Basics {
        public String name;
        public String category;
        public String subcategory;
        public String date;
        public String nominationDate;
    }

    public static class Contest {
        public String id;
        public String name;
        public String method;
        public String maxVotes;
    }

    public static class Seats {
        public Integer numberOfSeats;
        public Integer preferenceThreshold;
    }

    public static class ManagingAuthority {
        public String id;
        public String address;
    }

    public static class Committee {
        public String category;
        public String name;
        public Boolean acceptCentralSubmissions;
    }

    public static class Region {
        public String category;
        public String number;
        public String name;
        public String superiorCategory;
        public String superiorNumber;
        public List<Committee> committees = new ArrayList<>();
        public List<Region> children = new ArrayList<>();
    }

    // ---- Transformer state ----
    private final Election election;
    private final Definition definition;
    private boolean headerDone = false;

    // For building region hierarchy quickly
    private final Map<String, Region> regionIndex = new HashMap<>();

    public DutchDefinitionTransformer(Election election) {
        this.election = election;
        this.definition = new Definition();
        this.definition.id = election.getId();
        // expose immediately so FE can see partial tree if needed
        this.election.addMetadata("definition", this.definition);
    }

    @Override
    public void registerRegion(Map<String, String> m) {
        // Capture header (first hit)
        if (!headerDone) {
            captureHeader(m);
            headerDone = true;
        }

        // Build/extend region
        String cat  = val(m, "Region-RegionCategory");
        String num  = val(m, "Region-RegionNumber");
        String name = val(m, "RegionName");
        String supC = val(m, "Region-SuperiorRegionCategory");
        String supN = val(m, "Region-SuperiorRegionNumber");

        Region region = null;
        if (cat != null || num != null || name != null) {
            String key = regionKey(cat, num);
            region = regionIndex.computeIfAbsent(key, k -> {
                Region r = new Region();
                r.category = cat;
                r.number = num;
                return r;
            });

            if (name != null) region.name = name;
            if (supC != null) region.superiorCategory = supC;
            if (supN != null) region.superiorNumber = supN;

            // ensure it is attached to the root list if it doesn't have a parent (yet)
            attachToTree(region);
        }

        // Optional committee info on this region
        String committeeCat = val(m, "Committee-CommitteeCategory");
        if (committeeCat != null && region != null) {
            Committee c = new Committee();
            c.category = committeeCat;
            c.name = val(m, "Committee-CommitteeName");
            c.acceptCentralSubmissions = parseBool(val(m, "Committee-AcceptCentralSubmissions"));
            // avoid duplicates by (category,name)
            if (region.committees.stream().noneMatch(
                    cc -> Objects.equals(cc.category, c.category) && Objects.equals(cc.name, c.name))) {
                region.committees.add(c);
            }
        }

        // ManagingAuthority may appear here
        setIf(m, "AuthorityIdentifier-Id", v -> definition.managingAuthority.id = v);
        setIf(m, "AuthorityAddress",       v -> definition.managingAuthority.address = v);
    }

    @Override
    public void registerParty(Map<String, String> m) {
        String name = val(m, "RegisteredAppellation");
        if (name != null) {
            // keep unique list
            if (definition.parties.stream().noneMatch(p -> p.equalsIgnoreCase(name))) {
                definition.parties.add(name);
            }
        }
    }

    // ---- helpers ----
    private void captureHeader(Map<String, String> m) {
        // meta
        setIf(m, "EML-Id",                    v -> definition.meta.emlId = v);
        setIf(m, "EML-SchemaVersion",         v -> definition.meta.schemaVersion = v);
        setIf(m, "TransactionId",             v -> definition.meta.transactionId = v);
        setIf(m, "IssueDate",                 v -> definition.meta.issueDate = v);
        setIf(m, "CreationDateTime",          v -> definition.meta.creationDateTime = v);
        setIf(m, "CanonicalizationMethod-Algorithm",
                                             v -> definition.meta.canonicalizationAlgorithm = v);

        // basics
        setIf(m, "ElectionName",              v -> { definition.basics.name = v; election.setName(v); });
        setIf(m, "ElectionCategory",          v -> definition.basics.category = v);
        setIf(m, "ElectionSubcategory",       v -> definition.basics.subcategory = v);
        setIf(m, "ElectionDate",              v -> { definition.basics.date = v; election.setDate(v); });
        setIf(m, "NominationDate",            v -> definition.basics.nominationDate = v);

        // contest
        setIf(m, "ContestIdentifier-Id",      v -> definition.contest.id = v);
        setIf(m, "ContestName",               v -> definition.contest.name = v);
        setIf(m, "VotingMethod",              v -> definition.contest.method = v);
        setIf(m, "MaxVotes",                  v -> definition.contest.maxVotes = v);

        // seats/threshold
        Integer seats = parseInt(val(m, "NumberOfSeats"));
        Integer thr   = parseInt(val(m, "PreferenceThreshold"));
        if (seats != null) definition.seats.numberOfSeats = seats;
        if (thr   != null) definition.seats.preferenceThreshold = thr;

        // managing authority (if present here)
        setIf(m, "AuthorityIdentifier-Id",    v -> definition.managingAuthority.id = v);
        setIf(m, "AuthorityAddress",          v -> definition.managingAuthority.address = v);
    }

    private void attachToTree(Region r) {
        // if it has a parent, link it under parent
        if (r.superiorCategory != null || r.superiorNumber != null) {
            String parentKey = regionKey(r.superiorCategory, r.superiorNumber);
            Region parent = regionIndex.get(parentKey);
            if (parent != null && parent.children.stream().noneMatch(ch -> ch == r)) {
                // ensure it is not sitting at root already
                definition.regions.remove(r);
                parent.children.add(r);
                return;
            }
        }
        // otherwise make sure it’s at root (only once)
        if (definition.regions.stream().noneMatch(rr -> rr == r)) {
            definition.regions.add(r);
        }
    }

    private static String regionKey(String cat, String num) {
        return (cat == null ? "" : cat.trim()) + "#" + (num == null ? "" : num.trim());
    }

    private static String val(Map<String,String> m, String k) {
        String v = m.get(k);
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    private static void setIf(Map<String,String> m, String k, Consumer<String> set) {
        String v = val(m, k);
        if (v != null) set.accept(v);
    }

    private static Integer parseInt(String s) {
        if (s == null || s.isBlank()) return null;
        try { 
            return Integer.valueOf(s.trim()); 
        } catch (NumberFormatException e) { 
            return null; 
        }
    }

    private static Boolean parseBool(String s) {
        if (s == null) return null;
        String v = s.trim().toLowerCase(Locale.ROOT);
        if (v.equals("true") || v.equals("yes")) return Boolean.TRUE;
        if (v.equals("false") || v.equals("no"))  return Boolean.FALSE;
        return null;
    }
}

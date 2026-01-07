# Map Datatype

## Map

The Abstract map datatype is a datatype where elements can be stored with a key-value pair. The key-value pair has a unique key for identification and a value. All these key-value pairs are stored together on the map. You can then use the key to manipulate the data, like retrieving the value associated with a certain keyThe Abstract map datatype is a datatype where elements can be stored with a key-value pair. The key-value pair has a unique key for identification and a value. All these key-value pairs are stored together on the map. You can then use the key to manipulate the data, like retrieving the value associated with a certain key

For example:
![image](https://gitlab.fdmci.hva.nl/semester-3-hbo-ict/onderwijs/student-projecten/2025-2026/out-s-t-se/quufoogeenee32/-/raw/tst/docs/Onderzoek/Milan/Images/Screenshot%202025-11-18%20040149.png?ref_type=heads)

In this example a container (the entry or also called key-value pair), has a label (the key) and contents (the value) 

Label (Key) | Contents (Value) | Pointers |
| --- | --- | --- |
| GreenFarms-1324 | Seeds | (memory address) |
| EnergyLLC-5687 | Solar cells | (memory address) |
| WholeMart-5793 | apples | (memory address) |

Let’s say we use get(Object key) and say we search the contents of the container with the key Greenfarms-1324. If you do this the function will return the value of that key which in this case is seeds.

## Thought experiment.

We have two teams working on their own ships with their own crew. Both of the teams are currently using a array like way of storing cargo. Meaning each time they want to store a container they go and check every spot on the ship for a empty one and place the container their (iterating over the array). And every time they wish to find a specific cargo container they have to iterate over every container until they find the correct one. Of course this is hugely inefficient. Instead both teams decide they will use new strategies for storing and deposing (deleting) containers on the ship.
Team 1 decides to base their strategy on a hash map.
Team 2 decides to base their strategy on a tree map.
Lets first show how team 1 stores their containers.

## Team 1

Team 1 will use something called a hash map. Remember that each container had a key? Rather than store them all in a line and iterating every time this team does something clever, we will generate a hash for every key and pick the location based on the result of the key. Imagine we take the Greenfarms-1324 key. And we turn this into a number that number can be the location or basically the index on a array that is our ship. Then every time someone wants to find this container. They simply preform the calculation and there is the exact index of that container. 
We created this short code that simply prompts the worker with the label and assigns it a location on the ship: 

![image](https://gitlab.fdmci.hva.nl/semester-3-hbo-ict/onderwijs/student-projecten/2025-2026/out-s-t-se/quufoogeenee32/-/raw/tst/docs/Onderzoek/Milan/Images/Screenshot%202025-11-17%20005418.png?ref_type=heads)

There is one problem with this method:

![image](https://gitlab.fdmci.hva.nl/semester-3-hbo-ict/onderwijs/student-projecten/2025-2026/out-s-t-se/quufoogeenee32/-/raw/tst/docs/Onderzoek/Milan/Images/Screenshot%202025-11-17%20005559.png?ref_type=heads)

 If we do this hash the range of keys is way to large. Just a 64-bit can result can go up to 18,446,744,073,709,551,616 locations (October 3, 2005 Copyright © 2001-5 by Erik D. Demaine and Charles E. Leiserson lecture mit) which is of course a unreasonable size for our ship (in real life coding an array of this size would require a absolutely ridiculous amount of memory allocation to work on a computer) in addition the result could be negative as well, instead of just simply using this number as the index we will use a hash function to map our number onto our array. The hash function guarantees that the number is in our array, so that every time we use it we generate an actual valid location.
 
Here's a code example:

![image](https://gitlab.fdmci.hva.nl/semester-3-hbo-ict/onderwijs/student-projecten/2025-2026/out-s-t-se/quufoogeenee32/-/raw/tst/docs/Onderzoek/Milan/Images/Screenshot%202025-11-17%20011225.png?ref_type=heads)

![image](https://gitlab.fdmci.hva.nl/semester-3-hbo-ict/onderwijs/student-projecten/2025-2026/out-s-t-se/quufoogeenee32/-/raw/tst/docs/Onderzoek/Milan/Images/Screenshot%202025-11-17%20011248.png?ref_type=heads)

The location of the greenfarms-1324 container is now a valid location on our ship. This means that our crew can begin loading the containers onto the ship. Yet they encounter another problem. When we try to map 18 quintillion locations onto just 20,000 many many different strings will result in the same location on the ship. This is simply due to the fact that its impossible to make a unique one for each hash. So we have encountered what is called in coding a Collision. When two or more items map onto the exact same index. We can solve this by Understanding and implementing the logic behind buckets in java. 
A bucket is simply the value we talked about earlier, but rather than holding a single value it has a collection of values.
Goodrich and Tamassia explain this concept clearly and why its applicable to our issue:
If the keys are integers well distributed in the range [0,N − 1], this bucket array is all that is needed. An entry e with key k is simply inserted into the bucket,
if our keys are unique integers in the range [0,N − 1], then each bucket holds at most one entry. Thus, searches, insertions, and removals in the bucket array take O(1) time. This sounds like a great achievement, but it has two drawbacks. First, the space used is proportional to N. Thus, if N is much larger than the number of entries n actually present in the map, we have a waste of space. The second draw back is that keys are required to be integers in the range [0, N − 1], which is often not the case. Because of these two drawbacks, we use the bucket array in conjunction with a "good" mapping from the keys to the integers in the range [0,N − 1].
 
-page 523, Goodrich, Tamassia. Data structures and algorithms in Java (4th ed)

The quote describes exactly the issue we are having. One simple solution (that java doesn’t use for reasons that will become apparent) is to use linear probing, simply going forward until a empty slot is found, this would be like the crew agreeing that if there’s already a container on that particular location to simply continue to the next empty spot. The next time someone tries to find it using the key they go over to that spot, check every container after until they find the right one, THIS IS NOT GOOD. As it leads to the same issue as we originally had in the very beginning of the thought experiment especially as the ship reaches capacity as collisions and thus clustering will become increasingly likely. Another possibility is using open addressing, but I will not handle these methods, instead we will use Separate Chaining. Imagine our thought experiment again. The crew will implement the bucket system from Goodrich and Tamassia now. The ship is separated into sections each section representing a bucket essentially a index on the array. When we use the Separate Chaining technique we will give every container a pointer to another container, a pointer is simply a value that is the memory address of another value in our example you might see it as chalk on a container with an arrow to the next container within the same bucket, if the container isn’t the one youre looking for simply follow the arrows until you find the exact one youre looking for. This is the essential logic behind their strategy, theres ofcourse more to hashmaps, for example a hashmap in jave automatically increases in size after a certain point, when creating the hashmap you include these two parameters, a loadfactor and initial capacity. The initial capacity is the amount of buckets made when created and the loadfactor is the percentage of the hashmap that has to be filled until it automatically dubbels in size (which in java is automatically 0.75)
An instance of HashMap has two parameters that affect its performance: initial capacity and load factor. The capacity is the number of buckets in the hash table, and the initial capacity is simply the capacity at the time the hash table is created. The load factor is a measure of how full the hash table is allowed to get before its capacity is automatically increased. When the number of entries in the hash table exceeds the product of the load factor and the current capacity, the hash table is rehashed (that is, internal data structures are rebuilt) so that the hash table has approximately twice the number of buckets.
As a general rule, the default load factor (.75) offers a good tradeoff between time and space costs. Higher values decrease the space overhead but increase the lookup cost (reflected in most of the operations of the HashMap class, including get and put). The expected number of entries in the map and its load factor should be taken into account when setting its initial capacity, so as to minimize the number of rehash operations. If the initial capacity is greater than the maximum number of entries divided by the load factor, no rehash operations will ever occur. - docs.oracle.com, Class HashMap

## Team 2

Team 2 handled the issue differently they decided to use the tree map, a type of structure based on the Red-Black Tree method. Unlike other trees the red-black tree method is suited for use in map/dictionaries.
trees have a number of nice properties, there are some dictionary applications for which they are not well suited… the red black tree, does not have these drawbacks, however, as it requires that only O(1) structural changes be made after an update in order to stay balanced.
-page 641, Goodrich, Tamassia. Data structures and algorithms in Java (4th ed)
So the team uses the tree structure based on alphabetical order they believe that this way they can eliminate half the options each time, significantly reducing the amount of container by half each time, the first container they put on the ship is EnergyLLC-5687, this container becomes the root, but a problem quickly emerges, most of the shipping containers placed after EnergyLLC-5687 form a long chain on the same side of the tree, meaning that they are no longer eliminating half the options significantly slowing them down. They come together realizing that it doesn’t work and they need some new rules to make sure that these containers remain balanced on the ship. They decide to base their solution on the rules of the red-black tree method, they buy a bunch of stickers either red or black, they agree on four rules.
1.	Root property: the very first container aka the root is black
2.	Every empty spot is considered black for the sake of balance.
3.	The children of red containers must be black.
4.	All empty spots (considered black) must have the same amount of black ancestors (minus the external node itself).

These so happen to be the four core rules to a red-black tree, but one rule ill add for simplicity is that 

5.	All new inserted containers are marked red.

One issue immediately becomes apparent, if all new inserted containers are indeed red the rule 3 the children of red containers must be black can never become true, but this is exactly what we want as making this rule correct is the exact method for balancing the tree map lets take it step by step by stacking a bunch of containers that would’ve caused a massive delay in our last method but is automatically resolved in this method: 

![image](https://gitlab.fdmci.hva.nl/semester-3-hbo-ict/onderwijs/student-projecten/2025-2026/out-s-t-se/quufoogeenee32/-/raw/tst/docs/Onderzoek/Milan/Images/Screenshot%202025-11-17%20072017.png?ref_type=heads)

Now lets try to add 25 here:

![image](https://gitlab.fdmci.hva.nl/semester-3-hbo-ict/onderwijs/student-projecten/2025-2026/out-s-t-se/quufoogeenee32/-/raw/tst/docs/Onderzoek/Milan/Images/Screenshot%202025-11-17%20072156.png?ref_type=heads)

We must now look at the cases for a solution. The cases form the basis for finding a solution
Since the uncle doesn’t exist we must assume that its black and by this logic we need to rotate

![image](https://gitlab.fdmci.hva.nl/semester-3-hbo-ict/onderwijs/student-projecten/2025-2026/out-s-t-se/quufoogeenee32/-/raw/tst/docs/Onderzoek/Milan/Images/Screenshot%202025-11-17%20073531.png?ref_type=heads)

And now our tree is resorted, just like that the ship is organized.
Finding a container like 7 is as easy as asking the following questions:

10 < 7?
No, go left 

4 < 7?
Yes, go right

And there is container 7.
This new way of inserting containers makes it so there will never be imbalance increasing the time to find the right container. This is essentially the core logic behind this strategy. Using the color system helps keep the tree from becoming unbalanced which means we get O(log n) time for finding containers.

## Comparison

Similarities:

Both hashmap and tree map extend the abstract map datatype from the very beginning, so both systems work on a key-value look up basis. Both methods are unsynchronized. In addition
But the difference between them is far more notable.

Differences:

|     | HashMap | TreeMap |
| --- | --- | --- |
| Time inset (average) | O(1) | O(log n) |
| Time search<br><br>(average) | O(1) | O(log n) |
| order | No order | Sorted order |
| Null allowed? | Yes, always as a value once as a key | Never |
| Best use case? | Very fast at finding a specific entry in the map | Very good at providing a sorted list |

# Hypothesis

Based on everything I have discussed I will make three predictions,

1. Inserting into the Hash map is considerably faster than a tree map, as the average time will be O(1) compared to the average time of the tree map which is O(log n)
2. Searching for any particular entry in a hash map is considerably easier in a Hash map since the hashing will immediately return a index instead of the tree that has to eliminate options slowly over time, increasing with each layer in the tree.
3. Finding large sorted information is considerably faster on a tree map than on a HashMap.

# Data

Insertions:

**1.000.000 entries**

| Runs | HashMap<br><br>Without resizing | HashMap<br><br>With resizing | TreeMap |
| --- | --- | --- | --- |
| 1   | 81ms | 142ms | 921ms |
| 2   | 79ms | 88ms | 846ms |
| 3   | 82ms | 90ms | 889ms |
| 4   | 72ms | 146ms | 783ms |
| 5   | 90ms | 84ms | 893ms |
| 6   | 42ms | 87ms | 797ms |
| 7   | 91ms | 164ms | 893ms |
| 8   | 39ms | 109ms | 835ms |
| 9   | 74ms | 83ms | 880ms |
| 10  | 36ms | 91ms | 950ms |
| **Total** | **686ms** | **1.084ms** | **8.687ms** |

**100.000 entries**

| Runs | Hashmap<br><br>Without resizing | Hashmap<br><br>With resizing | Treemap |
| --- | --- | --- | --- |
| 1   | 18ms | 8ms | 40ms |
| 2   | 13ms | 11ms | 49ms |
| 3   | 17ms | 12ms | 63ms |
| 4   | 5ms | 10ms | 45ms |
| 5   | 5ms | 10ms | 41ms |
| 6   | 5ms | 10ms | 38ms |
| 7   | 17ms | 29ms | 51ms |
| 8   | 4ms | 8ms | 68ms |
| 9   | 4ms | 8ms | 66ms |
| 10  | 4ms | 8ms | 54ms |
| **Total** | **92ms** | **114ms** | **515ms** |

**10.000 entries**

| Runs | HashMap<br><br>Without resizing | HashMap<br><br>With resizing | TreeMap |
| --- | --- | --- | --- |
| 1   | 568µs | 426µs | 2175µs |
| 2   | 615µs | 601µs | 1955µs |
| 3   | 530µs | 748µs | 1913µs |
| 4   | 541µs | 678µs | 2679µs |
| 5   | 530µs | 773µs | 3428µs |
| 6   | 520µs | 483µs | 2033µs |
| 7   | 495µs | 443µs | 1680µs |
| 8   | 486µs | 450µs | 3906µs |
| 9   | 232µs | 461µs | 1622µs |
| 10  | 220µs | 443µs | 1516µs |
| **Total** | **4.737µs** | **5.063µs** | **22.907µs** |

Retrieve entries:

**1.000.000 entries**

| Runs | HashMap<br><br>Without resizing | HashMap<br><br>With resizing | TreeMap |
| --- | --- | --- | --- |
| 1   | 29ms | 38ms | 682ms |
| 2   | 26ms | 23ms | 855ms |
| 3   | 21ms | 20ms | 828ms |
| 4   | 27ms | 26ms | 681ms |
| 5   | 21ms | 18ms | 890ms |
| 6   | 28ms | 18ms | 833ms |
| 7   | 23ms | 33ms | 694ms |
| 8   | 24ms | 16ms | 843ms |
| 9   | 23ms | 17ms | 844ms |
| 10  | 27ms | 17ms | 704ms |
| **Total** | **249ms** | **226ms** | **7.854ms** |

**100.000 entries**

| Runs | HashMap<br><br>Without resizing | HashMap<br><br>With resizing | TreeMap |
| --- | --- | --- | --- |
| 1   | 2ms | 3ms | 35ms |
| 2   | 1ms | 7ms | 36ms |
| 3   | 1ms | 1ms | 33ms |
| 4   | 2ms | 1ms | 33ms |
| 5   | 1ms | 1ms | 37ms |
| 6   | 1ms | 1ms | 32ms |
| 7   | 1ms | 1ms | 31ms |
| 8   | 2ms | 1ms | 28ms |
| 9   | 1ms | 1ms | 32ms |
| 10  | 1ms | 1ms | 30ms |
| **Total** | **13ms** | **18ms** | **327ms** |

**10.000 entries**

| Runs | Hashmap<br><br>Without resizing | Hashmap<br><br>With resizing | Treemap |
| --- | --- | --- | --- |
| 1   | 353µs | 94µs | 2407µs |
| 2   | 333µs | 83µs | 1499µs |
| 3   | 347µs | 84µs | 1224µs |
| 4   | 331µs | 92µs | 1310µs |
| 5   | 294µs | 90µs | 1211µs |
| 6   | 314µs | 88µs | 1249µs |
| 7   | 124µs | 90µs | 1229µs |
| 8   | 101µs | 88µs | 1238µs |
| 9   | 105µs | 85µs | 1200µs |
| 10  | 116µs | 81µs | 1259µs |
| **Total** | **2.418µs** | **709µs** | **11.367µs** |

Range entries A-D :

**1.000.000 entries (57973)**

| Runs | HashMap<br><br>Without resizing | HashMap<br><br>With resizing | TreeMap |
| --- | --- | --- | --- |
| 1   | 66ms | 73ms | 6ms |
| 2   | 74ms | 67ms | 3ms |
| 3   | 63ms | 72ms | 2ms |
| 4   | 63ms | 63ms | 2ms |
| 5   | 69ms | 73ms | 1ms |
| 6   | 60ms | 66ms | 1ms |
| 7   | 60ms | 73ms | 1ms |
| 8   | 67ms | 65ms | 1ms |
| 9   | 67ms | 76ms | 1ms |
| 10  | 62ms | 70ms | 0ms |
| **Total** | **651ms** | **698ms** | **18ms** |

**100.000 entries (5704)**

| Runs | HashMap<br><br>Without resizing | HashMap<br><br>With resizing | TreeMap |
| --- | --- | --- | --- |
| 1   | 11ms | 2ms | 1656µs |
| 2   | 6ms | 5ms | 805µs |
| 3   | 9ms | 3ms | 6918µs |
| 4   | 9ms | 2ms | 372µs |
| 5   | 7ms | 5ms | 99µs |
| 6   | 6ms | 3ms | 94µs |
| 7   | 5ms | 3ms | 98µs |
| 8   | 6ms | 3ms | 98µs |
| 9   | 6ms | 2ms | 105µs |
| 10  | 5ms | 2ms | 96µs |
| **Total** | **70ms** | **30ms** | **10.341µs** |

**10.000 entries (569)**

| Runs | Hashmap<br><br>Without resizing | Hashmap<br><br>With resizing | Treemap |
| --- | --- | --- | --- |
| 1   | 1858µs | 352µs | 973µs |
| 2   | 1575µs | 332µs | 76µs |
| 3   | 1133µs | 274µs | 54µs |
| 4   | 942µs | 255µs | 43µs |
| 5   | 910µs | 267µs | 43µs |
| 6   | 907µs | 264µs | 42µs |
| 7   | 899µs | 258µs | 45µs |
| 8   | 948µs | 251µs | 79µs |
| 9   | 290µs | 244µs | 57µs |
| 10  | 311µs | 377µs | 38µs |
| **Total** | **9773µs** | **2874µs** | **1450µs** |

Range entries Aa-Ad:

**1.000.000 entries (1089)**

| Runs | HashMap<br><br>Without resizing | HashMap<br><br>With resizing | TreeMap |
| --- | --- | --- | --- |
| 1   | 71ms | 118ms | 868µs |
| 2   | 68ms | 97ms | 188µs |
| 3   | 61ms | 109ms | 82µs |
| 4   | 60ms | 106ms | 77µs |
| 5   | 59ms | 79ms | 84µs |
| 6   | 61ms | 69ms | 81µs |
| 7   | 64ms | 69ms | 81µs |
| 8   | 88ms | 66ms | 79µs |
| 9   | 88ms | 63ms | 176µs |
| 10  | 94ms | 66ms | 90µs |
| **Total** | **714ms** | **842ms** | **1.806µs** |

**100.000 entries (97)**

| Runs | HashMap<br><br>Without resizing | HashMap<br><br>With resizing | TreeMap |
| --- | --- | --- | --- |
| 1   | 12ms | 2ms | 1867µs |
| 2   | 9ms | 4ms | 29µs |
| 3   | 6ms | 2ms | 20µs |
| 4   | 6ms | 2ms | 31µs |
| 5   | 4ms | 4ms | 21µs |
| 6   | 4ms | 2ms | 17µs |
| 7   | 4ms | 2ms | 5968µs |
| 8   | 4ms | 2ms | 21µs |
| 9   | 4ms | 2ms | 12µs |
| 10  | 4ms | 2ms | 12µs |
| **Total** | **57ms** | **24ms** | **2.030µs** |

**10.000 entries (5)**

| Runs | Hashmap<br><br>Without resizing | Hashmap<br><br>With resizing | Treemap |
| --- | --- | --- | --- |
| 1   | 2159µs | 326µs | 849µs |
| 2   | 1595µs | 417µs | 6µs |
| 3   | 1059µs | 286µs | 4µs |
| 4   | 972µs | 341µs | 3µs |
| 5   | 955µs | 323µs | 4µs |
| 6   | 907µs | 331µs | 4µs |
| 7   | 981µs | 351µs | 4µs |
| 8   | 935µs | 296µs | 4µs |
| 9   | 578µs | 243µs | 3µs |
| 10  | 274µs | 254µs | 4µs |
| **Total** | **10.415µs** | **3.168µs** | **40µs** |

# Conclusion

The data aligns with the hypothesis, the HashMap is more efficient at insertions and finding specific entries, while the tree map is most efficient at finding a range of entries. Also interesting to note is that the HashMap with resizing is faster then the entries are low, most likely due to being more efficient with allocated capacity.
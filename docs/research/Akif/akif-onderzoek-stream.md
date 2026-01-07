# Java Stream

Stream is a powerful feature in Java that allows developers to process data in a variety of ways (Oracle, 2024a). Those
include filtering, mapping, and reducing data. Streams can be created from various data sources, such as collections,
arrays, or I/O channels.

## Short Introduction

In modern Java programming, streams are used to process data in a simple and efficient way. Streams were introduced in
Java 8 in 2014 and since then are available in all newer Java versions, including Java 21. Streams are used in many
types of applications, ranging from hobby projects to large enterprise systems.

## What is a Stream?

A **Stream** in Java is a sequence of elements supporting functional-style operations to perform bulk data processing.
Streams provide a high-level, declarative way to express computations on collections and other data sources without
explicit iteration (Oracle, 2024a).

## Key Characteristics of Streams

Streams have several key characteristics that make them powerful

- **No Storage**: Streams do not store data. They convey elements from a data source such as a collection, array, or I/O
  channel.
- **Functional in Nature**: Streams support functional-style operations that allow concise and readable data processing.
- **Lazy Evaluation**: Intermediate operations are not executed until a terminal operation is invoked. This improves
  efficiency, especially for large data sets.
- **Parallelizable**: Streams can be processed using parallel execution, leveraging multicore processors via
  `parallelStream()` or `stream().parallel()` (Oracle, 2024b).

## Types of Streams

There are two main types of streams in Java:

### 1. Sequential Streams

Sequential streams process elements one by one using a single thread. Any stream created without specifying parallel
behavior is sequential by default.

### 2. Parallel Streams

Parallel streams split the workload across multiple CPU cores, potentially improving performance. Each segment of the
data is processed independently, and the results are combined at the end (GeeksforGeeks, 2024).  
However, parallel execution may produce unpredictable ordering and can introduce complexity.

## Common Stream Operations

Stream operations fall into two categories: **intermediate** and **terminal** operations (Oracle, 2024a).

- **Intermediate operations** build a processing pipeline and return another stream. They are lazy and only run when a
  terminal operation triggers them.  
  Examples: `filter()`, `map()`, `sorted()`

- **Terminal operations** produce a final result, such as a collection, count, or output. After a terminal operation,
  the stream is closed.  
  Examples: `collect()`, `forEach()`, `count()`

This design allows efficient and readable processing in a single chain.

## Example Code

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<Integer> evenNumbers = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());

        System.out.println("Even Numbers: " + evenNumbers);
    }
}

```

This example filters even numbers from a list. The filter() method applies a condition to each element, and the
collect() method gathers the results into a list:
Output: [2, 4, 6, 8, 10]

## Conclusion

Java Streams offer a powerful and clean way to process data. They reduce boilerplate code, improve readability, and can
take advantage of multicore processors through parallel execution. They are widely used in modern Java development due
to their flexibility, performance, and functional design.

## Sources (APA)

- Oracle. (2024a). Java Platform, Standard Edition 21 API Specification: Stream Interface. Retrieved
from https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html
- Oracle. (2024b). Processing Data with Java Streams. Retrieved
from https://docs.oracle.com/javase/tutorial/collections/streams/index.html
- W3Schools. (n.d.). Java Streams. Retrieved November 10, 2025, from https://www.w3schools.com/java/java_stream.asp
- GeeksforGeeks. (2024). Stream in Java. Retrieved from https://www.geeksforgeeks.org/stream-in-java/
- Bloch, J. (2018). Effective Java (3rd ed.). Addison-Wesley.
import java.util.*;
import java.util.stream.*;

public class StreamDemo {
    public static void main(String[] args) {
        // Maak een lijst met 10 miljoen getallen (0 t/m 9.999.999)
        List<Integer> numbers = IntStream.range(0, 10_000_000).boxed().toList();

        System.out.println("Vergelijking van for-loop, stream() en parallelStream():");
        System.out.println("--------------------------------------------------------");
        long start = System.nanoTime();
        long count = 0;
        for (int n : numbers) {
            if (n % 2 == 0) count++;
        }
        long end = System.nanoTime();
        System.out.println("For-loop filter time = " + (end - start)/1_000_000 + " ms");

        long startS = System.nanoTime();
        long countS = numbers.stream().filter(n -> n % 2 == 0).count();
        long endS = System.nanoTime();
        System.out.println("Stream filter time = " + (endS - startS)/1_000_000 + " ms");

        long startP = System.nanoTime();
        long countP = numbers.parallelStream().filter(n -> n % 2 == 0).count();
        long endP = System.nanoTime();
        System.out.println("Parallel filter time = " + (endP - startP)/1_000_000 + " ms");
    }
}


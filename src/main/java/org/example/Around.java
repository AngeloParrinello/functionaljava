package org.example;

import java.util.Date;
import java.util.function.Supplier;

public class Around {

    // 1. problem statement: dover eseguire qualcosa "prima" e  qualcosa "dopo" e dobbiamo "ricordarci di farlo". Esempi:
    //    - performance tracing:
    //    - transactions
    //    - MDC for logging
    //    - multi tenant

    // 2. solution: Runnable lambda

    // 3. return values: e se la funzione dovesse ritornare un valore?
    //    solution: Supplier

    public static void main(String[] args) {
        long start = new Date().getTime();
        System.out.println("Hello World!");

        long end = new Date().getTime();
        System.out.println("Time taken: " + (end - start) + "ms");
    }

    public static void solution() {
        traceTime(() -> System.out.println("Hello World!"));
    }

    public static void traceTime(Runnable r) {
        long start = new Date().getTime();
        try {
            r.run();
        } finally {
            long end = new Date().getTime();
            System.out.println("Time taken: " + (end - start) + "ms");
        }
    }

    public static <T> T traceTime(Supplier<T> s) {
        long start = new Date().getTime();
        try {
            return s.get();
        } finally {
            long end = new Date().getTime();
            System.out.println("Time taken: " + (end - start) + "ms");
        }
    }

}

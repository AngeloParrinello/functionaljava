package workingsoftware;

import java.util.Date;

/*
 * Eseguire qualcosa "prima" e qualcosa "dopo" e dobbiamo "ricordarci di farlo"
 *      - time tracking
 *      - transactions
 *      - MDC logging
 *      - multi-tenant / thread-local variables
 */
public class WrapAround_002 {

    public static void main(String[] args) {
        long start = new Date().getTime();

        longComputation();

        long end = new Date().getTime();
        System.out.println("Time taken: " + (end - start) + "ms");
    }

    private static long longComputation() {
        try {
            long sleepTime = (long) (Math.random() * 200);
            Thread.sleep(sleepTime);
            System.out.println("This will take a while...");
            return sleepTime;
        } catch (InterruptedException e) {
            System.out.println("ouch...");
            return 0;
        }
    }

}

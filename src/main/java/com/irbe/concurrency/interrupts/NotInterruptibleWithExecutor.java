package com.irbe.concurrency.interrupts;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;
import static java.lang.Thread.sleep;

public final class NotInterruptibleWithExecutor {
    public static void main(final String[] args) {
        out.println("Non-interruptible thread test.");
        out.println("Starting T1!");
        try (final ExecutorService pool = Executors.newFixedThreadPool(1)) {
            final Future<?> t2 = pool.submit(() -> {
                try {
                    out.println("T2 started!");
                    sleep(1000L);
                    out.println("T2 finished!");
                } catch (final InterruptedException e) {
                    out.println("T2 interrupted!");
                }
            });
            pool.awaitTermination(500L, TimeUnit.MILLISECONDS);
            t2.cancel(true);
        } catch (final InterruptedException e) {
            out.println("T1 interrupted!");
        }
    }
}

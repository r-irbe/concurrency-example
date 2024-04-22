package com.irbe.concurrency.join;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.err;
import static java.lang.System.out;
import static java.lang.Thread.sleep;

public final class JoinUnstructured {
    public static void main(final String[] args) {
        out.println("Non-interruptible thread test.");
        out.println("Starting T1!");
        final var sum = new AtomicInteger(0);
        try {
            final Thread.UncaughtExceptionHandler exHandler = (t, e) -> {
                err.println(STR."Exception for thread \{t.getName()} with \{e.getMessage()}");
                e.printStackTrace();
            };
            final var t2 = new Thread(() -> {
                try {
                    out.println("T2 started!");
                    sleep(1000L);
                    sum.addAndGet(1);
                    out.println("T2 finished!");
                } catch (final InterruptedException e) {
                    out.println("T2 interrupted!");
                }
            }, "T2");
            t2.start();
            t2.setUncaughtExceptionHandler(exHandler);
            final var t3 = new Thread(() -> {
                try {
                    out.println("T3 started!");
                    sleep(1000L);
                    sum.addAndGet(2);
                    out.println("T3 finished!");
                } catch (final InterruptedException e) {
                    out.println("T3 interrupted!");
                }
            }, "T3");
            t3.start();
            t3.setUncaughtExceptionHandler(exHandler);
            t2.join();
            t3.join();
        } catch (final InterruptedException e) {
            out.println("T1 interrupted!");
        }
        out.println(STR."Sum: \{sum.get()}");
    }
}

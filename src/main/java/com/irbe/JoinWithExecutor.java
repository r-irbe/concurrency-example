package com.irbe;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.out;
import static java.lang.Thread.sleep;

public final class JoinWithExecutor {
    public static void main(final String[] args) {
        out.println("Non-interruptible thread test.");
        final var sum = new AtomicInteger(0);
        try (final ExecutorService pool = Executors.newFixedThreadPool(2)) {
            out.println("Starting T1!");
            final CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
                try {
                    out.println("T2 started!");
                    sleep(600L);
                    sum.addAndGet(1);
                    out.println("T2 finished!");
                } catch (final InterruptedException e) {
                    out.println("T2 interrupted!");
                }
            }, pool);
            final CompletableFuture<Void> f3 = CompletableFuture.runAsync(() -> {
                try {
                    out.println("T3 started!");
                    sleep(1000L);
                    sum.addAndGet(2);
                    out.println("T3 finished!");
                } catch (final InterruptedException e) {
                    out.println("T3 interrupted!");
                }
            }, pool);
            CompletableFuture.allOf(f2, f3)
                    .handle((v, e) -> {
                        e.printStackTrace();
                        return v;
                    }).orTimeout(500L, TimeUnit.MILLISECONDS)
                    .join();
            out.println(STR."Sum: \{sum.get()}");
        }
    }
}

package com.irbe;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.out;
import static java.lang.Thread.sleep;

public final class JoinStructured {
    public static void main(final String[] args) {
        out.println("Non-interruptible thread test.");
        final var sum = new AtomicInteger(0);
        final Thread.Builder.OfVirtual builder = Thread.ofVirtual().inheritInheritableThreadLocals(true)
                .uncaughtExceptionHandler((t, e) -> e.printStackTrace(System.err));
        final ThreadFactory factory = builder.factory();
        final Collection<StructuredTaskScope.Subtask<?>> tasks = new ArrayList<>();
        try (final var scope = new StructuredTaskScope.ShutdownOnFailure("scope 1", factory)) {
            out.println("Starting T1!");
            final StructuredTaskScope.Subtask<?> t2 = scope.fork(() -> {
                try {
                    out.println("T2 started!");
                    sleep(600L);
                    sum.addAndGet(1);
                    out.println("T2 finished!");
                    return true;
                } catch (final InterruptedException e) {
                    out.println("T2 interrupted!");
                    return false;
                }
            });
            final StructuredTaskScope.Subtask<?> t3 = scope.fork(() -> {
                try {
                    out.println("T3 started!");
                    sleep(1000L);
                    sum.addAndGet(2);
                    out.println("T3 finished!");
                    return true;
                } catch (final InterruptedException e) {
                    out.println("T3 interrupted!");
                    return false;
                }
            });
            tasks.add(t2);
            tasks.add(t3);
            scope.joinUntil(Instant.now().plus(500L, ChronoUnit.MILLIS));
        } catch (final InterruptedException | TimeoutException e) {
            out.println("T1 interrupted!");
        }
        for (final StructuredTaskScope.Subtask<?> t : tasks) {
            out.println(STR."subtask state: \{t.state()}");
        }
        out.println(STR."Sum: \{sum.get()}");
    }
}

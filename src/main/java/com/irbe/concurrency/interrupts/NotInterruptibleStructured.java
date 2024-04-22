package com.irbe.concurrency.interrupts;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadFactory;

import static java.lang.System.out;
import static java.lang.Thread.sleep;

public final class NotInterruptibleStructured {
    public static void main(final String[] args) {
        out.println("Non-interruptible thread test.");
        out.println("Starting T1!");
        final Thread.Builder.OfVirtual builder = Thread.ofVirtual().inheritInheritableThreadLocals(true)
                .uncaughtExceptionHandler((t, e) -> e.printStackTrace(System.err));
        final ThreadFactory factory = builder.factory();
        StructuredTaskScope.Subtask<Boolean> t2 = null;
        try (final var scope = new StructuredTaskScope.ShutdownOnFailure("scope 1", factory)) {
            t2 = scope.fork(() -> {
                try {
                    out.println("T2 started!");
                    sleep(1000L);
                    out.println("T2 done sleeping!");
                    out.println("T2 finished!");
                    return true;
                } catch (final InterruptedException e) {
                    out.println("T2 interrupted!");
                    return false;
                }
            });
            throw new InterruptedException();
        } catch (final InterruptedException e) {
            out.println("T1 interrupted!");
        }
        if (null != t2) {
            out.println(STR."T2 state: \{t2.state()}");
        }
    }
}

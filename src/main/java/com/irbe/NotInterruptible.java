package com.irbe;

import static java.lang.System.out;
import static java.lang.Thread.sleep;

public final class NotInterruptible {
    public static void main(final String[] args) {
        out.println("Non-interruptible thread test.");
        out.println("Starting T1!");
        try {
            final var t = new Thread(() -> {
                try {
                    out.println("T2 started!");
                    sleep(1000L);
                    out.println("T2 finished!");
                } catch (final InterruptedException e) {
                    out.println("T2 interrupted!");
                }
            }, "T2");
            t.start();
            throw new InterruptedException();
        } catch (InterruptedException e) {
            out.println("T1 interrupted!");
        }
    }
}

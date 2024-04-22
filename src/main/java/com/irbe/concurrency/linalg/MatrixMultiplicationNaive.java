package com.irbe.concurrency.linalg;

import java.util.concurrent.CountDownLatch;

public class MatrixMultiplicationNaive {
    public static void main(final String[] args) throws InterruptedException {
        //generate matrix
        final var N = 1_000;
        final var M = 6_000;
        final var countdown = new CountDownLatch(N);
        final int[][] a = new int[N][M];
        for (int i = 0; i < N; i++) {
            for (int j = 0; M > j; j++) {
                a[i][j] = i + j;
            }
        }
        final int[][] b = new int[M][N];
        for (int i = 0; M > i; i++) {
            for (int j = 0; j < N; j++) {
                b[i][j] = i + j;
            }
        }
        final int[][] c = new int[N][N];
        for (int i = 0; i < N; i++) {
            final int threadId = i;
            final var name = STR."thread-\{threadId}";
            new Thread(() -> {
                System.out.println("Starting " + name);
                countdown.countDown();
                for (int j = 0; j < N; j++) {
                    for (int k = 0; M > k; k++) {
                        c[threadId][j] += a[threadId][k] * b[k][j];
                    }
                }
                System.out.println("Finishing " + name);
            }, name).start();
        }
        countdown.await();
    }
}

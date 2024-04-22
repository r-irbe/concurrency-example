package com.irbe.concurrency.linalg;

import java.util.concurrent.CountDownLatch;

public class MatrixMultiplicationWithVirtualThreads {
    public static void main(final String[] args) throws InterruptedException {
        //generate matrix
        var N = 1_000;
        var M = 1_000;
        CountDownLatch countdown = new CountDownLatch(N);
        final int[][] a = new int[N][M];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j <M; j++) {
                a[i][j] = i + j;
            }
        }
        final int[][] b = new int[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j <N; j++) {
                b[i][j] = i + j;
            }
        }
        final int[][] c = new int[N][N];
        for (int i = 0; i < N; i++) {
            final int finalI = i;
            new Thread(() -> {
                countdown.countDown();
                for (int j = 0; j < N; j++) {
                    for (int k = 0; k < M; k++) {
                        c[finalI][j] += a[finalI][k] * b[k][j];
                    }
                }
            }).start();
        }
        countdown.await();
    }
}

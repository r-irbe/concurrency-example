package com.irbe.concurrency.linalg;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class MatrixMultiplicationWithForkJoin {
    public static void main(final String[] args) throws InterruptedException {
        //generate matrix
        var N = 1_000;
        var M = 1_000;
        var cores = Runtime.getRuntime().availableProcessors();

        var pool = new ForkJoinPool(cores);
        ForkJoinPool.commonPool().execute(() -> {
            System.out.println("Hello world!");
        });
        pool.submit(new ForkJoinTask<Object>() {
            @Override
            public Object getRawResult() {
                return null;
            }

            @Override
            protected void setRawResult(Object value) {

            }

            @Override
            protected boolean exec() {
                return false;
            }
        }).fork()
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

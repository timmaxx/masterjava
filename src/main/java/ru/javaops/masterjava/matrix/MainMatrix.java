package ru.javaops.masterjava.matrix;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainMatrix {
    private static final int MATRIX_SIZE = 2000;
    private static final int THREAD_NUMBER = 4;
    private static final int COUNT_OF_PASS = 2;

    private final static ExecutorService executor = Executors.newFixedThreadPool(MainMatrix.THREAD_NUMBER);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final int[][] matrixA = MatrixUtil.create(MATRIX_SIZE);
        final int[][] matrixB = MatrixUtil.create(MATRIX_SIZE);

        //double singleThreadSum = 0.;
        double concurrentThreadSum = 0.;
        double Sum_concurrentMultiply = 0.;
        double Sum_concurrentMultiply2 = 0.;
        double Sum_singleThreadMultiplyOpt2 = 0.;
        int count = 1;
        while (count <= COUNT_OF_PASS) {
            System.out.println("Pass " + count);
            long start;
            double duration;
/*
            start = System.currentTimeMillis();
            final int[][] matrixC = MatrixUtil.singleThreadMultiplyOpt(matrixA, matrixB);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Single thread time, sec: %.3f", duration);
            singleThreadSum += duration;
*/
            start = System.currentTimeMillis();
            final int[][] concurrentMatrixC = MatrixUtil.concurrentMultiplyStreams(matrixA, matrixB, Runtime.getRuntime().availableProcessors() - 1);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Concurrent thread time, sec: %.3f", duration);
            concurrentThreadSum += duration;

            final int[][] matrixC = concurrentMatrixC;

            // concurrentMultiply
            start = System.currentTimeMillis();
            final int[][] MatrixC_concurrentMultiply = MatrixUtil.concurrentMultiply(matrixA, matrixB, executor);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Concurrent thread time, sec: %.3f", duration);
            Sum_concurrentMultiply += duration;

            // concurrentMultiply2
            start = System.currentTimeMillis();
            final int[][] MatrixC_concurrentMultiply2 = MatrixUtil.concurrentMultiply2(matrixA, matrixB, executor);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Concurrent thread time, sec: %.3f", duration);
            Sum_concurrentMultiply2 += duration;

            // singleThreadMultiplyOpt2
            start = System.currentTimeMillis();
            final int[][] MatrixC_singleThreadMultiplyOpt2 = MatrixUtil.singleThreadMultiplyOpt2(matrixA, matrixB);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Single thread time, sec: %.3f", duration);
            Sum_singleThreadMultiplyOpt2 += duration;

            /*if (!MatrixUtil.compare(matrixC, concurrentMatrixC)) {
                System.err.println("Comparison failed");
                break;
            } else */if (!MatrixUtil.compare(matrixC, MatrixC_concurrentMultiply)) {
                System.err.println("Comparison failed");
                break;
            } if (!MatrixUtil.compare(matrixC, MatrixC_concurrentMultiply2)) {
                System.err.println("Comparison failed");
                break;
            } if (!MatrixUtil.compare(matrixC, MatrixC_singleThreadMultiplyOpt2)) {
                System.err.println("Comparison failed");
                break;
            }
            count++;
        }
        count--;
        executor.shutdown();
        //out("\nAverage single thread time (singleThreadMultiplyOpt), sec: %.3f", singleThreadSum / count);
        out("Average concurrent thread time (concurrentMultiplyStreams), sec: %.3f", concurrentThreadSum / count);

        out("Average concurrent thread time (concurrentMultiply), sec: %.3f", Sum_concurrentMultiply / count);
        out("Average concurrent thread time (concurrentMultiply2), sec: %.3f", Sum_concurrentMultiply2 / count);
        out("Average single thread time (singleThreadMultiplyOpt2), sec: %.3f", Sum_singleThreadMultiplyOpt2 / count);

        /*
        THREAD_NUMBER = 1
        Average single thread time (singleThreadMultiplyOpt), sec: 1.236
        Average concurrent thread time (concurrentMultiplyStreams), sec: 0.100
        Average concurrent thread time (concurrentMultiply), sec: 0.380
        Average concurrent thread time (concurrentMultiply2), sec: 0.260
        Average single thread time (singleThreadMultiplyOpt2), sec: 0.708
        */

        /*
        THREAD_NUMBER = 2
        Average single thread time (singleThreadMultiplyOpt), sec: 1.260
        Average concurrent thread time (concurrentMultiplyStreams), sec: 0.109
        Average concurrent thread time (concurrentMultiply), sec: 0.205
        Average concurrent thread time (concurrentMultiply2), sec: 0.150
        Average single thread time (singleThreadMultiplyOpt2), sec: 0.716
        */

        /*
        THREAD_NUMBER = 4
        Average single thread time (singleThreadMultiplyOpt), sec: 1.276
        Average concurrent thread time (concurrentMultiplyStreams), sec: 0.112
        Average concurrent thread time (concurrentMultiply), sec: 0.119
        Average concurrent thread time (concurrentMultiply2), sec: 0.101
        Average single thread time (singleThreadMultiplyOpt2), sec: 0.713
        */

        /*
        THREAD_NUMBER = 10
        Average single thread time (singleThreadMultiplyOpt), sec: 1.250
        Average concurrent thread time (concurrentMultiplyStreams), sec: 0.113
        Average concurrent thread time (concurrentMultiply), sec: 0.108
        Average concurrent thread time (concurrentMultiply2), sec: 0.113
        Average single thread time (singleThreadMultiplyOpt2), sec: 0.708
        */

        /*
        THREAD_NUMBER = 100
        Average single thread time (singleThreadMultiplyOpt), sec: 1.392
        Average concurrent thread time (concurrentMultiplyStreams), sec: 0.113
        Average concurrent thread time (concurrentMultiply), sec: 0.147
        Average concurrent thread time (concurrentMultiply2), sec: 0.133
        Average single thread time (singleThreadMultiplyOpt2), sec: 0.682
        */

        /*
        THREAD_NUMBER = 2
        MATRIX_SIZE = 2000
        COUNT_OF_PASS = 2
        Average concurrent thread time (concurrentMultiplyStreams), sec: 1.530
        Average concurrent thread time (concurrentMultiply), sec: 2.268
        Average concurrent thread time (concurrentMultiply2), sec: 1.805
        Average single thread time (singleThreadMultiplyOpt2), sec: 11.139
        */

        /*
        THREAD_NUMBER = 4
        MATRIX_SIZE = 2000
        COUNT_OF_PASS = 2
        Average concurrent thread time (concurrentMultiplyStreams), sec: 1.297
        Average concurrent thread time (concurrentMultiply), sec: 1.366
        Average concurrent thread time (concurrentMultiply2), sec: 1.057
        Average single thread time (singleThreadMultiplyOpt2), sec: 10.988
        */

        /*
        THREAD_NUMBER = 10
        MATRIX_SIZE = 2000
        COUNT_OF_PASS = 2
        Average single thread time (singleThreadMultiplyOpt), sec: 19.561
        Average concurrent thread time (concurrentMultiplyStreams), sec: 1.235
        Average concurrent thread time (concurrentMultiply), sec: 1.269
        Average concurrent thread time (concurrentMultiply2), sec: 1.098
        Average single thread time (singleThreadMultiplyOpt2), sec: 10.815
        */
    }

    private static void out(String format, double ms) {
        System.out.println(String.format(format, ms));
    }
}

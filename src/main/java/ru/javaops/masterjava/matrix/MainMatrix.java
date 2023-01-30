package ru.javaops.masterjava.matrix;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainMatrix {
    private static final int MATRIX_SIZE = 1000;
    private static final int THREAD_NUMBER = 10;

    private static final int COUNT_OF_PASS = 5;

    private final static ExecutorService executor = Executors.newFixedThreadPool(MainMatrix.THREAD_NUMBER);

    public static void main(String[] args) {
        // final int[][] matrixA = {{5, 1}, {5, 9}};
        // final int[][] matrixB = {{0, 5}, {8, 9}};
        // final int[][] matrixC = {{8, 34}, {72, 106}}; // Ожидаемый результат

        final int[][] matrixA = MatrixUtil.create(MATRIX_SIZE);
        final int[][] matrixB = MatrixUtil.create(MATRIX_SIZE);

        double singleThreadSum = 0.;
        double singleThreadSum2 = 0.;
        double singleThreadSum3 = 0.;
        double singleThreadSum43 = 0.;
        double singleThreadSumBySliceMatrixB = 0.;
        double concurrentThreadSum = 0.;
        int count = 1;

        while (count <= COUNT_OF_PASS) {
            System.out.println("Pass " + count);
            long start;
            double duration;

            start = System.currentTimeMillis();
            final int[][] matrixC = MatrixUtil.singleThreadMultiply(matrixA, matrixB);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Single thread time, sec: %.3f", duration);
            singleThreadSum += duration;

            start = System.currentTimeMillis();
            final int[][] matrixC2 = MatrixUtil.singleThreadMultiply2(matrixA, matrixB);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Single thread time (2), sec: %.3f", duration);
            singleThreadSum2 += duration;

            start = System.currentTimeMillis();
            final int[][] matrixC3 = MatrixUtil.singleThreadMultiply3(matrixA, matrixB);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Single thread time (3), sec: %.3f", duration);
            singleThreadSum3 += duration;

            start = System.currentTimeMillis();
            final int[][] matrixC43 = MatrixUtil.singleThreadMultiply43(matrixA, matrixB);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Single thread time (43), sec: %.3f", duration);
            singleThreadSum43 += duration;

            start = System.currentTimeMillis();
            final int[][] MatrixCBySliceMatrixB = MatrixUtil.singleThreadMultiplyBySliceMatrixB(matrixA, matrixB);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Single thread time (BySliceMatrixB), sec: %.3f", duration);
            singleThreadSumBySliceMatrixB += duration;

            start = System.currentTimeMillis();
            final int[][] concurrentMatrixC = MatrixUtil.concurrentMultiply(matrixA, matrixB, executor);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Concurrent thread time (BySliceMatrixB), sec: %.3f", duration);
            concurrentThreadSum += duration;

            if (!MatrixUtil.compare(matrixC, matrixC2)) {
                System.err.println("Comparison failed (2)");
                break;
            } else if (!MatrixUtil.compare(matrixC, matrixC3)) {
                System.err.println("Comparison failed (3)");
                break;
            } else if (!MatrixUtil.compare(matrixC, matrixC43)) {
                System.err.println("Comparison failed (43)");
                break;
            } else if (!MatrixUtil.compare(matrixC, MatrixCBySliceMatrixB)) {
                System.err.println("Comparison failed (2 BySliceMatrixB)");
                break;
            } else if (!MatrixUtil.compare(matrixC, concurrentMatrixC)) {
                System.err.println("Comparison failed (2 concurrent)");
                break;
            }
            count++;
        }
        executor.shutdown();
        count--;
        out("\nAverage single thread time, sec: %.3f", singleThreadSum / count);
        out("Average single thread time (2), sec: %.3f", singleThreadSum2 / count);
        out("Average single thread time (3), sec: %.3f", singleThreadSum3 / count);
        out("Average single thread time (43), sec: %.3f", singleThreadSum43 / count);
        out("Average single thread time (2 BySliceMatrixB), sec: %.3f", singleThreadSumBySliceMatrixB / count);
        out("Average concurrent thread time (2 BySliceMatrixB), sec: %.3f", concurrentThreadSum / count);

        /*
        Average single thread time, sec: 5.388
        Average single thread time (2), sec: 0.922
        Average single thread time (3), sec: 1.032
        Average single thread time (43), sec: 0.607
        Average single thread time (2 BySliceMatrixB), sec: 0.385
        Average concurrent thread time (2 BySliceMatrixB), sec: 0.119
        */

        /*
        Average single thread time, sec: 4.905
        Average single thread time (2), sec: 0.949
        Average single thread time (3), sec: 1.110
        Average single thread time (43), sec: 0.633
        Average single thread time (2 BySliceMatrixB), sec: 0.395
        Average concurrent thread time (2 BySliceMatrixB), sec: 0.132
        */

        /*
        1. Шаг 2 производительнее чем шаг 1 примерно в 7 раз. Ok.
        2. Шаг 3 НЕ производительнее шага 2. ПОЧЕМУ? (не соответствует статье).
        3. Шаг 43 производительнее шага 3. Ок.

        Из этих замеров, самый быстрый, это многопоточный.
        */
    }

    private static void out(String format, double ms) {
        System.out.println(String.format(format, ms));
    }
}

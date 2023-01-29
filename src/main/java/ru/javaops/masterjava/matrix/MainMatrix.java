package ru.javaops.masterjava.matrix;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * gkislin
 * 03.07.2016
 */
public class MainMatrix {
    private static final int MATRIX_SIZE = 1000;
    private static final int THREAD_NUMBER = 10;

    private static final int COUNT_OF_PASS = 5;

    private final static ExecutorService executor = Executors.newFixedThreadPool(MainMatrix.THREAD_NUMBER);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("Enter size of matrix. Enter 1000");
        Scanner scanner = new Scanner(System.in);
        // Специально сделано не через константу. Для проверки на шаге 0.
        int matrix_size = scanner.nextInt();

        // final int[][] matrixA = MatrixUtil.create(MATRIX_SIZE);
        // final int[][] matrixB = MatrixUtil.create(MATRIX_SIZE);

        final int[][] matrixA = MatrixUtil.create(matrix_size);
        final int[][] matrixB = MatrixUtil.create(matrix_size);

        double singleThreadSum0 = 0.;
        double singleThreadSum = 0.;
        double singleThreadSum2 = 0.;
        double singleThreadSum3 = 0.;
        double singleThreadSum42 = 0.;
        double singleThreadSum43 = 0.;
        //double singleThreadSum543 = 0.;
        //double concurrentThreadSum = 0.;
        int count = 1;

        while (count <= COUNT_OF_PASS) {
            System.out.println("Pass " + count);
            long start;
            double duration;

            start = System.currentTimeMillis();
            final int[][] matrixC0 = MatrixUtil.singleThreadMultiply0(matrixA, matrixB);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Single thread time (0), sec: %.3f", duration);
            singleThreadSum0 += duration;

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
            final int[][] matrixC42 = MatrixUtil.singleThreadMultiply42(matrixA, matrixB);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Single thread time (42), sec: %.3f", duration);
            singleThreadSum42 += duration;

            start = System.currentTimeMillis();
            final int[][] matrixC43 = MatrixUtil.singleThreadMultiply43(matrixA, matrixB);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Single thread time (43), sec: %.3f", duration);
            singleThreadSum43 += duration;
/*
            start = System.currentTimeMillis();
            final int[][] matrixC543 = MatrixUtil.singleThreadMultiply543(matrixA, matrixB);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Single thread time (543), sec: %.3f", duration);
            singleThreadSum543 += duration;
*/
            /*
            start = System.currentTimeMillis();
            final int[][] concurrentMatrixC = MatrixUtil.concurrentMultiply(matrixA, matrixB, executor);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Concurrent thread time, sec: %.3f", duration);
            concurrentThreadSum += duration;
*/
            if (!MatrixUtil.compare(matrixC, matrixC2)) {
                System.err.println("Comparison failed (2)");
                break;
            } else if (!MatrixUtil.compare(matrixC, matrixC0)) {
                System.err.println("Comparison failed (0)");
                break;
            } else if (!MatrixUtil.compare(matrixC, matrixC3)) {
                System.err.println("Comparison failed (3)");
                break;
            } else if (!MatrixUtil.compare(matrixC, matrixC42)) {
                System.err.println("Comparison failed (42)");
                break;
            } else if (!MatrixUtil.compare(matrixC, matrixC43)) {
                System.err.println("Comparison failed (43)");
                break;
            } /* else if (!MatrixUtil.compare(matrixC, matrixC543)) {
                System.err.println("Comparison failed (543)");
            }*/
            /* else if (!MatrixUtil.compare(matrixC, concurrentMatrixC)) {
                System.err.println("Comparison failed");
                break;
            }
            */
            count++;
        }
        executor.shutdown();
        System.out.println("\n");
        out("Average single thread time (0), sec: %.3f", singleThreadSum0 / count);
        out("Average single thread time, sec: %.3f", singleThreadSum / count);
        out("Average single thread time (2), sec: %.3f", singleThreadSum2 / count);
        out("Average single thread time (3), sec: %.3f", singleThreadSum3 / count);
        out("Average single thread time (42), sec: %.3f", singleThreadSum42 / count);
        out("Average single thread time (43), sec: %.3f", singleThreadSum43 / count);
        //out("Average single thread time 543, sec: %.3f", singleThreadSum543 / count);
        //out("Average concurrent thread time, sec: %.3f", concurrentThreadSum / count);

        /*
        Average single thread time (0), sec: 4.710
        Average single thread time, sec: 4.608
        Average single thread time (2), sec: 0.512
        Average single thread time (3), sec: 0.844
        Average single thread time (42), sec: 1.636
        Average single thread time (43), sec: 0.518
        */

        /*
        Average single thread time (0), sec: 4.828
        Average single thread time, sec: 4.784
        Average single thread time (2), sec: 0.498
        Average single thread time (3), sec: 0.849
        Average single thread time (42), sec: 1.635
        Average single thread time (43), sec: 0.507
        */

        /*
        1. Шаг 0 немного менее производительный чем шаг 1. Ок.
        2. Шаг 2 производительнее чем шаг 1 примерно в 10 раз. Ok.
        3. Шаг 3 НЕ производительнее шага 2. ПОЧЕМУ? (не соответствует статье).
        4. Шаг 42 НЕ производительнее шага 2. ПОЧЕМУ? (42 - моя собственная инициатива).
        5. Шаг 43 производительнее шага 3. Ок.
        6. Шаг 43 НЕ производительнее шага 2. ПОЧЕМУ? (не соответствует статье).

        Из этих замеров, похоже, что самый быстрый, это шаг 2.
        */
    }

    private static void out(String format, double ms) {
        System.out.println(String.format(format, ms));
    }
}

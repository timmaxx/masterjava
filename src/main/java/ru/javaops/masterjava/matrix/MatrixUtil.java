package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(
            int[][] matrixA,
            int[][] matrixB,
            ExecutorService executor) {
        final CompletionService<MatrixColumn> completionService = new ExecutorCompletionService<>(executor);

        final int matrixSize = matrixA.length;
        final int[][] matrixBT = transparent(matrixB);

        List<Future<MatrixColumn>> futures = new ArrayList<>();
        for (int i = 0; i < matrixSize; i++) {
            int finalI = i;
            futures.add( completionService.submit(
                    () -> new MatrixColumn(singleThreadMultiplyMatrixOnColumn(matrixA, matrixBT[finalI]), finalI)));
        }
/*
        // Мог-бы быть вариант альтернативный, но не понял, как вытащить номер строки двумерной матрицы при создании из неё потока.
        List<Future<MatrixColumn>> futures = Arrays.stream(matrixBT)
                .map(matrixBTrow -> new MatrixColumn(singleThreadMultiplyMatrixOnColumn(matrixA, matrixBTrow), finalI))
                .collect(Collectors.toList());
*/
        try {
            final int[][] matrixCT = new int[matrixSize][matrixSize];
            while (!futures.isEmpty()) {
                Future<MatrixColumn> future;
                do {
                    future = completionService.poll(1, TimeUnit.SECONDS);
                } while (future == null);
                futures.remove(future);
                MatrixColumn matrixSlice = future.get();
                matrixCT[matrixSlice.getColumnN()] = matrixSlice.getMatrixCN1();
            }
            return transparent(matrixCT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Перемножение матрицы (A) на матрицу (B), через нарезку B на колонки и сборкой результирующей матрицы С из колонок.
    public static int[][] singleThreadMultiplyBySliceMatrixB(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;

        final int[][] matrixBT = transparent(matrixB);
        final int[][] matrixCT = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            matrixCT[i] = singleThreadMultiplyMatrixOnColumn(matrixA, matrixBT[i]);
        }

        return transparent(matrixCT);
    }

    // Перемножение матрицы на столбец.
    public static int[] singleThreadMultiplyMatrixOnColumn(int[][] matrixA, int[] matrixBT) {
        final int matrixSize = matrixA.length;
        final int[] matrixCN1 = new int[matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            int sum = 0;
            for (int k = 0; k < matrixSize; k++) {
                sum += matrixA[i][k] * matrixBT[k];
            }
            matrixCN1[i] = sum;
        }
        return matrixCN1;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    // Шаг 1. Исходный простейший кубический алгоритм перемножения матриц.
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    // Реализован Шаг 2 из статьи.
    // Матрицу B транспонируем (BT) и тогда будет последовательный доступ к элементам BT.
    // Получим максимальную выгоду от кеша.
    public static int[][] singleThreadMultiply2(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final int[][] matrixBT = transparent(matrixB);

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixBT[j][k];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    // Реализован Шаг 3 из статьи.
    // Главный недостаток от шага 2 — много циклов. Сокращаем объем кода и делаем его изящнее,
    // объединив операции транспонирования и умножения в один вычислительный цикл.
    // При этом, транспонирование будет осуществляться не для целой матрицы (B), а по-столбцам,
    // по мере их надобности.
    public static int[][] singleThreadMultiply3(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int j = 0; j < matrixSize; j++) {
            final int[] oneColumnB = new int[matrixSize];
            for (int k = 0; k < matrixSize; k++) {
                oneColumnB[k] = matrixB[k][j];
            }
            for (int i = 0; i < matrixSize; i++) {
                int[] oneRowA = matrixA[i];
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += oneRowA[k] * oneColumnB[k];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    // Реализован Шаг 4 из статьи на методе singleThreadMultiply3.
    // Воспользуемся еще одним преимуществом Java — исключениями.
    // Заменяем проверку на выход за границы матрицы (для внешнего цикла - по столбцам матрицы B) на блок try {} catch {}.
    // Это сократит количество сравнений на 1000 в нашем случае.
    // Действительно, зачем 1000 раз сравнивать то, что всегда будет возвращать false и на 1001 раз вернет true.
    //
    // С одной стороны — сокращаем количество сравнений,
    // с другой — появляется дополнительные накладные расходы на обработку исключений.
    public static int[][] singleThreadMultiply43(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        try {
            for (int j = 0; ; j++) {
                final int[] oneColumnB = new int[matrixSize];
                for (int k = 0; k < matrixSize; k++) {
                    oneColumnB[k] = matrixB[k][j];
                }
                for (int i = 0; i < matrixSize; i++) {
                    int[] oneRowA = matrixA[i];
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += oneRowA[k] * oneColumnB[k];
                    }
                    matrixC[i][j] = sum;
                }
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int[][] transparent(int[][] matrixA) {
        final int matrixSize = matrixA.length;
        final int[][] matrixAT = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrixAT[j][i] = matrixA[i][j];
            }
        }
        return matrixAT;
    }

    public static synchronized void printMatrix(String name, int[][] matrixA)
    {
        final int matrixSize = matrixA.length;
        System.out.println( name + "(" + matrixSize + ", " + matrixSize + ") =");
        for (int[] row: matrixA) {
            for (int cell: row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public static synchronized void printMatrixColumn(String name, int[] matrixA)
    {
        final int matrixSize = matrixA.length;
        System.out.println( "  " + name + "(" + matrixSize + ", " + matrixSize + ") =");
        for (int cell: matrixA) {
            System.out.println("  " + cell);
        }
    }


    static public class MatrixColumn {
        private final int[] matrixCN1;
        private final int columnN;

        public MatrixColumn(int[] matrixCN1, int columnN) {
            this.matrixCN1 = matrixCN1;
            this.columnN = columnN;
        }

        public int[] getMatrixCN1() {
            return matrixCN1;
        }

        public int getColumnN() {
            return columnN;
        }
    }
}

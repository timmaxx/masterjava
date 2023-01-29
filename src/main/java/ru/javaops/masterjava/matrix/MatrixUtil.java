package ru.javaops.masterjava.matrix;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        return matrixC;
    }

    // Реализован шаг 0 из статьи.
    // Предположение о деоптимизации в случае, когда отказываемся от границ матриц final на динамические.
    public static int[][] singleThreadMultiply0(int[][] matrixA, int[][] matrixB) {
        final int[][] matrixC = new int[matrixA.length][matrixA.length];

        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixA.length; j++) {
                int sum = 0;
                for (int k = 0; k < matrixA.length; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
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

        final int matrixBT[][] = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrixBT[j][i] = matrixB[i][j];
            }
        }

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

    // Реализован Шаг 4 из статьи на методе singleThreadMultiply2.
    // Воспользуемся еще одним преимуществом Java — исключениями.
    // Заменяем проверку на выход за границы матрицы (для внешнего цикла) на блок try {} catch {}.
    // Это сократит количество сравнений на 1000 в нашем случае.
    // Действительно, зачем 1000 раз сравнивать то, что всегда будет возвращать false и на 1001 раз вернет true.
    //
    // С одной стороны — сокращаем количество сравнений,
    // с другой — появляется дополнительные накладные расходы на обработку исключений.
    public static int[][] singleThreadMultiply42(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final int matrixBT[][] = new int[matrixSize][matrixSize];

        try {
            for (int i = 0; ; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    matrixBT[j][i] = matrixB[i][j];
                }
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            for (int i = 0; ; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += matrixA[i][k] * matrixBT[j][k];
                    }
                    matrixC[i][j] = sum;
                }
            }
        } catch (IndexOutOfBoundsException ignored) {
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

        final int oneColumnB[] = new int[matrixSize];
        for (int j = 0; j < matrixSize; j++) {
            for (int k = 0; k < matrixSize; k++) {
                oneColumnB[k] = matrixB[k][j];
            }
            for (int i = 0; i < matrixSize; i++) {
                int oneRowA[] = matrixA[i];
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

        final int oneColumnB[] = new int[matrixSize];
        try {
            for (int j = 0; ; j++) {
                for (int k = 0; k < matrixSize; k++) {
                    oneColumnB[k] = matrixB[k][j];
                }
                for (int i = 0; i < matrixSize; i++) {
                    int oneRowA[] = matrixA[i];
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

    // Шаг 5. Была попытка отказаться от проверки окончания в цикле второго уровня.
    // Но дождаться окончания выполнения этого метода не вышло.
    public static int[][] singleThreadMultiply543(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final int oneColumnB[] = new int[matrixSize];
        try {
            for (int j = 0; ; j++) {
                try {
                    for (int k = 0; ; k++) {
                        oneColumnB[k] = matrixB[k][j];
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
                try {
                    for (int i = 0; ; i++) {
                        int oneRowA[] = matrixA[i];
                        int sum = 0;
                        for (int k = 0; k < matrixSize; k++) {
                            sum += oneRowA[k] * oneColumnB[k];
                        }
                        matrixC[i][j] = sum;
                    }
                }  catch (IndexOutOfBoundsException ignored) {
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
}

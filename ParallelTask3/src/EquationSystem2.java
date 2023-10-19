import java.util.*;
import java.util.concurrent.*;

public class EquationSystem2 {
    public static void main(String[] args) {
        final int size = 13000;
        final double epsilon = 0.001;
        Map<String, Object> generated = generateRandomLinearEquation(size);
        int[] threadNum = new int[]{2, 4, 8, 16};

        double[] XNonParallel = new double[size];

        System.out.println("Starting non-parallel:");
        long start = System.currentTimeMillis();
        jacobiNonParallel(size, (double[][]) generated.get("Matrix"), (double[]) generated.get("Free elements"), XNonParallel, epsilon);
        long finish = System.currentTimeMillis();
        System.out.println("Time for non-parallel: " + (finish - start) + "\n");

        for (int x : threadNum) {
            double[] XParallel = new double[size];

            System.out.println("Starting parallel (" + x + " threads):");
            long start2 = System.currentTimeMillis();
            jacobiParallel(size, (double[][]) generated.get("Matrix"), (double[]) generated.get("Free elements"), XParallel, epsilon, x);
            long finish2 = System.currentTimeMillis();
            System.out.println("Time for parallel (" + x + " threads): " + (finish2 - start2));
            double speedup = (double) (finish - start) / (finish2 - start2);
            System.out.println("Acceleration: " + speedup);
            System.out.println("efficiency: " + speedup / x);
        }

    }

    public static void jacobiNonParallel(int size, double[][] coefficients, double[] values, double[] X, double eps) {
        double[] previousX = new double[size];
        double err;

        do {
            err = 0;
            double[] newValues = new double[size];

            for (int i = 0; i < size; i++) {
                newValues[i] = values[i];

                for (int j = 0; j < size; j++) {
                    if (i != j) {
                        newValues[i] -= coefficients[i][j] * previousX[j];
                    }
                }

                newValues[i] = newValues[i] / coefficients[i][i];

                if (Math.abs(previousX[i] - newValues[i]) > err) {
                    err = Math.abs(previousX[i] - newValues[i]);
                }
            }
            previousX = newValues;
        } while (err > eps);

        System.arraycopy(previousX, 0, X, 0, size);
    }

    public static void jacobiParallel(int size, double[][] coefficients, double[] values, double[] X, double eps, int threadNum) {
        double[] previousX = new double[size];
        final double[] err = new double[1];

        Thread[] threads = new Thread[threadNum];

        int[][] parameters = new int[threadNum][2];

        int remainder = size % threadNum;
        int remainderPassed = 0;

        for (int q = 0; q < threadNum; q++) {
            int from = size / threadNum * q + remainderPassed;
            int to = size / threadNum * (q + 1) + remainderPassed;

            if (remainder > 0) {
                remainder--;
                remainderPassed++;
                to++;
            }
            parameters[q][0] = from;
            parameters[q][1] = to;
        }

        do {
            err[0] = 0;
            double[] newValues = new double[size];

            for (int q = 0; q < threadNum; q++) {
                int toPass = q;
                threads[q] = new Thread(() -> {
                    for (int i = parameters[toPass][0]; i < parameters[toPass][1]; i++) {
                        newValues[i] = values[i];

                        for (int j = 0; j < size; j++) {
                            if (i != j) {
                                newValues[i] -= coefficients[i][j] * previousX[j];
                            }
                        }

                        newValues[i] = newValues[i] / coefficients[i][i];

                        if (Math.abs(previousX[i] - newValues[i]) > err[0]) {
                            err[0] = Math.abs(previousX[i] - newValues[i]);
                        }
                    }
                });
            }

            for (Thread item : threads) {
                item.start();
            }

            for (Thread item : threads) {
                try {
                    item.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.arraycopy(newValues, 0, previousX, 0, size);

        } while (err[0] > eps);

        System.arraycopy(previousX, 0, X, 0, size);
    }

    public static Map<String, Object> generateRandomLinearEquation(int vars) {
        Map<String, Object> toReturn = new HashMap<>();

        double[] solutions = new double[vars];
        double[] freeElements = new double[vars];

        List<double[]> matrix = new ArrayList<>();

        Random random = new Random();

        for (int i = 0; i < vars; i++) {
            solutions[i] = random.nextInt(100) + 1;
        }

        for (int i = 0; i < vars; i++) {
            double[] toAdd = new double[vars];

            for (int j = 0; j < vars; j++) {
                if (j == i) {
                    toAdd[j] = random.nextInt(100 * vars) + 100 * vars;
                } else {
                    toAdd[j] = random.nextInt(100) + 1;
                }
                freeElements[i] += toAdd[j] * solutions[j];
            }

            matrix.add(toAdd);
        }

        toReturn.put("Matrix", matrix.toArray(new double[0][]));
        toReturn.put("Free elements", freeElements);
        toReturn.put("Solutions", solutions);
        return toReturn;
    }
}


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static java.util.Collections.min;

public class Main {

    interface FloydWarshallFunction {
        void run(int thread_id);
    }
    private static final int INF = Integer.MAX_VALUE;

    public static List<List<Integer>> floydWarshall(List<List<Integer>> graph, int start, int end, int numThreads) {
        int n = graph.size();
        List<List<Integer>> distance = new ArrayList<>(n);

        for (List<Integer> row : graph) {
            List<Integer> newRow = new ArrayList<>(row);
            distance.add(newRow);
        }

        FloydWarshallFunction parallelFloydWarshall = (int thread_id) -> {
            for (int k = 0; k < n; k++) {
                for (int i = thread_id; i < n; i+= numThreads) {
                        for (int j = 0; j < n; j++) {
                            if (distance.get(i).get(k) != INF && distance.get(k).get(j) != INF) {
                                int distIK = distance.get(i).get(k);
                                int distKJ = distance.get(k).get(j);
                                int currentDist = distance.get(i).get(j);

                                // Update distance[i][j] to the minimum of its current value
                                // and the sum of distances through vertex k
                                int newDist = Math.min(currentDist, distIK + distKJ);
                                distance.get(i).set(j, newDist);
                            }
                        }
                }
            }
        };

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            final int thread_id = i; // Store the thread_id in a final variable
            Runnable runnable = () -> parallelFloydWarshall.run(thread_id);
            Thread thread = new Thread(runnable);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return distance;
    }

    public static List<List<Integer>> generateRandomGraph(int n) {
        List<List<Integer>> graph = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            List<Integer> row = new ArrayList<>(n);
            graph.add(row);
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    row.add(0);
                } else {
                    row.add(new Random().nextInt(10) + 1);
                }
            }
        }

        return graph;
    }

    public static void main(String[] args) {
        int sizeR = 1000;
//        List<List<Integer>> graphR = List.of(
//                List.of(0, 5, INF, INF, INF),
//                List.of(5, 0, INF, 7, INF),
//                List.of(2, INF, 0, 3, 8),
//                List.of(INF, 7, 2, 0, 4),
//                List.of(INF, INF, 8, INF, 0)
//        );

        List<List<Integer>> graphR = generateRandomGraph(sizeR);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter start vertex: ");
        int start = scanner.nextInt();
        System.out.print("Enter end vertex: ");
        int end = scanner.nextInt();


        long beginTime = System.currentTimeMillis();
        List<List<Integer>> shortestDistances = floydWarshall(graphR, start, end, 1);
        long endTime = System.currentTimeMillis();
        float timeOneThread = (float) (endTime - beginTime) / 1000;

        if (shortestDistances.get(start).get(end) == INF) {
            System.out.println("No path from vertex " + start + " to " + end);
        } else {
            System.out.println("Shortest distance from vertex " + start + " to " + end + " is: " + shortestDistances.get(start).get(end));
        }

        System.out.println("Time taken for 1 thread: " + timeOneThread + " seconds");

        int[] threadsArray = new  int[]{2,4,8,16};
        for (int numThreads:
             threadsArray) {
            beginTime = System.currentTimeMillis();
            shortestDistances = floydWarshall(graphR, start, end, numThreads);
            endTime = System.currentTimeMillis();
            float timeNThread = (float) (endTime - beginTime) / 1000;

            if (shortestDistances.get(start).get(end) == INF) {
                System.out.println("No path from vertex " + start + " to " + end);
            } else {
                System.out.println("Shortest distance from vertex " + start + " to " + end + " is: " + shortestDistances.get(start).get(end));
            }

            System.out.println("Time taken for " + numThreads + " threads: " + timeNThread + " seconds");
            System.out.println("Acceleration: " + timeOneThread / timeNThread);
            System.out.println("Efficiency: " + (timeOneThread / timeNThread) / numThreads);
        }


    }
}

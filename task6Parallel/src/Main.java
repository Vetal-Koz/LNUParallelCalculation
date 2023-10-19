import java.util.*;

public class Main {

    interface Dijkstra {
        void run(int thread_id);
    }
    static final int INF = Integer.MAX_VALUE;

    static class DijkstraResult {
        List<Integer> distances;
        List<List<Integer>> paths;

        public DijkstraResult(List<Integer> distances, List<List<Integer>> paths) {
            this.distances = distances;
            this.paths = paths;
        }
    }

    public static DijkstraResult dijkstra(List<List<Integer>> graph, int start, int num_threads) {
        int n = graph.size();
        List<Integer> distances = new ArrayList<>();
        List<List<Integer>> paths = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            distances.add(INF);
            paths.add(new ArrayList<Integer>());
        }
        distances.set(start, 0);

        boolean[] visited = new boolean[n];
        Arrays.fill(visited, false);

        Dijkstra parallel_dijkstra = (int thread_id) ->{
            for (int k = 0; k < n; k++) {
                int u = -1;
                int local_min = INF;
                for (int i = 0; i < n; ++i) {
                    if (!visited[i] && distances.get(i) < local_min) {
                        local_min = distances.get(i);
                        u = i;
                    }
                }
                if (u == -1) break;

                visited[u] = true;
                for (int v = 0; v < n; ++v) {
                    if (!visited[v] && graph.get(u).get(v) != INF && distances.get(u) + graph.get(u).get(v) < distances.get(v)) {
                        distances.set(v, distances.get(u) + graph.get(u).get(v));
                        paths.set(v, paths.get(u));
                        paths.get(v).add(u);
                    }
                }

            }
        };
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < num_threads; ++i) {
            int threadId = i;
            Runnable runnable = () -> parallel_dijkstra.run(threadId);
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

        for (int i = 0; i < n; ++i) {
            paths.get(i).add(i);
        }

        return new DijkstraResult(distances, paths);
    }

    public static void main(String[] args) {
        int n = 5;
        int size_r = 1000;
        List<List<Integer>> graph_r = generateRandomGraph(size_r);

//        List<List<Integer>> graph_r = List.of(
//                List.of(0, 5, INF, INF, INF),
//                List.of(5, 0, INF, 7, INF),
//                List.of(2, INF, 0, 3, 8),
//                List.of(INF, 7, 2, 0, 4),
//                List.of(INF, INF, 8, INF, 0)
//        );

        Scanner scanner = new Scanner(System.in);

        int start;
        System.out.print("Enter start vertex: ");
        start = scanner.nextInt();



        long beginTime = System.currentTimeMillis();
        DijkstraResult result = dijkstra(graph_r, start, 1);
        long endTime = System.currentTimeMillis();
        float timeOneThread = (endTime - beginTime) / 1000.0f;

        System.out.println("Shortest distances from vertex " + start + " to all other vertices:");
        for (int i = 0; i < result.distances.size(); ++i) {
            System.out.println("Vertex " + i + ": " + result.distances.get(i));
        }



        System.out.println("Time taken for 1 thread: " + timeOneThread + " seconds");
        System.out.println();

        int[] threadsArray = new int[]{2,4,8,16};
        for (int num_threads:
             threadsArray) {
            beginTime = System.currentTimeMillis();
            DijkstraResult result_a = dijkstra(graph_r, start, num_threads);
            endTime = System.currentTimeMillis();
            float timeNThread = (endTime - beginTime) / 1000.0f;

            System.out.println("Time taken for " + num_threads + " threads: " + timeNThread + " seconds");
            System.out.println("Acceleration: " + timeOneThread / timeNThread);
            System.out.println("Efficiency: " + (timeOneThread / timeNThread) / num_threads);
        }
    }

    public static List<List<Integer>> generateRandomGraph(int n) {
        List<List<Integer>> graph = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            List<Integer> row = new ArrayList<>(n);
            graph.add(row);
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    row.add(0);
                } else {
                    row.add(new Random().nextInt(100) + 1);
                }
            }
        }

        return graph;
    }
}

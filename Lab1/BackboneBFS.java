import java.util.*;
public class BackboneBFS {
    static class Edge {
        int to;
        boolean isBackbone;

        Edge(int to, boolean isBackbone) {
            this.to = to;
            this.isBackbone = isBackbone;
        }
    }

    static int findBestPath(int n, int src, int dest, List<List<Edge>> adj, int[] parent) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Deque<Integer> dq = new ArrayDeque<>();

        dist[src] = 0;
        dq.addFirst(src);
        parent[src] = -1;

        while (!dq.isEmpty()) {
            int u = dq.pollFirst();

            for (Edge e : adj.get(u)) {
                int v = e.to;
                int weight = e.isBackbone ? 0 : 1;

                if (dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    parent[v] = u;

                    if (weight == 0)
                        dq.addFirst(v);
                    else
                        dq.addLast(v);
                }
            }
        }

        return dist[dest];
    }

    static void printPath(int src, int dest, int[] parent) {
        List<Integer> path = new ArrayList<>();
        int node = dest;
        while (node != -1) {
            path.add(node);
            node = parent[node];
        }

        Collections.reverse(path);

        System.out.print("Path: ");
        for (int p : path)
            System.out.print((char)(p + 'A') + " ");  // Convert 0 -> A, 1 -> B, etc.
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of nodes and edges: ");
        int n = sc.nextInt();
        int m = sc.nextInt();

        List<List<Edge>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++)
            adj.add(new ArrayList<>());

        System.out.println("Enter edges in format: A B type (1=backbone, 0=normal):");
        for (int i = 0; i < m; i++) {
            char ch1 = sc.next().charAt(0);
            char ch2 = sc.next().charAt(0);
            int type = sc.nextInt();

            int u = ch1 - 'A';
            int v = ch2 - 'A';
            boolean isBackbone = (type == 1);
            adj.get(u).add(new Edge(v, isBackbone));
            adj.get(v).add(new Edge(u, isBackbone)); // Remove this line if graph is directed
        }

        System.out.print("Enter source and destination (e.g. A D): ");
        char chSrc = sc.next().charAt(0);
        char chDest = sc.next().charAt(0);

        int src = chSrc - 'A';
        int dest = chDest - 'A';

        int[] parent = new int[n];
        int result = findBestPath(n, src, dest, adj, parent);

        if (result == Integer.MAX_VALUE)
            System.out.println("No path exists between source and destination.");
        else
            printPath(src, dest, parent);
    }
}



















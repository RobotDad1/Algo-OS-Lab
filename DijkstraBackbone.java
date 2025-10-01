import java.util.Scanner;

public class DijkstraBackbone {

    static final int MAX_N = 100;
    static final int MAX_DEG = 100;
    static final int INF = Integer.MAX_VALUE;

    static class Edge {
        int to;
        boolean isBackbone;

        Edge(int to, boolean isBackbone){
            this.to = to;
            this.isBackbone = isBackbone;
        }
    }

    static Edge[][] adj = new Edge[MAX_N][MAX_DEG];
    static int[] deg = new int[MAX_N];
    static int[] dist = new int[MAX_N];
    static int[] parent = new int[MAX_N];

    // Manually implementing min-heap
    static class MinHeap {
        int[] heap = new int[MAX_N];
        int[] pos = new int[MAX_N];
        int size = 0;

        MinHeap() {
            for (int i = 0; i < MAX_N; i++) pos[i] = -1;
        }
        void swap(int i, int j) {
            int temp = heap[i];
            heap[i] = heap[j];
            heap[j] = temp;
            pos[heap[i]] = i;
            pos[heap[j]] = j;
        }
        void push(int node) {
            heap[size] = node;
            pos[node] = size;
            int i = size;
            size++;

            while (i > 0 && dist[heap[i]] < dist[heap[(i - 1) / 2]]) {
                swap(i, (i - 1) / 2);
                i = (i - 1) / 2;
            }
        }
        void heapify(int i) {
            int smallest = i;
            int l = 2*i + 1;
            int r = 2*i + 2;

            if (l<size && dist[heap[l]] < dist[heap[smallest]]) smallest = l;
            if (r<size && dist[heap[r]] < dist[heap[smallest]]) smallest = r;

            if (smallest != i) {
                swap(i, smallest);
                heapify(smallest);
            }
        }
        void decreaseKey(int node) {
            int i = pos[node];
            while (i > 0 && dist[heap[i]] < dist[heap[(i - 1) / 2]]) {
                swap(i, (i - 1) / 2);
                i = (i - 1) / 2;
            }
        }
        int pop() {
            int root = heap[0];
            heap[0] = heap[--size];
            pos[heap[0]] = 0;
            pos[root] = -1;
            heapify(0);
            return root;
        }
        boolean isEmpty() {
            return size == 0;
        }
        boolean contains(int node) {
            return pos[node] != -1;
        }
    }
    static void dijkstra(int src, int n) {
        for (int i=0; i<n; i++) {
            dist[i] = INF;
            parent[i] = -1;
        }
        dist[src] = 0;
        MinHeap pq = new MinHeap();
        pq.push(src);

        while (!pq.isEmpty()) {
            int u = pq.pop();

            for (int i=0; i<deg[u]; i++) {
                int v=adj[u][i].to;
                int weight=adj[u][i].isBackbone ? 0 : 1;

                if (dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    parent[v] = u;

                    if (pq.contains(v)) {
                        pq.decreaseKey(v);
                    } else {
                        pq.push(v);
                    }
                }
            }
        }
    }
    static void printPath(int src, int dest) {
        if (dist[dest] == INF) {
            System.out.println("No path exists between source and destination.");
            return;
        }
        int[] path = new int[MAX_N];
        int idx = 0;
        int node = dest;
        while (node != -1) {
            path[idx++] = node;
            node = parent[node];
        }

        System.out.print("Path: ");
        for (int i = idx - 1; i >= 0; i--) {
            System.out.print(path[i] + " ");
        }
        System.out.println();
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of nodes and edges: ");
        int n = sc.nextInt();
        int m = sc.nextInt();
        for (int i=0; i<n; i++) deg[i] = 0;

        System.out.println("Enter edges in format: u v type (1 = backbone, 0 = normal):");
        for (int i=0; i<m; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            int type=sc.nextInt();
            boolean isBackbone = (type == 1);

            adj[u][deg[u]++] = new Edge(v, isBackbone);
            adj[v][deg[v]++] = new Edge(u, isBackbone); // for undirected
        }
        System.out.print("Enter source and destination: ");
        int src = sc.nextInt();
        int dest = sc.nextInt();

        dijkstra(src, n);
        printPath(src, dest);
    }
}

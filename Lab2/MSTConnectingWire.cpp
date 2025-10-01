#include <bits/stdc++.h>
using namespace std;
const int maxx_nodes = 1000;
const int maxx_edges = 10000;  

int parentNode[maxx_nodes], setSize[maxx_nodes];

struct Edge {
    int w, u, v;
};

void make_set(int node) {
    parentNode[node] = node;
    setSize[node] = 1;
}

int find_ultimateParent(int node) {
    if (node == parentNode[node]) return node;
    return parentNode[node] = find_ultimateParent(parentNode[node]);
}

void union_sets(int a, int b) {
    a = find_ultimateParent(a);
    b = find_ultimateParent(b);
    if (a != b) {
        if (setSize[a] < setSize[b]) {
            int tmp = a; a = b; b = tmp;
        }
        parentNode[b] = a;
        setSize[a] += setSize[b];
    }
}
// Sorting edges using Bubble sort by weight .. m is no. of valid edges count
void sortEdges(Edge edges[], int m) {
    for (int i = 0; i < m - 1; i++) {
        for (int j = 0; j < m - i - 1; j++) {
            if (edges[j].w > edges[j+1].w) {
                Edge tmp = edges[j];
                edges[j] = edges[j+1];
                edges[j + 1]=tmp;
            }
        }
    }
}

int calculateMST(int socketType[], Edge edges[], int n, int m) {
    for (int i = 0; i < n; i++) make_set(i);

    Edge validEdges[maxx_edges];
    int validCount = 0;

    // filter edges
    for (int i = 0; i < m; i++) {
        int u = edges[i].u, v = edges[i].v;
        if (socketType[u] && socketType[v] && u != v) {    //agar koi type circuit me nhi hai to vo skip ho jayega include nhi hoga
            validEdges[validCount++] = edges[i];
        }
    }

    sortEdges(validEdges, validCount);

    int required = 0;
    for (int i = 0; i < n; i++) if (socketType[i]) required++;
    if (required <= 1) return 0;

    int totalCost = 0, connected = 0;

    cout << "MST edges:\n";
    for (int i = 0; i < validCount; i++) {
        int u = validEdges[i].u, v = validEdges[i].v, w = validEdges[i].w;
        if (find_ultimateParent(u) != find_ultimateParent(v)) {
            union_sets(u, v);
            totalCost += w;
            connected++;
            cout << u << " - " << v << " : " << w << "\n";
            if (connected == required - 1) return totalCost;
        }
    }
    return -1;
}

int main() {
    int n, m;
    cout << "Enter number of sockets: ";
    cin >> n;

    cout << "Enter number of edges: ";
    cin >> m;

    if (n == 1) {
        cout << "Only one socket, so no joining possible. Total wire length = 0\n";
        return 0;
    }

    int ground[maxx_nodes], neutral[maxx_nodes], live[maxx_nodes];
    cout << "Enter (G N L in 0/1) present for each socket :\n";
    for (int i=0; i<n; i++) cin >> ground[i] >> neutral[i] >> live[i];
    
    Edge edges[maxx_edges];
    cout << "Enter edges (u v w):\n";
    for (int i = 0; i < m; i++) cin >> edges[i].u >> edges[i].v >> edges[i].w;

    cout << "\n----- Ground MST -----\n";
    int g = calculateMST(ground, edges, n, m);

    cout << "\n----- Neutral MST -----\n";
    int ne = calculateMST(neutral, edges, n, m);

    cout << "\n----- Live MST -----\n";
    int l = calculateMST(live, edges, n, m);

    cout << "\n";
    if (g<0 || ne<0 || l<0) {
        if (g < 0) cout << "Impossible to connect all Ground points.\n";
        if (ne < 0) cout << "Impossible to connect all Neutral points.\n";
        if (l < 0) cout << "Impossible to connect all Live points.\n";
    } else {
        cout << "Minimum wire length for Ground: " << g << "\n";
        cout << "Minimum wire length for Neutral: " << ne << "\n";
        cout << "Minimum wire length for Live: " << l << "\n";
        cout << "Total wire length needed: " << (g + ne + l) << "\n";
    }
    return 0;
}

// 1 1 1
// 0 1 1
// 0 1 1
// 0 1 1
// 1 1 1
// 0 1 1
// 0 1 1
// 1 1 1
// 0 1 1


// 0 1 8
// 1 3 8
// 3 2 3
// 3 6 6
// 4 7 4
// 4 0 3
// 1 5 2
// 2 6 1
// 5 8 1
// 1 8 4
// 0 7 5

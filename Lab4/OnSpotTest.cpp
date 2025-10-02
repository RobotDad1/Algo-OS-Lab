// we have given a grid matrix we have to print the length of the longest consequtive path in the matrix we can move in all directions(up,down,left,right)..

// Grid Path (Longest Consequtive Path)

#include <bits/stdc++.h>
using namespace std;

#define maxx 100
int mat[maxx][maxx];
int visited[maxx][maxx];
int n, m;

int dpx[] = {-1, 1, 0, 0};
int dpy[] = {0, 0, -1, 1};

bool check(int x, int y) {
    return (x >= 0 && x < n && y >= 0 && y < m);
}

int dfs(int x, int y) {
    if (visited[x][y] != -1) return visited[x][y];

    int ans = 1;
    for (int dir = 0; dir < 4; dir++) {
        int nx = x + dpx[dir];
        int ny = y + dpy[dir];
        if (check(nx, ny) && mat[nx][ny] == mat[x][y] + 1) {
            ans = max(ans, 1 + dfs(nx, ny));
        }
    }
    return visited[x][y] = ans;
}

int main() {
    cout << "Enter no. of rows and column :- ";
    cin >> n >> m;

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            cin >> mat[i][j];
        }
    }

    memset(visited, -1, sizeof(visited));

    int path_length = 0;
    int start_x = 0, start_y = 0;

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            int len = dfs(i, j);
            if (len > path_length) {
                path_length = len;
                start_x = i;
                start_y = j;
            }
        }
    }

    cout << "Longest Consecutive Path Length = " << path_length << "\n";

    // ---- Reconstruct the path ----
    vector<int> path;
    int x = start_x, y = start_y;
    path.push_back(mat[x][y]);

    // Follow the path according to visited[][] values
    while (true) {
        bool moved = false;
        for (int dir = 0; dir < 4; dir++) {
            int nx = x + dpx[dir];
            int ny = y + dpy[dir];
            if (check(nx, ny) &&
                mat[nx][ny] == mat[x][y] + 1 &&
                visited[nx][ny] == visited[x][y] - 1) {
                path.push_back(mat[nx][ny]);
                x = nx;
                y = ny;
                moved = true;
                break;
            }
        }
        if (!moved) break; // reached end of path
    }

    cout << "Path: ";
    for (int val : path) cout << val << " ";
    cout << "\n";

    return 0;
}

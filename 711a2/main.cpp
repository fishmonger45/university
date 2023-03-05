#include <iostream>
#include <vector>
#include <omp.h>
#include <sstream>
//#include <bits/stdc++.h>
#include <algorithm>

using namespace std;
using adjacency = vector<vector<int>>;

void simple(int n, float *a, float *b)
{
    int i;
#pragma omp parallel for
    for (i = 1; i < n; i++)
        b[i] = (a[i] + a[i - 1]);
}

vector<adjacency> fetch_adjacencies()
{
    vector<adjacency> adjacencies;
    std::string in;
    int n;
    while (std::cin)
    {
        std::getline(std::cin, in);
        n = stoi(in);
        if (n == 0)
        {
            break;
        }
        adjacency xs;
        vector<int> ys;

        for (int i = 0; i < n; ++i)
        {
            int temp;

            std::getline(std::cin, in);
            std::stringstream iss(in);
            while (iss >> temp)
            {
                if (temp >= 0 && temp < n)
                {
                    ys.push_back(temp);
                }
                else
                {
                    cout << "error: nodes must be betweeen the input bounds" << endl;
                    exit(1);
                }
            }
            xs.push_back(ys);
            ys.clear();
        }
        adjacencies.push_back(xs);
    }
    return adjacencies;
}

void print(vector<adjacency> adjacencies) {
    cout << "adjacency:" << endl;
    for (vector<vector<int>> adj : adjacencies)
    {
        for (vector<int> row : adj)
        {
            for (int elem : row)
            {
                cout << elem << " ";
            }
            cout << endl;
        }
    }
    cout << "adjacency end" << endl;
}

void dfs(int n, adjacency adj, vector<int> dp, vector<int> vis) {
    vis[n] = true;
    for(int i = 0; i < adj[n].size(); i++) {
            if(!vis[adj[n][i]]) {
                dfs(adj[n][i], adj, dp, vis);
            }

            dp[n] = max(dp[n], 1 + dp[adj[n][i]]);
    }
}

vector<int> longest_path(adjacency adj, int n) {
    vector<int> dp;
    for(int i = 0; i < n+1; ++i) {
        dp.push_back(0);
    }
 
    vector<int> vis;
    for(int i = 0; i < n+1; ++i) {
        vis.push_back(0);
    }
 
    for (int i = 1; i <= n; i++) {
        if (!vis[i])
            dfs(i, adj, dp, vis);
    }
    return dp;
}

void bfs(adjacency adj, int n) {

    vector<bool> visited;
    for(int i = 0; i < adj.size(); ++i) {
        visited.push_back(false);
    }
    vector<int> dist;
    for(int i = 0; i < adj.size(); ++i) {
        dist.push_back(-1);
    }
    dist[n] = 0;
    visited[n] = true;
    for(auto x : adj[n]) {
        //mark x visited
    }
}

int main(void)
{
    vector<adjacency> adjacencies = fetch_adjacencies();
    print(adjacencies);
    for(auto adj : adjacencies) {
        vector<int> eccentricities;
        for(int n = 0; n < adj.size(); ++n) {
            eccentricities = longest_path(adj, n);
        }
        // remove all 0 elements
        remove_if(eccentricities.begin(), eccentricities.end(), [](int x){ return x == 0;});
        cout << "radius: " << *min_element(eccentricities.begin(), eccentricities.end()) << endl;
    }
}
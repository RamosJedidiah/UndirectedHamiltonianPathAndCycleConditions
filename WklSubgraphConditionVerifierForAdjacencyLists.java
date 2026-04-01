import java.util.LinkedList;
import java.util.Queue;

// This code does not find violations. It verifies violations.

public class WklSubgraphConditionVerifierForAdjacencyLists {
    public WklSubgraphConditionVerifierForAdjacencyLists() {
        int[][] exampleAdjacencyLists = new int[][]{
            new int[]{2, 5}, // 0
            new int[]{3, 5, 6}, // 1
            new int[]{0}, // 2
            new int[]{1, 4}, // 3
            new int[]{3}, // 4
            new int[]{0, 1, 6}, // 5
            new int[]{1, 5, 7}, // 6
            new int[]{6} // 7
        };
        int exampleN = exampleAdjacencyLists.length;

        int[] exampleBottlenecks = new int[]{0, 1};
        int exampleK = exampleBottlenecks.length;

        int[] exampleMedals = new int[]{2, 4};
        int exampleL = exampleMedals.length;

        int[][] exampleLanyards = new int[][]{
            new int[]{}, // L_0,2
            new int[]{}, // L_0,4
            new int[]{}, // L_1,2
            new int[]{3} // L_1,4
        };
        int exampleLanyardVertexCount = 0;
        for (int[] exampleLanyard : exampleLanyards) {
            exampleLanyardVertexCount += exampleLanyard.length;
        }

        System.out.println(violatesWklSubgraphConditionForHamiltonianPaths(exampleAdjacencyLists, exampleN, exampleBottlenecks, exampleK, exampleMedals, exampleL, exampleLanyards, exampleLanyardVertexCount));
    }

    // A recursive function that finds biconnected components using depth-first search traversal
    // u --> The vertex to be visited next
    // discoveryTime[] --> Stores discovery times of visited vertices
    // low[] -- >> earliest visited vertex (the vertex with minimum discovery time) that can be reached from subtree rooted with current vertex
    // stack -- >> To store visited edges
    void biconnectedComponent(int[][] adjacencyLists, int u, int[] timeAndBCCcount, int[] discoveryTime, int[] low, LinkedList<int[]> stack, int[] parent, int[][] BCCs, LinkedList<int[]> temp) {
        timeAndBCCcount[0]++; // time++
        // Initialize discovery time and low value
        discoveryTime[u] = timeAndBCCcount[0];
        low[u] = timeAndBCCcount[0];
        int children = 0;

        // Go through all neighbors of u
        int[] adjacencyList = adjacencyLists[u];
        for (int v : adjacencyList) { // v is current neighbor of u
            // If v is not visited yet, then recur for it
            if (discoveryTime[v] == -1) {
                children++;
                parent[v] = u;

                // Store the edge in stack
                stack.push(new int[]{u, v});

                biconnectedComponent(adjacencyLists, v, timeAndBCCcount, discoveryTime, low, stack, parent, BCCs, temp);

                // Check if the subtree rooted with v has a connection to one of the ancestors of u
                // Case 1 - per Strongly Connected Components Article
                if (low[u] > low[v]) {
                    low[u] = low[v];
                }

                // If u is an articulation point, pop all edges from stack
                if ((discoveryTime[u] == 1 && children > 1) || (discoveryTime[u] > 1 && low[v] >= discoveryTime[u])) {
                    // Collect only necessary edges
                    int[] edge;
                    while (stack.peek()[0] != u || stack.peek()[1] != v) {
                        edge = stack.pop();
                        temp.push(edge);
                    }
                    edge = stack.pop();
                    temp.push(edge);
                    // Add collected edges to biconnected component
                    int BCCcount = timeAndBCCcount[1];
                    BCCs[BCCcount] = new int[(temp.size()) * 2];
                    int[] BCC = BCCs[BCCcount];
                    int j = 0;
                    while (!temp.isEmpty()) {
                        edge = temp.pop();
                        BCC[j] = edge[0];
                        BCC[j + 1] = edge[1];
                        j += 2;
                    }
                    timeAndBCCcount[1]++; // BCC count++
                }
            }

            // Update low value of u only if v is still in stack (i.e. it's a back edge, not cross edge)
            // Case 2 - per Strongly Connected Components Article
            else if (v != parent[u] && discoveryTime[v] < discoveryTime[u] ) {
                if (low[u] > discoveryTime[v]) {
                    low[u] = discoveryTime[v];
                }
                stack.push(new int[]{u, v});
            }
        }
    }

    // The function to do depth-first search traversal using biconnectedComponent()
    int getBiconnectedComponents(int[][] adjacencyLists, int n, int[][] BCCs) {
        int[] discoveryTime = new int[n];
        int[] low = new int[n];
        int[] parent = new int[n];
        LinkedList<int[]> stack = new LinkedList<>();
        LinkedList<int[]> temp = new LinkedList<>();
        int i, j, BCCcount;
        // Initialize discovery time, low, and parent arrays
        for (i = 0; i < n; i++) {
            discoveryTime[i] = -1;
            low[i] = -1;
            parent[i] = -1;
        }
        int[] timeAndBCCcount = new int[]{0, 0}; // {time, BCC count}

        int[] edge, BCC;

        for (i = 0; i < n; i++) {
            if (discoveryTime[i] == -1) {
                biconnectedComponent(adjacencyLists, i, timeAndBCCcount, discoveryTime, low, stack, parent, BCCs, temp);
            }
            // If stack is not empty, pop all edges from stack
            if (!stack.isEmpty()) {
                BCCcount = timeAndBCCcount[1];
                BCCs[BCCcount] = new int[stack.size() * 2];
                BCC = BCCs[BCCcount];
                j = 0;
                while (!stack.isEmpty()) {
                    edge = stack.pop();
                    BCC[j] = edge[0];
                    BCC[j + 1] = edge[1];
                    j += 2;
                }
                timeAndBCCcount[1]++; // BCC count++
            }
        }
        return timeAndBCCcount[1]; // return BCC count
    }
    // This code is contributed by Aakash Hasija

    void breadthFirstSearch(int[][] adjacencyLists, int vertex, boolean[] visited, Queue<Integer> queue) {
        visited[vertex] = true;
        queue.add(vertex);
        int current;
        int[] adjacencyList;
        while(!queue.isEmpty()) {
            current = queue.poll();
            adjacencyList = adjacencyLists[current];
            for (int neighbor : adjacencyList) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }

    boolean violatesWklSubgraphConditionForHamiltonianPaths(int[][] adjacencyLists, int n, int[] bottlenecks, int k, int[] medals, int l, int[][] lanyards, int lanyardVertexCount) {
        if (k >= l && k != l && l != k - 1) {
            System.out.println("Checking only accounts for W_k,l subgraphs where k < l or W_k,k and W_k,k-1 subgraphs");
            return false;
        }
        WklSubgraphVerifierForAdjacencyLists subgraphVerifier = new WklSubgraphVerifierForAdjacencyLists();
        int[] WklChecklist = new int[n];
        int i;
        for (i = 0; i < n; i++) {
            WklChecklist[i] = -1;
        }
        if (!subgraphVerifier.areValidAdjacencyLists(adjacencyLists, n, WklChecklist)) {
            System.out.println("Invalid adjacency lists");
            return false;
        }
        if (!subgraphVerifier.isWklSubgraph(adjacencyLists, n, bottlenecks, k, medals, l, lanyards, lanyardVertexCount, WklChecklist)) {
            System.out.println("Invalid W_k,l subgraph");
            return false;
        }
        if (k < l) {
            if (k + l + lanyardVertexCount < n) {
                System.out.println("The W_k,l subgraph condition, where k < l, is violated because there are vertices outside the subgraph");
                return true;
            } else {
                System.out.println("The W_k,l subgraph condition, where k < l, is NOT violated because there are no vertices outside the subgraph");
                return false;
            }
        }
        // Otherwise, k >= l
        // Get biconnected components
        int[][] BCCs = new int[n][];
        int BCCcount = getBiconnectedComponents(adjacencyLists, n, BCCs);
        // Count intersecting biconnected components
        int intersectingBCCcount = 0;
        int[] BCC;
        int vertexInsideSubgraph;
        int vertexOutsideSubgraph;
        boolean[] inIntersectingBCC = new boolean[n];
        LinkedList<Integer> intersectingBCCindices = new LinkedList<>();
        LinkedList<Integer> externalBCCindices = new LinkedList<>();
        for (i = 0; i < BCCcount; i++) {
            BCC = BCCs[i];
            vertexInsideSubgraph = -1;
            vertexOutsideSubgraph = -1;
            for (int BCCvertex : BCC) {
                if (WklChecklist[BCCvertex] == -1) {
                    vertexOutsideSubgraph = BCCvertex;
                    if (externalBCCindices.isEmpty() || externalBCCindices.getLast() != i) {
                        // Biconnected component is not contained in the subgraph
                        externalBCCindices.add(i);
                    }
                } else {
                    vertexInsideSubgraph = BCCvertex;
                }
                if (vertexInsideSubgraph >= 0 && vertexOutsideSubgraph >= 0) {
                    // Biconnected component intersects with the subgraph
                    intersectingBCCcount++;
                    inIntersectingBCC[vertexOutsideSubgraph] = true;
                    intersectingBCCindices.add(i);
                    break;
                }
            }
        }
        if (intersectingBCCcount == 0 || (l == k - 1 && intersectingBCCcount < 3)) {
            System.out.println("Not enough intersecting biconnected components");
            return false;
        }
        // Mark all vertices in the subgraph so that breadth-first search must find paths outside the subgraph
        boolean[] visited = new boolean[n];
        for (i = 0; i < n; i++) {
            if (WklChecklist[i] != -1) {
                visited[i] = true;
            }
        }
        // Count biconnected components not connected to each other outside the subgraph
        int disconnectedBCCcount = 0;
        Queue<Integer> queue = new LinkedList<>();
        for (i = 0; i < n; i++) {
            if (inIntersectingBCC[i] && !visited[i]) {
                // Vertex i in an intersecting biconnected component is not yet visited by breadth-first search
                disconnectedBCCcount++;
                breadthFirstSearch(adjacencyLists, i, visited, queue);
            }
        }
        if (l == k - 1) {
            if (disconnectedBCCcount >= 3) {
                System.out.println("The W_k,k-1 subgraph condition is violated because the subgraph intersects with at least 3 externally disconnected biconnected components");
                return true;
            } else {
                System.out.println("The W_k,k-1 subgraph condition is NOT violated because the subgraph intersects with less than 3 externally disconnected biconnected components");
                return false;
            }
        }
        // Otherwise, k == l
        if (disconnectedBCCcount >= 2) {
            System.out.println("The W_k,k subgraph condition is violated because the subgraph intersects with at least 2 externally disconnected biconnected components");
            return true;
        }
        // Otherwise, check if an intersecting biconnected component intersects with at least 2 biconnected components
        for (i = 0; i < n; i++) {
            inIntersectingBCC[i] = false;
        }
        for (int x : intersectingBCCindices) {
            // Mark all vertices in the xth biconnected component
            BCC = BCCs[x];
            for (int BCCvertex : BCC) {
                inIntersectingBCC[BCCvertex] = true;
            }
            // Go through all other biconnected components not contained in the subgraph
            intersectingBCCcount = 0;
            for (int y : externalBCCindices) {
                if (x != y) {
                    BCC = BCCs[y];
                    vertexInsideSubgraph = -1;
                    vertexOutsideSubgraph = -1;
                    for (int BCCvertex : BCC) {
                        if (inIntersectingBCC[BCCvertex]) {
                            // Vertex inside the intersecting biconnected component
                            vertexInsideSubgraph = 0;
                            if (WklChecklist[BCCvertex] == -1) {
                                // Vertex is also outside the subgraph
                                vertexOutsideSubgraph = 0;
                            }
                        } else if (WklChecklist[BCCvertex] == -1) {
                            // Vertex outside the intersecting biconnected component and outside the subgraph
                            vertexOutsideSubgraph = 0;
                        }
                        if (vertexInsideSubgraph == 0 && vertexOutsideSubgraph == 0) {
                            intersectingBCCcount++;
                            if (intersectingBCCcount >= 2) {
                                System.out.println("The W_k,k subgraph condition is violated because the subgraph intersects with a biconnected component that intersects with at least 2 biconnected components");
                                return true;
                            }
                            break;
                        }
                    }
                }
            }
            // Unmark all vertices in the xth biconnected component
            BCC = BCCs[x];
            for (int BCCvertex : BCC) {
                inIntersectingBCC[BCCvertex] = false;
            }
        }
        System.out.println("No W_k,l subgraph condition violations found");
        return false;
    }

    boolean has3W11Subgraphs(int[][] adjacencyLists, int n, int[] threeBottlenecks, int[] threeMedals, int[][] threeLanyards) {
        if (threeBottlenecks.length < 3) {
            System.out.println("Not enough bottleneck vertices");
            return false;
        }
        if (threeMedals.length < 3) {
            System.out.println("Not enough medal vertices");
            return false;
        }
        if (threeLanyards.length < 3) {
            System.out.println("Not enough lanyards");
            return false;
        }
        WklSubgraphVerifierForAdjacencyLists subgraphVerifier = new WklSubgraphVerifierForAdjacencyLists();
        int[] WklChecklist = new int[n];
        int i, j;
        for (i = 0; i < n; i++) {
            WklChecklist[i] = -1;
        }
        if (!subgraphVerifier.areValidAdjacencyLists(adjacencyLists, n, WklChecklist)) {
            System.out.println("Invalid adjacency lists");
            return false;
        }
        // Verify 3 subgraphs
        int[] bottlenecks = new int[1];
        int[] medals = new int[1];
        int[][] lanyards = new int[1][];
        for (i = 0; i < 3; i++) {
            bottlenecks[0] = threeBottlenecks[i];
            medals[0] = threeMedals[i];
            lanyards[0] = threeLanyards[i];
            if (!subgraphVerifier.isWklSubgraph(adjacencyLists, n, bottlenecks, 1, medals, 1, lanyards, threeLanyards[i].length, WklChecklist)) {
                System.out.println(i + "th subgraph is not a W_1,1 subgraph");
                return false;
            }
            for (j = 0; j < n; j++) {
                WklChecklist[j] = -1;
            }
        }
        // Ensure 3 subgraphs do not share medal vertices and lanyard vertices
        int[] lanyard;
        for (i = 0; i < 3; i++) {
            if (subgraphVerifier.inChecklist(threeMedals[i], WklChecklist, 0, n)) {
                System.out.println("Medal vertex " + threeMedals[i] + " is shared by the W_1,1 subgraphs");
                return false;
            }
            lanyard = threeLanyards[i];
            for (int lanyardVertex : lanyard) {
                if (subgraphVerifier.inChecklist(lanyardVertex, WklChecklist, 0, n)) {
                    System.out.println("Lanyard vertex " + lanyardVertex + " is shared by the W_1,1 subgraphs");
                    return false;
                }
            }
        }
        System.out.println("3 W_1,1 subgraphs found");
        return true;
    }

    boolean violatesWklSubgraphConditionForHamiltonianCycles(int[][] adjacencyLists, int n, int[] bottlenecks, int k, int[] medals, int l, int[][] lanyards, int lanyardVertexCount) {
        if (k > l) {
            System.out.println("Checking does not account for W_k,l subgraphs where k > l");
            return false;
        }
        WklSubgraphVerifierForAdjacencyLists subgraphVerifier = new WklSubgraphVerifierForAdjacencyLists();
        int[] WklChecklist = new int[n];
        for (int i = 0; i < n; i++) {
            WklChecklist[i] = -1;
        }
        if (!subgraphVerifier.areValidAdjacencyLists(adjacencyLists, n, WklChecklist)) {
            System.out.println("Invalid adjacency lists");
            return false;
        }
        if (!subgraphVerifier.isWklSubgraph(adjacencyLists, n, bottlenecks, k, medals, l, lanyards, lanyardVertexCount, WklChecklist)) {
            System.out.println("Invalid W_k,l subgraph");
            return false;
        }
        if (k < l) {
            System.out.println("The W_k,l subgraph condition, where k < l, is violated because there is a W_k,l subgraph where k < l");
            return true;
        }
        // Otherwise, k == l
        if (k + l + lanyardVertexCount < n) {
            System.out.println("The W_k,k subgraph condition is violated because there are vertices outside the subgraph");
            return true;
        } else {
            System.out.println("The W_k,k subgraph condition is NOT violated because there are no vertices outside the subgraph");
            return false;
        }
    }

    boolean hasW11Subgraph(int[][] adjacencyLists, int n, int bottleneck, int medal, int[] lanyard) {
        WklSubgraphVerifierForAdjacencyLists subgraphVerifier = new WklSubgraphVerifierForAdjacencyLists();
        int[] WklChecklist = new int[n];
        for (int i = 0; i < n; i++) {
            WklChecklist[i] = -1;
        }
        if (!subgraphVerifier.areValidAdjacencyLists(adjacencyLists, n, WklChecklist)) {
            System.out.println("Invalid adjacency lists");
            return false;
        }
        int[][] lanyards = new int[1][];
        lanyards[0] = lanyard;
        if (!subgraphVerifier.isWklSubgraph(adjacencyLists, n, new int[]{bottleneck}, 1, new int[]{medal}, 1, lanyards, lanyard.length, WklChecklist)) {
            System.out.println("Invalid W_1,1 subgraph");
            return false;
        }
        System.out.println("W_1,1 subgraph found");
        return true;
    }
}

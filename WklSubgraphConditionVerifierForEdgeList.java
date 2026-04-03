import java.util.LinkedList;
import java.util.Queue;

// This code does not find violations. It verifies violations.

public class WklSubgraphConditionVerifierForEdgeList {
    public WklSubgraphConditionVerifierForEdgeList() {
        int exampleN = 8;

        int[][] exampleEdgeList = new int[][]{
            new int[]{0, 2},
            new int[]{0, 5},
            new int[]{1, 3},
            new int[]{1, 5},
            new int[]{1, 6},
            new int[]{3, 4},
            new int[]{5, 6},
            new int[]{6, 7}
        };
        int exampleEdgeCount = exampleEdgeList.length;

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

        System.out.println(violatesWklSubgraphConditionForHamiltonianPaths(exampleN, exampleEdgeList, exampleEdgeCount, exampleBottlenecks, exampleK, exampleMedals, exampleL, exampleLanyards, exampleLanyardVertexCount));
    }

    byte compareEdges(int u, int v, int x, int y) {
        if (u < x) {
            return -1;
        }
        if (u > x) {
            return 1;
        }
        // u == x
        if (v < y) {
            return -1;
        }
        if (v > y) {
            return 1;
        }
        // (u, v) == (x, y)
        return 0;
    }

    int binarySearchEdge(int u, int v, int[][] edgeList, int edgeCount) {
        int low = 0;
        int high = edgeCount - 1;
        int mid;
        int[] edge;
        byte comparison;
        while (low <= high) {
            mid = (low + high) / 2;
            edge = edgeList[mid];
            comparison = compareEdges(u, v, edge[0], edge[1]);
            if (comparison == 0) {
                // Edge found
                return mid;
            } else if (comparison < 0) {
                // Search left
                high = mid - 1;
            } else {
                // Search right
                low = mid + 1;
            }
        }
        // Edge not found
        return -1;
    }

    int nextNeighbor(int n, int[][] edgeList, int edgeCount, int u, int previousNeighbor) {
        // Binary search every vertex to find neighbors
        int v = previousNeighbor;
        int edgeIndex = -1;
        do {
            v++;
            if (u < v) {
                edgeIndex = binarySearchEdge(u, v, edgeList, edgeCount);
            } else if (u > v) {
                edgeIndex = binarySearchEdge(v, u, edgeList, edgeCount);
            }
        } while (v < n && edgeIndex < 0);
        return v;
    }

    // A recursive function that finds biconnected components using depth-first search traversal
    // u --> The vertex to be visited next
    // discoveryTime[] --> Stores discovery times of visited vertices
    // low[] -- >> earliest visited vertex (the vertex with minimum discovery time) that can be reached from subtree rooted with current vertex
    // stack -- >> To store visited edges
    void biconnectedComponent(int n, int[][] edgeList, int edgeCount, int u, int[] timeAndBCCcount, int[] discoveryTime, int[] low, LinkedList<int[]> stack, int[] parent, int[][] BCCs, LinkedList<int[]> temp) {
        timeAndBCCcount[0]++; // time++
        // Initialize discovery time and low value
        discoveryTime[u] = timeAndBCCcount[0];
        low[u] = timeAndBCCcount[0];
        int children = 0;

        // Go through all neighbors of u
        int v = -1;
        while (v < n) {
            v = nextNeighbor(n, edgeList, edgeCount, u, v); // v is current neighbor of u
            if (v == n) {
                return;
            }
            // If v is not visited yet, then recur for it
            if (discoveryTime[v] == -1) {
                children++;
                parent[v] = u;

                // Store the edge in stack
                stack.push(new int[]{u, v});

                biconnectedComponent(n, edgeList, edgeCount, v, timeAndBCCcount, discoveryTime, low, stack, parent, BCCs, temp);

                // Check if the subtree rooted with v has a connection to one of the ancestors of u
                // Case 1 - per Strongly Connected Components Article
                if (low[u] > low[v]) {
                    low[u] = low[v];
                }

                // If u is an articulation point, pop all edges from stack
                if ((discoveryTime[u] == 1 && children > 1) || (discoveryTime[u] > 1 && low[v] >= discoveryTime[u])) {
                    // Collect edges
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
    int getBiconnectedComponents(int n, int[][] edgeList, int edgeCount, int[][] BCCs, boolean[] BCCchecklist) {
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
                biconnectedComponent(n, edgeList, edgeCount, i, timeAndBCCcount, discoveryTime, low, stack, parent, BCCs, temp);
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

        // Remove duplicate vertices from biconnected components
        BCCcount = timeAndBCCcount[1];
        for (i = 0; i < BCCcount; i++) {
            j = 0; // BCC vertex count
            for (int vertex : BCCs[i]) {
                if (!BCCchecklist[vertex]) {
                    BCCchecklist[vertex] = true;
                    j++;
                }
            }
            BCC = new int[j];
            j = 0;
            for (int vertex : BCCs[i]) {
                if (BCCchecklist[vertex]) {
                    BCCchecklist[vertex] = false;
                    BCC[j] = vertex;
                    j++;
                }
            }
            BCCs[i] = BCC;
        }
        return BCCcount; // return BCC count
    }
    // This code is contributed by Aakash Hasija

    void breadthFirstSearch(int n, int[][] edgeList, int edgeCount, int vertex, boolean[] visited, Queue<Integer> queue) {
        visited[vertex] = true;
        queue.add(vertex);
        int current, neighbor;
        while(!queue.isEmpty()) {
            current = queue.poll();
            neighbor = -1;
            while (neighbor < n) {
                neighbor = nextNeighbor(n, edgeList, edgeCount, current, neighbor);
                if (neighbor < n && !visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }

    boolean violatesWklSubgraphConditionForHamiltonianPaths(int n, int[][] edgeList, int edgeCount, int[] bottlenecks, int k, int[] medals, int l, int[][] lanyards, int lanyardVertexCount) {
        if (k >= l && k != l && l != k - 1) {
            System.out.println("Checking only accounts for W_k,l subgraphs where k < l or W_k,k and W_k,k-1 subgraphs");
            return false;
        }
        WklSubgraphVerifierForEdgeList subgraphVerifier = new WklSubgraphVerifierForEdgeList();
        if (!subgraphVerifier.isValidEdgeList(n, edgeList, edgeCount)) {
            System.out.println("Invalid edge list");
            return false;
        }
        int[] WklChecklist = new int[n];
        int i;
        if (!subgraphVerifier.isWklSubgraph(n, edgeList, edgeCount, bottlenecks, k, medals, l, lanyards, lanyardVertexCount, WklChecklist)) {
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
        boolean[] visited = new boolean[n];
        int BCCcount = getBiconnectedComponents(n, edgeList, edgeCount, BCCs, visited);
        // Count intersecting biconnected components
        int intersectingBCCcount = 0;
        int[] BCC;
        int vertexInsideSubgraph;
        int vertexOutsideSubgraph;
        boolean[] inIntersectingBCC = new boolean[n];
        LinkedList<Integer> intersectingBCCindices = new LinkedList<>();
        boolean[] isExternalBCC = new boolean[BCCcount];
        for (i = 0; i < BCCcount; i++) {
            BCC = BCCs[i];
            vertexInsideSubgraph = -1;
            vertexOutsideSubgraph = -1;
            for (int BCCvertex : BCC) {
                if (WklChecklist[BCCvertex] == -1) {
                    // Biconnected component is not contained in the subgraph
                    vertexOutsideSubgraph = BCCvertex;
                    isExternalBCC[i] = true;
                } else {
                    // Biconnected component intersects with the subgraph
                    vertexInsideSubgraph = BCCvertex;
                }
                if (vertexInsideSubgraph >= 0 && vertexOutsideSubgraph >= 0) {
                    // Biconnected component is not contained in the subgraph and intersects with the subgraph
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
                breadthFirstSearch(n, edgeList, edgeCount, i, visited, queue);
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
        // Get external biconnected component indices of articulation points
        int[] BCC1ofVertex = new int[n];
        int[] BCC2ofVertex = new int[n];
        for (i = 0; i < n; i++) {
            BCC1ofVertex[i] = -1;
            BCC2ofVertex[i] = -1;
        }
        for (i = 0; i < BCCcount; i++) {
            if (isExternalBCC[i]) {
                BCC = BCCs[i];
                for (int BCCvertex : BCC) {
                    if (BCC1ofVertex[BCCvertex] < 0) {
                        BCC1ofVertex[BCCvertex] = i;
                    } else if (BCC2ofVertex[BCCvertex] < 0) {
                        BCC2ofVertex[BCCvertex] = i;
                    }
                }
            }
        }
        int intersectingBCCindex, BCCindex;
        for (int j : intersectingBCCindices) {
            BCC = BCCs[j];
            intersectingBCCindex = -1;
            for (int BCCvertex : BCC) {
                BCCindex = BCC1ofVertex[BCCvertex];
                if (BCCindex >= 0 && BCCindex != j && isExternalBCC[BCCindex]) {
                    if (intersectingBCCindex < 0) {
                        intersectingBCCindex = BCCindex;
                    } else {
                        System.out.println("The W_k,k subgraph condition is violated because the subgraph intersects with a biconnected component that intersects with at least 2 biconnected components");
                        return true;
                    }
                }
                BCCindex = BCC2ofVertex[BCCvertex];
                if (BCCindex >= 0 && BCCindex != j && isExternalBCC[BCCindex]) {
                    if (intersectingBCCindex < 0) {
                        intersectingBCCindex = BCCindex;
                    } else {
                        System.out.println("The W_k,k subgraph condition is violated because the subgraph intersects with a biconnected component that intersects with at least 2 biconnected components");
                        return true;
                    }
                }
            }
        }
        System.out.println("No W_k,l subgraph condition violations found");
        return false;
    }

    boolean has3W11Subgraphs(int n, int[][] edgeList, int edgeCount, int[] threeBottlenecks, int[] threeMedals, int[][] threeLanyards) {
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
        WklSubgraphVerifierForEdgeList subgraphVerifier = new WklSubgraphVerifierForEdgeList();
        int[] WklChecklist = new int[n];
        int i, j;
        for (i = 0; i < n; i++) {
            WklChecklist[i] = -1;
        }
        if (!subgraphVerifier.isValidEdgeList(n, edgeList, edgeCount)) {
            System.out.println("Invalid edge list");
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
            if (!subgraphVerifier.isWklSubgraph(n, edgeList, edgeCount, bottlenecks, 1, medals, 1, lanyards, threeLanyards[i].length, WklChecklist)) {
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

    boolean violatesWklSubgraphConditionForHamiltonianCycles(int n, int[][] edgeList, int edgeCount, int[] bottlenecks, int k, int[] medals, int l, int[][] lanyards, int lanyardVertexCount) {
        if (k > l) {
            System.out.println("Checking does not account for W_k,l subgraphs where k > l");
            return false;
        }
        WklSubgraphVerifierForEdgeList subgraphVerifier = new WklSubgraphVerifierForEdgeList();
        if (!subgraphVerifier.isValidEdgeList(n, edgeList, edgeCount)) {
            System.out.println("Invalid edge list");
            return false;
        }
        if (!subgraphVerifier.isWklSubgraph(n, edgeList, edgeCount, bottlenecks, k, medals, l, lanyards, lanyardVertexCount, new int[n])) {
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

    boolean hasW11Subgraph(int n, int[][] edgeList, int edgeCount, int bottleneck, int medal, int[] lanyard) {
        WklSubgraphVerifierForEdgeList subgraphVerifier = new WklSubgraphVerifierForEdgeList();
        int[] WklChecklist = new int[n];
        for (int i = 0; i < n; i++) {
            WklChecklist[i] = -1;
        }
        if (!subgraphVerifier.isValidEdgeList(n, edgeList, edgeCount)) {
            System.out.println("Invalid edge list");
            return false;
        }
        int[][] lanyards = new int[1][];
        lanyards[0] = lanyard;
        if (!subgraphVerifier.isWklSubgraph(n, edgeList, edgeCount, new int[]{bottleneck}, 1, new int[]{medal}, 1, lanyards, lanyard.length, WklChecklist)) {
            System.out.println("Invalid W_1,1 subgraph");
            return false;
        }
        System.out.println("W_1,1 subgraph found");
        return true;
    }
}

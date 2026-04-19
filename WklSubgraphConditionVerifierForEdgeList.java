import java.util.LinkedList;
import java.util.Queue;

// This code is not for finding violations. It is for verifying violations.

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

        //                                     0   1   2  3   4   5   6   7
        int[] exampleBottleneckOf = new int[]{-2, -2, -3, 1, -3, -1, -1, -1};
        int exampleK = 2;

        //                               0  1  2  3  4  5  6  7
        int[] exampleMedalOf = new int[]{0, 0, 0, 4, 0, 0, 0, 0};
        int exampleL = 2;

        System.out.println(violatesWkkSubgraphConditionForHamiltonianPaths(exampleN, exampleEdgeList, exampleEdgeCount, exampleK, exampleL, exampleBottleneckOf, exampleMedalOf));
    }

    boolean isValidEdgeList(int n, int[][] edgeList, int edgeCount) {
        if (n < 1) {
            System.out.println("n should not be less than 1");
            return false;
        }
        if (edgeList == null) {
            System.out.println("Edge list is null");
            return false;
        }
        int maxEdgeCount = (n * (n - 1)) / 2;
        if (edgeCount < 0 || edgeCount > maxEdgeCount) {
            System.out.println("Edge count is out of range 0 to " + maxEdgeCount);
            return false;
        }
        if (edgeList.length < edgeCount) {
            System.out.println("There are less than " + edgeCount + " edges");
            return false;
        }
        // Ensure each vertex is within range, the edge list is sorted, and each edge is sorted
        int lastEdgeIndex = edgeCount - 1;
        for (int i = 0; i < edgeCount; i++) {
            if (edgeList[i][0] < 0 || edgeList[i][0] >= n || edgeList[i][1] < 0 || edgeList[i][1] >= n) {
                System.out.println("Edge (" + edgeList[i][0] + ", " + edgeList[i][1] + ") has a vertex out of range 0 to " + (n - 1));
                return false;
            }
            if (i < lastEdgeIndex && (edgeList[i][0] > edgeList[i + 1][0] || (edgeList[i][0] == edgeList[i + 1][0] && edgeList[i][1] >= edgeList[i + 1][1]))) {
                System.out.println("Edge list is unsorted at " + i + "th edge");
                return false;
            }
            if (edgeList[i][0] >= edgeList[i][1]) {
                System.out.println("Edge (" + edgeList[i][0] + ", " + edgeList[i][1] + ") is unsorted");
                return false;
            }
        }
        // Edge list represents a valid graph
        return true;
    }

    // This code is not for finding W_k,l subgraphs. It is for verifying W_k,l subgraphs.
    boolean isWklSubgraph(int n, int[][] edgeList, int edgeCount, int k, int l, int[] bottleneckOf, int[] medalOf) {
        // n vertices in the graph, k bottleneck vertices, l medal vertices
        if (bottleneckOf == null || medalOf == null) {
            System.out.println("An array is null");
            return false;
        }
        if (bottleneckOf.length < n || medalOf.length < n) {
            System.out.println("An array is too short");
            return false;
        }
        if (k < 1) {
            System.out.println("k should not be less than 1");
            return false;
        }
        if (l < 0) {
            System.out.println("l should not be less than 0");
            return false;
        }
        int i, j, endpoint, edgeIndex, temp;
        // Count bottleneck vertices and medal vertices
        int bottleneckVertexCount = 0;
        int medalVertexCount = 0;
        for (i = 0; i < n; i++) {
            if (bottleneckOf[i] < -3) {
                System.out.println("Vertex " + i + " should be assigned as either a bottleneck vertex, a medal vertex, a lanyard vertex, or a vertex outside the subgraph");
                return false;
            }
            if (bottleneckOf[i] == -2) {
                // Vertex i is a bottleneck vertex
                bottleneckVertexCount++;
            } else if (bottleneckOf[i] == -3) {
                // Vertex i is a medal vertex
                medalVertexCount++;
            } else if (bottleneckOf[i] >= 0) {
                // Vertex i is a lanyard vertex
                endpoint = bottleneckOf[i];
                if (endpoint >= n) {
                    System.out.println("Bottleneck vertex " + endpoint + " of lanyard vertex " + i + " is out of range 0 to " + (n - 1));
                    return false;
                }
                if (bottleneckOf[endpoint] != -2) {
                    System.out.println("Endpoint " + endpoint + " of lanyard vertex " + i + " is not a bottleneck vertex");
                    return false;
                }
                endpoint = medalOf[i];
                if (endpoint < 0 || endpoint >= n) {
                    System.out.println("Medal vertex " + endpoint + " of lanyard vertex " + i + " is out of range 0 to " + (n - 1));
                    return false;
                }
                if (bottleneckOf[endpoint] != -3) {
                    System.out.println("Endpoint " + endpoint + " of lanyard vertex " + i + " is not a medal vertex");
                    return false;
                }
            }
        }
        if (bottleneckVertexCount != k) {
            System.out.println("There should be exactly k bottleneck vertices");
            return false;
        }
        if (medalVertexCount != l) {
            System.out.println("There should be exactly l medal vertices");
        }
        for (edgeIndex = 0; edgeIndex < edgeCount; edgeIndex++) {
            i = edgeList[edgeIndex][0];
            j = edgeList[edgeIndex][1];
            do { // Twice for (i, j) and (j, i)
                if (bottleneckOf[i] == -3) {
                    // Vertex i is a medal vertex
                    // Ensure medal vertices are an independent set with neighbors only in the subgraph
                    if (bottleneckOf[j] == -3) {
                        System.out.println("Independent set of medal vertices should not have edge (" + i + ", " + j + ")");
                        return false;
                    }
                    if (bottleneckOf[j] == -1) {
                        System.out.println("Medal vertex " + i + " has neighbor vertex " + j + " outside the subgraph");
                        return false;
                    }
                } else if (bottleneckOf[i] >= 0) {
                    // Vertex i is a lanyard vertex
                    // Ensure lanyard vertices have neighbors only in their own lanyards and endpoints
                    if (bottleneckOf[j] == -1) {
                        System.out.println("Lanyard vertex " + i + " has neighbor vertex " + j + " outside the subgraph");
                        return false;
                    }
                    if (bottleneckOf[j] == -2 && bottleneckOf[i] != j) {
                        System.out.println("Lanyard vertex " + i + " has neighbor bottleneck vertex " + j + " outside lanyard L_" + bottleneckOf[i] + "," + medalOf[i]);
                        return false;
                    }
                    if (bottleneckOf[j] == -3 && medalOf[i] != j) {
                        System.out.println("Lanyard vertex " + i + " has neighbor medal vertex " + j + " outside lanyard L_" + bottleneckOf[i] + "," + medalOf[i]);
                        return false;
                    }
                    // Otherwise, vertex j is also a lanyard vertex
                    if (bottleneckOf[j] >= 0 && (bottleneckOf[i] != bottleneckOf[j] || medalOf[i] != medalOf[j])) {
                        System.out.println("Lanyard vertex " + i + " has neighbor lanyard vertex " + j + " outside lanyard L_" + bottleneckOf[i] + "," + medalOf[i]);
                        return false;
                    }
                }
                // Swap i and j
                temp = i;
                i = j;
                j = temp;
            } while (i > j);
        }
        // Passed all checks
        return true;
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

    boolean violatesWklSubgraphCondition(int n, int[][] edgeList, int edgeCount, int k, int l, int[] bottleneckOf, int[] medalOf, int s) {
        if (!isValidEdgeList(n, edgeList, edgeCount)) {
            System.out.println("Invalid edge list");
            return false;
        }
        if (!isWklSubgraph(n, edgeList, edgeCount, k, l, bottleneckOf, medalOf)) {
            System.out.println("Invalid W_k,l subgraph");
            return false;
        }
        if (l - k >= s) {
            System.out.println("The W_k,l subgraph condition, where l - k >= " + s + ", is violated because there is a W_k,l subgraph where l - k >= " + s);
            return true;
        }
        // Otherwise, k - l >= 1 - s
        // Mark all vertices in the subgraph so that breadth-first search must find paths outside the subgraph
        boolean[] visited = new boolean[n];
        int i;
        for (i = 0; i < n; i++) {
            if (bottleneckOf[i] != -1) {
                visited[i] = true;
            }
        }
        // Count subgraphs not connected to each other outside the subgraph
        int disconnectedSubgraphCount = k - l + s;
        Queue<Integer> queue = new LinkedList<>();
        for (i = 0; i < n; i++) {
            if (!visited[i]) {
                // Vertex in an externally disconnected subgraph is not yet visited by breadth-first search
                disconnectedSubgraphCount--;
                if (disconnectedSubgraphCount == 0) {
                    System.out.println("The W_k,l subgraph condition, where k - l >= " + (1 - s) + ", is violated because the subgraph intersects with at least k - l + " + s + " externally disconnected subgraphs");
                    return true;
                }
                breadthFirstSearch(n, edgeList, edgeCount, i, visited, queue);
            }
        }
        System.out.println("No W_k,l subgraph condition violations found");
        return false;
    }

    boolean violatesWklSubgraphConditionForHamiltonianPaths(int n, int[][] edgeList, int edgeCount, int k, int l, int[] bottleneckOf, int[] medalOf) {
        return violatesWklSubgraphCondition(n, edgeList, edgeCount, k, l, bottleneckOf, medalOf, 2);
    }

    boolean violatesWkkSubgraphConditionForHamiltonianPaths(int n, int[][] edgeList, int edgeCount, int k, int l, int[] bottleneckOf, int[] medalOf) {
        if (k != l) {
            System.out.println("Checking only accounts for W_k,k subgraphs");
            return false;
        }
        if (!isValidEdgeList(n, edgeList, edgeCount)) {
            System.out.println("Invalid edge list");
            return false;
        }
        if (!isWklSubgraph(n, edgeList, edgeCount, k, l, bottleneckOf, medalOf)) {
            System.out.println("Invalid W_k,l subgraph");
            return false;
        }
        // Get biconnected components
        int[][] BCCs = new int[n][];
        boolean[] isExternalBCC = new boolean[n];
        int BCCcount = getBiconnectedComponents(n, edgeList, edgeCount, BCCs, isExternalBCC);
        // Mark biconnected components not contained in the subgraph, intersecting with the subgraph, and biconnected component indices of each vertex
        int[] BCC;
        boolean intersecting;
        LinkedList<Integer> intersectingBCCindices = new LinkedList<>();
        int[] BCC1ofVertex = new int[n];
        int[] BCC2ofVertex = new int[n];
        int i;
        for (i = 0; i < n; i++) {
            BCC1ofVertex[i] = -1;
            BCC2ofVertex[i] = -1;
        }
        for (i = 0; i < BCCcount; i++) {
            BCC = BCCs[i];
            intersecting = false;
            for (int BCCvertex : BCC) {
                if (bottleneckOf[BCCvertex] == -1) {
                    // Biconnected component is not contained in the subgraph
                    isExternalBCC[i] = true;
                } else if (!intersecting) {
                    // Biconnected component intersects with the subgraph
                    intersecting = true;
                    intersectingBCCindices.add(i);
                }
                if (BCC1ofVertex[BCCvertex] < 0) {
                    // Vertex is in the ith biconnected component
                    BCC1ofVertex[BCCvertex] = i;
                } else if (BCC2ofVertex[BCCvertex] < 0) {
                    // Vertex is also in the ith biconnected component
                    BCC2ofVertex[BCCvertex] = i;
                } else {
                    // Vertex is shared by at least 3 biconnected components
                    System.out.println("All W_k,l subgraph conditions are violated because vertex " + BCCvertex + " has criticality at least 3");
                    return true;
                }
            }
        }
        if (intersectingBCCindices.isEmpty()) {
            System.out.println("The W_k,k subgraph condition is not violated because the subgraph does not intersect with any biconnected component");
            return false;
        }
        // Check if an intersecting biconnected component intersects with at least 2 external biconnected components
        int intersectingBCCindex, BCCindex;
        for (int j : intersectingBCCindices) {
            BCC = BCCs[j];
            intersectingBCCindex = -1;
            // Check all vertices in the current biconnected component
            for (int BCCvertex : BCC) {
                // Check which external biconnected components contain the current vertex
                BCCindex = BCC1ofVertex[BCCvertex];
                if (BCCindex >= 0 && BCCindex != j && isExternalBCC[BCCindex] && intersectingBCCindex != BCCindex) {
                    if (intersectingBCCindex < 0) {
                        intersectingBCCindex = BCCindex;
                    } else {
                        System.out.println("The W_k,k subgraph condition is violated because the subgraph intersects with a biconnected component that intersects with at least 2 biconnected components");
                        return true;
                    }
                }
                BCCindex = BCC2ofVertex[BCCvertex];
                if (BCCindex >= 0 && BCCindex != j && isExternalBCC[BCCindex] && intersectingBCCindex != BCCindex) {
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

    boolean has3W11Subgraphs(int n, int[][] edgeList, int edgeCount, int[] bottlenecks1of, int[] bottlenecks2of, int[] bottlenecks3of, int[] medals1of, int[] medals2of, int[] medals3of) {
        if (!isValidEdgeList(n, edgeList, edgeCount)) {
            System.out.println("Invalid edge list");
            return false;
        }
        // Verify 3 subgraphs
        int[][] bottlenecksOf = new int[][]{bottlenecks1of, bottlenecks2of, bottlenecks3of};
        int[][] medalsOf = new int[][]{medals1of, medals2of, medals3of};
        int i, j;
        for (i = 0; i < 3; i++) {
            if (!isWklSubgraph(n, edgeList, edgeCount, 1, 1, bottlenecksOf[i], medalsOf[i])) {
                System.out.println(i + "th subgraph is not a W_1,1 subgraph");
                return false;
            }
        }
        // Ensure 3 subgraphs do not share medal vertices and lanyard vertices
        boolean[] WklChecklist = new boolean[n];
        for (i = 0; i < 3; i++) {
            for (j = 0; j < n; j++) {
                if (WklChecklist[j]) {
                    if (bottlenecksOf[i][j] == -3) {
                        System.out.println("Medal vertex " + j + " is shared by the W_1,1 subgraphs");
                        return false;
                    }
                    if (bottlenecksOf[i][j] >= 0) {
                        System.out.println("Lanyard vertex " + j + " is shared by the W_1,1 subgraphs");
                        return false;
                    }
                }
                WklChecklist[j] = true;
            }
        }
        System.out.println("3 W_1,1 subgraphs found");
        return true;
    }

    boolean violatesWklSubgraphConditionForHamiltonianCycles(int n, int[][] edgeList, int edgeCount, int k, int l, int[] bottleneckOf, int[] medalOf) {
        return violatesWklSubgraphCondition(n, edgeList, edgeCount, k, l, bottleneckOf, medalOf, 1);
    }

    boolean hasW11SubgraphAndVerticesOutside(int n, int[][] edgeList, int edgeCount, int[] bottleneckOf, int[] medalOf) {
        if (!isValidEdgeList(n, edgeList, edgeCount)) {
            System.out.println("Invalid edge list");
            return false;
        }
        if (!isWklSubgraph(n, edgeList, edgeCount, 1, 1, bottleneckOf, medalOf)) {
            System.out.println("Invalid W_1,1 subgraph");
            return false;
        }
        // Find a vertex outside the subgraph
        for (int i = 0; i < n; i++) {
            if (bottleneckOf[i] == -1) {
                System.out.println("W_1,1 subgraph and vertices outside the subgraph found");
                return true;
            }
        }
        System.out.println("W_1,1 subgraph found without vertices outside the subgraph");
        return false;
    }
}

import java.util.LinkedList;
import java.util.Queue;

// This code is not for finding violations. It is for verifying violations.

public class WklSubgraphConditionVerifierForAdjacencyMatrix {
    public WklSubgraphConditionVerifierForAdjacencyMatrix() {
        byte[][] exampleAdjacencyMatrix = new byte[][]{
            //         0  1  2  3  4  5  6  7
            new byte[]{0, 0, 1, 0, 0, 1, 0, 0}, // 0
            new byte[]{0, 0, 0, 1, 0, 1, 1, 0}, // 1
            new byte[]{1, 0, 0, 0, 0, 0, 0, 0}, // 2
            new byte[]{0, 1, 0, 0, 1, 0, 0, 0}, // 3
            new byte[]{0, 0, 0, 1, 0, 0, 0, 0}, // 4
            new byte[]{1, 1, 0, 0, 0, 0, 1, 0}, // 5
            new byte[]{0, 1, 0, 0, 0, 1, 0, 1}, // 6
            new byte[]{0, 0, 0, 0, 0, 0, 1, 0}, // 7
        };
        int exampleN = exampleAdjacencyMatrix.length;

        //                                     0   1   2  3   4   5   6   7
        int[] exampleBottleneckOf = new int[]{-2, -2, -3, 1, -3, -1, -1, -1};
        int exampleK = 2;

        //                               0  1  2  3  4  5  6  7
        int[] exampleMedalOf = new int[]{0, 0, 0, 4, 0, 0, 0, 0};
        int exampleL = 2;

        System.out.println(violatesWkkSubgraphConditionForHamiltonianPaths(exampleAdjacencyMatrix, exampleN, exampleK, exampleL, exampleBottleneckOf, exampleMedalOf));
    }

    boolean isValidAdjacencyMatrix(byte[][] adjacencyMatrix, int n) {
        if (n < 1) {
            System.out.println("n should not be less than 1");
            return false;
        }
        if (adjacencyMatrix == null) {
            System.out.println("Adjacency matrix is null");
            return false;
        }
        if (adjacencyMatrix.length < n) {
            System.out.println("Adjacency matrix has less than " + n + " rows");
            return false;
        }
        int i, j;
        for (i = 0; i < n; i++) {
            if (adjacencyMatrix[i] == null) {
                System.out.println(i + "th row of adjacency matrix is null");
                return false;
            }
            if (adjacencyMatrix[i].length < n) {
                System.out.println(i + "th row of adjacency matrix has less than " + n + " columns");
                return false;
            }
            for (j = i + 1; j < n; j++) {
                if (adjacencyMatrix[i][j] != 0 && adjacencyMatrix[i][j] != 1) {
                    System.out.println("Cell at " + i + "th row and " + j + "th column of adjacency matrix is not 0 or 1");
                    return false;
                }
                if (adjacencyMatrix[i][j] != adjacencyMatrix[j][i]) {
                    System.out.println("Adjacency matrix is asymmetric at " + i + "th row and " + j + "th column");
                    return false;
                }
            }
        }
        // Adjacency matrix represents a valid graph
        return true;
    }

    // This code is not for finding W_k,l subgraphs. It is for verifying W_k,l subgraphs.
    boolean isWklSubgraph(byte[][] adjacencyMatrix, int n, int k, int l, int[] bottleneckOf, int[] medalOf) {
        // n vertices in the graph, k bottleneck vertices, l medal vertices
        // bottleneckOf[w] == -1 if w is outside the W_k,l subgraph
        // bottleneckOf[w] == -2 if w is a bottleneck vertex
        // bottleneckOf[w] == -3 if w is a medal vertex
        // bottleneckOf[w] == u if w is a lanyard vertex in lanyard L_u,v
        // medalOf[w] == v if w is a lanyard vertex in lanyard L_u,v
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
        int i, j, endpoint;
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
        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                if (i != j && adjacencyMatrix[i][j] == 1) {
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
                }
            }
        }
        // Passed all checks
        return true;
    }

    void breadthFirstSearch(byte[][] adjacencyMatrix, int n, int vertex, boolean[] visited, Queue<Integer> queue) {
        visited[vertex] = true;
        queue.add(vertex);
        int current, neighbor;
        while(!queue.isEmpty()) {
            current = queue.poll();
            for (neighbor = 0; neighbor < n; neighbor++) {
                if (adjacencyMatrix[current][neighbor] == 1 && !visited[neighbor]) {
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
    void biconnectedComponent(byte[][] adjacencyMatrix, int n, int u, int[] timeAndBCCcount, int[] discoveryTime, int[] low, LinkedList<int[]> stack, int[] parent, int[][] BCCs, LinkedList<int[]> temp) {
        timeAndBCCcount[0]++; // time++
        // Initialize discovery time and low value
        discoveryTime[u] = timeAndBCCcount[0];
        low[u] = timeAndBCCcount[0];
        int children = 0;

        // Go through all neighbors of u
        for (int v = 0; v < n; v++) {
            if (adjacencyMatrix[u][v] == 1) { // v is current neighbor of u
                // If v is not visited yet, then recur for it
                if (discoveryTime[v] == -1) {
                    children++;
                    parent[v] = u;

                    // Store the edge in stack
                    stack.push(new int[]{u, v});

                    biconnectedComponent(adjacencyMatrix, n, v, timeAndBCCcount, discoveryTime, low, stack, parent, BCCs, temp);

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
    }

    // The function to do depth-first search traversal using biconnectedComponent()
    int getBiconnectedComponents(byte[][] adjacencyMatrix, int n, int[][] BCCs, boolean[] BCCchecklist) {
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
                biconnectedComponent(adjacencyMatrix, n, i, timeAndBCCcount, discoveryTime, low, stack, parent, BCCs, temp);
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

    boolean violatesWklSubgraphCondition(byte[][] adjacencyMatrix, int n, int k, int l, int[] bottleneckOf, int[] medalOf, int c) {
        if (!isValidAdjacencyMatrix(adjacencyMatrix, n)) {
            System.out.println("Invalid adjacency matrix");
            return false;
        }
        if (!isWklSubgraph(adjacencyMatrix, n, k, l, bottleneckOf, medalOf)) {
            System.out.println("Invalid W_k,l subgraph");
            return false;
        }
        if (l - k >= c) {
            System.out.println("The W_k,l subgraph condition, where l - k >= " + c + ", is violated because there is a W_k,l subgraph where l - k >= " + c);
            return true;
        }
        // Otherwise, k - l >= 1 - c
        // Mark all vertices in the W_k,l subgraph so that breadth-first search must find paths outside the W_k,l subgraph
        boolean[] visited = new boolean[n];
        int i;
        for (i = 0; i < n; i++) {
            if (bottleneckOf[i] != -1) {
                visited[i] = true;
            }
        }
        // Count subgraphs not connected to each other outside the W_k,l subgraph
        int disconnectedSubgraphCount = k - l + c;
        Queue<Integer> queue = new LinkedList<>();
        for (i = 0; i < n; i++) {
            if (!visited[i]) {
                // Vertex in an externally disconnected subgraph is not yet visited by breadth-first search
                disconnectedSubgraphCount--;
                if (disconnectedSubgraphCount == 0) {
                    System.out.println("The W_k,l subgraph condition, where k - l >= " + (1 - c) + ", is violated because the W_k,l subgraph intersects with at least k - l + " + c + " externally disconnected subgraphs");
                    return true;
                }
                breadthFirstSearch(adjacencyMatrix, n, i, visited, queue);
            }
        }
        System.out.println("No W_k,l subgraph condition violations found");
        return false;
    }

    boolean violatesWklSubgraphConditionForHamiltonianPaths(byte[][] adjacencyMatrix, int n, int k, int l, int[] bottleneckOf, int[] medalOf) {
        return violatesWklSubgraphCondition(adjacencyMatrix, n, k, l, bottleneckOf, medalOf, 2);
    }

    boolean violatesWkkSubgraphConditionForHamiltonianPaths(byte[][] adjacencyMatrix, int n, int k, int l, int[] bottleneckOf, int[] medalOf) {
        if (k != l) {
            System.out.println("Checking only accounts for W_k,k subgraphs");
            return false;
        }
        if (!isValidAdjacencyMatrix(adjacencyMatrix, n)) {
            System.out.println("Invalid adjacency matrix");
            return false;
        }
        if (!isWklSubgraph(adjacencyMatrix, n, k, l, bottleneckOf, medalOf)) {
            System.out.println("Invalid W_k,l subgraph");
            return false;
        }
        // Get biconnected components
        int[][] BCCs = new int[n][];
        boolean[] isExternalBCC = new boolean[n];
        int BCCcount = getBiconnectedComponents(adjacencyMatrix, n, BCCs, isExternalBCC);
        // Mark biconnected components not contained in the subgraph, intersecting with the subgraph, and biconnected component indices of each vertex
        int[] BCC;
        boolean intersecting;
        LinkedList<Integer> intersectingBCCindices = new LinkedList<>();
        int[] BCC1of = new int[n];
        int[] BCC2of = new int[n];
        int i;
        for (i = 0; i < n; i++) {
            BCC1of[i] = -1;
            BCC2of[i] = -1;
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
                if (BCC1of[BCCvertex] < 0) {
                    // Vertex is in the ith biconnected component
                    BCC1of[BCCvertex] = i;
                } else if (BCC2of[BCCvertex] < 0) {
                    // Vertex is also in the ith biconnected component
                    BCC2of[BCCvertex] = i;
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
                BCCindex = BCC1of[BCCvertex];
                if (BCCindex >= 0 && BCCindex != j && isExternalBCC[BCCindex] && intersectingBCCindex != BCCindex) {
                    if (intersectingBCCindex < 0) {
                        intersectingBCCindex = BCCindex;
                    } else {
                        System.out.println("The W_k,k subgraph condition is violated because the subgraph intersects with a biconnected component that intersects with at least 2 biconnected components");
                        return true;
                    }
                }
                BCCindex = BCC2of[BCCvertex];
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

    boolean has3W11Subgraphs(byte[][] adjacencyMatrix, int n, int[] bottlenecks1of, int[] bottlenecks2of, int[] bottlenecks3of, int[] medals1of, int[] medals2of, int[] medals3of) {
        if (!isValidAdjacencyMatrix(adjacencyMatrix, n)) {
            System.out.println("Invalid adjacency matrix");
            return false;
        }
        // Verify 3 subgraphs
        int[][] bottlenecksOf = new int[][]{bottlenecks1of, bottlenecks2of, bottlenecks3of};
        int[][] medalsOf = new int[][]{medals1of, medals2of, medals3of};
        int i, j;
        for (i = 0; i < 3; i++) {
            if (!isWklSubgraph(adjacencyMatrix, n, 1, 1, bottlenecksOf[i], medalsOf[i])) {
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

    boolean violatesWklSubgraphConditionForHamiltonianCycles(byte[][] adjacencyMatrix, int n, int k, int l, int[] bottleneckOf, int[] medalOf) {
        return violatesWklSubgraphCondition(adjacencyMatrix, n, k, l, bottleneckOf, medalOf, 1);
    }

    boolean hasW11SubgraphAndVerticesOutside(byte[][] adjacencyMatrix, int n, int[] bottleneckOf, int[] medalOf) {
        if (!isValidAdjacencyMatrix(adjacencyMatrix, n)) {
            System.out.println("Invalid adjacency matrix");
            return false;
        }
        if (!isWklSubgraph(adjacencyMatrix, n, 1, 1, bottleneckOf, medalOf)) {
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

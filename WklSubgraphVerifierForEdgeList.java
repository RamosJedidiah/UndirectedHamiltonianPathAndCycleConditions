// This code does not find W_k,l subgraphs. It verifies W_k,l subgraphs.

public class WklSubgraphVerifierForEdgeList {
    boolean inChecklist(int item, int[] checklist, int checkMark, int n) {
        // Check if item is in checklist
        if (item < 0 || item >= n) {
            System.out.println("Vertex " + item + " is out of range 0 to " + (n - 1));
            return true;
        }
        if (checklist[item] != -1) {
            System.out.println("Vertex " + item + " is a duplicate");
            return true;
        }
        // Add item to checklist
        checklist[item] = checkMark;
        return false;
    }

    boolean isValidEdgeList(int n, int[][] edgeList, int edgeCount) {
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

    boolean isWklSubgraph(int n, int[][] edgeList, int edgeCount, int[] bottlenecks, int k, int[] medals, int l, int[][] lanyards, int lanyardVertexCount, int[] checklist) {
        // n vertices in the graph, k bottleneck vertices, l medal vertices
        int i, j;
        if (bottlenecks == null || medals == null || lanyards == null) {
            System.out.println("An array is null");
            return false;
        }
        int kl = k * l;
        if (lanyards.length < kl || bottlenecks.length < k || medals.length < l) {
            System.out.println("An array is too short");
            return false;
        }
        // Count vertices of all lanyards
        j = 0;
        int[] lanyard;
        for (i = 0; i < kl; i++) {
            lanyard = lanyards[i];
            j += lanyard.length;
        }
        if (j != lanyardVertexCount) {
            System.out.println("Wrong lanyard vertex count");
            return false;
        }
        if (n < k + l + lanyardVertexCount || k < 1 || l < 0) {
            System.out.println("Invalid number input");
            return false;
        }

        // Ensure bottleneck vertices, medal vertices, and lanyard vertices are in the graph and do not have duplicates and shared vertices
        for (i = 0; i < n; i++) {
            checklist[i] = -1;
        }
        for (i = 0; i < k; i++) {
            if (inChecklist(bottlenecks[i], checklist, -2, n)) {
                // Bottleneck vertex is out of range, or duplicate bottleneck vertices found
                System.out.println("Bottleneck vertex " + bottlenecks[i] + " is invalid");
                return false;
            }
        }
        for (i = 0; i < l; i++) {
            if (inChecklist(medals[i], checklist, -3, n)) {
                // Medal vertex is out of range, or duplicate medal vertices found
                System.out.println("Medal vertex " + medals[i] + " is invalid");
                return false;
            }
        }
        for (i = 0; i < kl; i++) {
            lanyard = lanyards[i];
            for (int lanyardVertex : lanyard) {
                if (inChecklist(lanyardVertex, checklist, i, n)) {
                    // Lanyard vertex is out of range, or duplicate lanyard vertex found
                    System.out.println("Lanyard vertex " + lanyardVertex + " in the " + i + "th lanyard is invalid");
                    return false;
                }
            }
        }

        // Ensure medal vertices are an independent set with neighbors only in the subgraph, and lanyard vertices have neighbors only in their own lanyards and endpoints
        int a, b, temp, bottleneckEndpoint, medalEndpoint;
        for (i = 0; i < edgeCount; i++) {
            a = edgeList[i][0];
            b = edgeList[i][1];
            if (checklist[a] == -1) {
                // Swap adjacent vertices so that b is the outsider
                temp = a;
                a = b;
                b = temp;
            }
            if (checklist[a] != -1) {
                // a is in the subgraph
                if (checklist[a] == -2) {
                    // Swap adjacent vertices so that b is the bottleneck vertex
                    temp = a;
                    a = b;
                    b = temp;
                }
                if (checklist[a] == -3) {
                    // a is a medal vertex
                    if (checklist[b] == -3) {
                        System.out.println("Independent set of medal vertices should not have edge (" + a + ", " + b + ")");
                        return false;
                    }
                    if (checklist[b] == -1) {
                        System.out.println("Medal vertex " + a + " has neighbor vertex " + b + " outside the subgraph");
                        return false;
                    }
                    if (checklist[b] >= 0) {
                        // Swap adjacent vertices so that a is the lanyard vertex and b is the medal vertex
                        temp = a;
                        a = b;
                        b = temp;
                    }
                }
                if (checklist[a] >= 0) {
                    // a is a lanyard vertex
                    bottleneckEndpoint = bottlenecks[checklist[a] % k];
                    medalEndpoint = medals[checklist[a] / k];
                    if (b != bottleneckEndpoint && b != medalEndpoint && checklist[a] != checklist[b]) {
                        System.out.println("Lanyard vertex " + a + " has neighbor " + b + " outside the " + checklist[a] + "th lanyard");
                        return false;
                    }
                }
            }
        }

        // Passed all checks
        return true;
    }
}

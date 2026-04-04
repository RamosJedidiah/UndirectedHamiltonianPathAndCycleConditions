// This code is not for finding W_k,l subgraphs. It is for verifying W_k,l subgraphs.

public class WklSubgraphVerifierForAdjacencyMatrix {
    boolean inChecklist(int item, boolean[] checklist, int n) {
        // Check if item is in checklist
        if (item < 0 || item >= n) {
            System.out.println("Vertex " + item + " is out of range 0 to " + (n - 1));
            return true;
        }
        if (checklist[item]) {
            System.out.println("Vertex " + item + " is a duplicate");
            return true;
        }
        // Add item to checklist
        checklist[item] = true;
        return false;
    }

    void clearChecklist(boolean[] checklist, int[] vertexSubset, int vertexSubsetSize) {
        for (int i = 0; i < vertexSubsetSize; i++) {
            checklist[vertexSubset[i]] = false;
        }
    }

    boolean isEdge(int u, int v, byte[][] adjacencyMatrix) {
        return adjacencyMatrix[u][v] == 1;
    }

    boolean isValidAdjacencyMatrix(byte[][] adjacencyMatrix, int n) {
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

    boolean isWklSubgraph(byte[][] adjacencyMatrix, int n, int[] bottlenecks, int k, int[] medals, int l, int[][] lanyards, int lanyardVertexCount, boolean[] checklist) {
        // n vertices in the graph, k bottleneck vertices, l medal vertices
        int i, j, neighbor;
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
        for (i = 0; i < k; i++) {
            if (inChecklist(bottlenecks[i], checklist, n)) {
                // Bottleneck vertex is out of range, or duplicate bottleneck vertices found
                System.out.println("Bottleneck vertex " + bottlenecks[i] + " is invalid");
                return false;
            }
        }
        for (i = 0; i < l; i++) {
            if (inChecklist(medals[i], checklist, n)) {
                // Medal vertex is out of range, or duplicate medal vertices found
                System.out.println("Medal vertex " + medals[i] + " is invalid");
                return false;
            }
        }
        for (i = 0; i < kl; i++) {
            lanyard = lanyards[i];
            for (int lanyardVertex : lanyard) {
                if (inChecklist(lanyardVertex, checklist, n)) {
                    // Lanyard vertex is out of range, or duplicate lanyard vertex found
                    System.out.println("Lanyard vertex " + lanyardVertex + " in the " + i + "th lanyard is invalid");
                    return false;
                }
            }
        }

        // Ensure medal vertices are an independent set with neighbors only in the subgraph
        for (i = 0; i < l; i++) {
            for (j = i + 1; j < l; j++) {
                if (isEdge(medals[i], medals[j], adjacencyMatrix)) {
                    System.out.println("Independent set of medal vertices should not have edge (" + medals[i] + ", " + medals[j] + ")");
                    return false;
                }
            }
            for (neighbor = 0; neighbor < n; neighbor++) {
                if (isEdge(medals[i], neighbor, adjacencyMatrix) && !checklist[neighbor]) {
                    System.out.println("Medal vertex " + medals[i] + " has neighbor vertex " + neighbor + " outside the subgraph");
                    return false;
                }
            }
        }
        // No duplicates so far
        clearChecklist(checklist, bottlenecks, k);
        clearChecklist(checklist, medals, l);
        for (i = 0; i < kl; i++) {
            lanyard = lanyards[i];
            clearChecklist(checklist, lanyard, lanyard.length);
        }

        // Ensure lanyard vertices have neighbors only in their own lanyards and endpoints
        int lanyardIndex = 0;
        for (i = 0; i < k; i++) {
            for (j = 0; j < l; j++) {
                lanyard = lanyards[lanyardIndex];
                // Add current lanyard vertices in the checklist
                for (int lanyardVertex : lanyard) {
                    inChecklist(lanyardVertex, checklist, n);
                }
                // Check for neighbors outside the current lanyard
                for (int lanyardVertex : lanyard) {
                    for (neighbor = 0; neighbor < n; neighbor++) {
                        if (isEdge(lanyardVertex, neighbor, adjacencyMatrix) && neighbor != bottlenecks[i] && neighbor != medals[j] && !checklist[neighbor]) {
                            System.out.println("Lanyard vertex " + lanyardVertex + " has neighbor " + neighbor + " outside the " + lanyardIndex + "th lanyard");
                            return false;
                        }
                    }
                }
                clearChecklist(checklist, lanyard, lanyard.length);
                lanyardIndex++;
            }
        }

        // Passed all checks
        return true;
    }
}

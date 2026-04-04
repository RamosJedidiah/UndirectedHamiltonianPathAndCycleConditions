// This code is not for finding W_k,l subgraphs. It is for verifying W_k,l subgraphs.

public class WklSubgraphVerifierForAdjacencyLists {
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

    int binarySearchIndex(int target, int[] sortedArray, int n) {
        int low = 0;
        int high = n - 1;
        int mid;
        while (low <= high) {
            mid = (low + high) / 2;
            if (sortedArray[mid] == target) {
                // Target found
                return mid;
            } else if (sortedArray[mid] > target) {
                // Search left
                high = mid - 1;
            } else {
                // Search right
                low = mid + 1;
            }
        }
        // Target not found
        return -1;
    }

    boolean isEdge(int u, int v, int[][] adjacencyLists) {
        int[] adjacencyList = adjacencyLists[u];
        return binarySearchIndex(v, adjacencyList, adjacencyList.length) >= 0;
    }

    boolean areValidAdjacencyLists(int[][] adjacencyLists, int n, int[] checklist) {
        if (adjacencyLists == null) {
            System.out.println("Adjacency lists are null");
            return false;
        }
        if (adjacencyLists.length < n) {
            System.out.println("There are less than " + n + " adjacency lists");
            return false;
        }
        int[] adjacencyList;
        int previousNeighbor;
        for (int vertex = 0; vertex < n; vertex++) {
            adjacencyList = adjacencyLists[vertex];
            if (adjacencyList == null) {
                // Invalid input
                return false;
            }
            previousNeighbor = -1; // Previous neighbor in the adjacency list
            for (int neighbor : adjacencyList) {
                if (vertex == neighbor) {
                    System.out.println("Vertex " + vertex + " has an edge to itself");
                    return false;
                }
                if (previousNeighbor > neighbor) {
                    System.out.println("Adjacency list of vertex " + vertex + " is unsorted");
                    return false;
                }
                if (!isEdge(neighbor, vertex, adjacencyLists)) {
                    System.out.println("Vertex " + vertex + " has neighbor " + neighbor + ", but vertex " + neighbor + " does not have neighbor " + vertex);
                    return false;
                }
                // Update the previous neighbor in the adjacency list
                previousNeighbor = neighbor;
                if (inChecklist(neighbor, checklist, 1, n)) {
                    // Neighbor vertex is out of range, or duplicate neighbor vertices found
                    System.out.println("Neighbor " + neighbor + " of vertex " + vertex + " is invalid");
                    return false;
                }
            }
            // Clear checklist
            for (int neighbor : adjacencyList) {
                checklist[neighbor] = -1;
            }
        }
        // Adjacency lists represent a valid graph
        return true;
    }

    boolean isWklSubgraph(int[][] adjacencyLists, int n, int[] bottlenecks, int k, int[] medals, int l, int[][] lanyards, int lanyardVertexCount, int[] checklist) {
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

        // Ensure medal vertices are an independent set with neighbors only in the subgraph
        int[] adjacencyList;
        for (i = 0; i < l; i++) {
            adjacencyList = adjacencyLists[medals[i]];
            for (int neighbor : adjacencyList) {
                if (checklist[neighbor] == -3) {
                    System.out.println("Independent set of medal vertices should not have edge (" + medals[i] + ", " + neighbor + ")");
                    return false;
                }
                if (checklist[neighbor] == -1) {
                    System.out.println("Medal vertex " + medals[i] + " has neighbor vertex " + neighbor + " outside the subgraph");
                    return false;
                }
            }
        }

        // Ensure lanyard vertices have neighbors only in their own lanyards and endpoints
        int lanyardIndex = 0;
        for (i = 0; i < k; i++) {
            for (j = 0; j < l; j++) {
                lanyard = lanyards[lanyardIndex];
                // Check for neighbors outside the current lanyard
                for (int lanyardVertex : lanyard) {
                    adjacencyList = adjacencyLists[lanyardVertex];
                    for (int neighbor : adjacencyList) {
                        if (neighbor != bottlenecks[i] && neighbor != medals[j] && checklist[lanyardVertex] != checklist[neighbor]) {
                            System.out.println("Lanyard vertex " + lanyardVertex + " has neighbor " + neighbor + " outside the " + lanyardIndex + "th lanyard");
                            return false;
                        }
                    }
                }
                lanyardIndex++;
            }
        }

        // Passed all checks
        return true;
    }
}

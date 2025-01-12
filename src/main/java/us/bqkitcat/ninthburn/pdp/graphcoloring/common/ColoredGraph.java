package us.bqkitcat.ninthburn.pdp.graphcoloring.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ColoredGraph {
    
    private final List<List<Integer>> adjacencyList;
    private final List<Integer> nodeColors;
    public int colors;
    
    private ColoredGraph(List<List<Integer>> adjacencyList, List<Integer> nodeColors, int colors) {
        this.adjacencyList = adjacencyList;
        this.nodeColors = nodeColors;
        this.colors = colors;
    }

    public ColoredGraph(int vertices, int colorCount) {
        this.colors = colorCount;
        adjacencyList = new ArrayList<>();
        nodeColors = new ArrayList<>();

        for (int i = 0; i < vertices; i++) {
            adjacencyList.add(new ArrayList<>());
            nodeColors.add(-1);

        }
    }
    
    public ColoredGraph safeClone() {
        return new ColoredGraph(adjacencyList, new ArrayList<>(nodeColors), colors);
    }

    public void addEdge(int u, int v) {
        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    public List<Integer> getNeighbors(int vertex) {
        return adjacencyList.get(vertex);
    }

    public static ColoredGraph readFromFile(String filename) {
        ColoredGraph graph = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int vertices = Integer.parseInt(reader.readLine());
            int colors = Integer.parseInt(reader.readLine());
            graph = new ColoredGraph(vertices, colors);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] edge = line.split(" ");
                int u = Integer.parseInt(edge[0]);
                int v = Integer.parseInt(edge[1]);
                graph.addEdge(u, v);
            }

        } catch (Exception e) {
            System.err.println("Error reading the graph file: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        return graph;
    }

    public boolean isSafeToColor(int vertex, int color) {
        for (int neighbor : getNeighbors(vertex)) {
            if (nodeColors.get(neighbor).equals(color)) {
                return false;
            }
        }

        return true;
    }

    public int getSize() {
        return adjacencyList.size();
    }

    public int getColor(int vertex) {
        return nodeColors.get(vertex);
    }

    public void setColor(int vertex, int color) {
        if (color >= colors)
            throw new RuntimeException("Tried to color with " + color + " out of " + colors);

        nodeColors.set(vertex, color);
    }
    
    public boolean isFullyColored() {
        return nodeColors.stream().allMatch(v -> v != -1);
    }
    
}
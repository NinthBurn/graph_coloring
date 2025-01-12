package us.bqkitcat.ninthburn.pdp.graphcoloring.singlethread;

import us.bqkitcat.ninthburn.pdp.graphcoloring.common.ColoredGraph;

import java.util.Arrays;

public class SinglethreadApplication {
    public static boolean validateSolution(ColoredGraph graph) {
        int graphSize = graph.getSize();

        for (int vertex = 0; vertex < graphSize; ++vertex) {
            int currentColor = graph.getColor(vertex);
            if(currentColor == -1 )
                return false;

            for (int neighbor : graph.getNeighbors(vertex)) {
                int color = graph.getColor(neighbor);
                if (currentColor == color || color == -1 || color >= graph.colors) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void run(String[] args) {
        ColoredGraph graph = ColoredGraph.readFromFile("graph.txt");
        int graphSize = graph.getSize();

        for (int i = 0; i < graphSize; i++) {
            graph.setColor(i, -1);
        }

        graph.setColor(0, 0);

        for (int vertex = 1; vertex < graphSize; ++vertex) {
            boolean[] available = new boolean[graphSize];
            Arrays.fill(available, true);

            for (int neighbor : graph.getNeighbors(vertex)) {
                int color = graph.getColor(neighbor);
                if (color != -1) {
                    available[color] = false;
                }
            }

            for (int color = 0; color < graph.colors; ++color) {
                if (available[color]) {
                    graph.setColor(vertex, color);
                    break;
                }
            }
        }

        System.out.println("Vertex colors:");
        for (int i = 0; i < graphSize; ++i) {
            System.out.println("Vertex " + i + " -> Color " + graph.getColor(i));
        }

        if(validateSolution(graph))
            System.out.println("Solution is correct");
        else System.err.println("Solution is not correct");
    }
}
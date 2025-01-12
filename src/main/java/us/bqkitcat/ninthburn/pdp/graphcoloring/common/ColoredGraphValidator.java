package us.bqkitcat.ninthburn.pdp.graphcoloring.common;

public class ColoredGraphValidator {
    
    public static boolean validateSolution(ColoredGraph graph) {
        int graphSize = graph.getSize();

        for (int vertex = 0; vertex < graphSize; ++vertex) {
            int currentColor = graph.getColor(vertex);
            if (currentColor == -1)
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
    
}

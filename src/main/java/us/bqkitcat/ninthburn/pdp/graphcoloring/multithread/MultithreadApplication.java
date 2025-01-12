package us.bqkitcat.ninthburn.pdp.graphcoloring.multithread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import us.bqkitcat.ninthburn.pdp.graphcoloring.common.ColoredGraph;
import us.bqkitcat.ninthburn.pdp.graphcoloring.common.ColoredGraphValidator;

public class MultithreadApplication {

    private static ExecutorService threadPool;
    
    public static ColoredGraph safeColorTask(ColoredGraph graph, int vertex, int color)
            throws InterruptedException, ExecutionException {
        
        if (graph.isSafeToColor(vertex, color)) {
            graph.setColor(vertex, color);

            ColoredGraph newGraph = colorGraphBacktrack(graph, vertex + 1);
            if (newGraph.isFullyColored())
                return newGraph;
            
            graph.setColor(vertex, -1);
        }
        
        return graph;
    }
    
    public static ColoredGraph colorGraphBacktrack(ColoredGraph graph, int vertex) 
            throws InterruptedException, ExecutionException {
        
        if (vertex >= graph.getSize())
            return graph;
        
        @SuppressWarnings("unchecked") // fine to initialize raw array
        Future<ColoredGraph>[] lookups = new Future[graph.colors];

        for (int color = 0; color < graph.colors; color++) {
            int finalColor = color;
            lookups[color] = threadPool.submit(() -> safeColorTask(graph.safeClone(), vertex, finalColor));
        }
        
        for (var lookup : lookups) {
            ColoredGraph newGraph = lookup.get();
            if (newGraph.isFullyColored()) return newGraph;
        }

        return graph;
    }

    public static ColoredGraph colorGraph(ColoredGraph graph, int processors) {
        
        threadPool = Executors.newWorkStealingPool(processors);
        Future<ColoredGraph> coloredGraph = threadPool.submit(() -> colorGraphBacktrack(graph, 0));
        
        try {
            ColoredGraph newGraph = coloredGraph.get();
            threadPool.shutdownNow();
            return newGraph;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        
    }

    public static void run(String[] args) {
        
        int processors = Runtime.getRuntime().availableProcessors();
        String graphFile = "graph.txt";
        
        if (args.length >= 3) {            
            processors = Integer.parseInt(args[1]);
            graphFile = args[2];
        }
        
        ColoredGraph graph = ColoredGraph.readFromFile(graphFile);
        graph = colorGraph(graph, processors);
        
        if (graph.isFullyColored()) {
            
            System.out.println("Vertex colors:");
            for (int i = 0; i < graph.getSize(); ++i) {
                System.out.println("Vertex " + i + " -> Color " + graph.getColor(i));
            }

            if (ColoredGraphValidator.validateSolution(graph))
                System.out.println("Solution is correct");
            else System.err.println("Solution is not correct");
            
        } else {
            throw new RuntimeException("No solution found!");
        }
        
    }

}

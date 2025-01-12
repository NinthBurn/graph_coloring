package us.bqkitcat.ninthburn.pdp.graphcoloring.mpi;

import us.bqkitcat.ninthburn.pdp.graphcoloring.common.ColoredGraph;

import java.util.Arrays;

import mpi.MPI;

public class MPIApplication {
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
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        ColoredGraph graph = ColoredGraph.readFromFile("graph.txt");

        graph.colors = size - 1;

        if (rank == 0) {
            try {
                graphColoringMain(graph);
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        else {
            graphColoringWorker(rank, graph);
        }

        MPI.Finalize();
    }

    public static void graphColoringMain(ColoredGraph graph) {
        int[] colors = graphColoringRecursive(graph, -1, new int[graph.getSize()], 0);

        for(int i = 0; i < colors.length; ++i)
            graph.setColor(i, colors[i] - 1);

        System.out.println("Vertex colors:");
        for (int i = 0; i < graph.getSize(); ++i) {
            System.out.println("Vertex " + i + " -> Color " + graph.getColor(i));
        }

        if(validateSolution(graph))
            System.out.println("Solution is correct");
        else System.err.println("Solution is not correct");

        if (colors[0] == -1)
            throw new RuntimeException("No solution found!");
    }

    private static int[] graphColoringRecursive(ColoredGraph graph, int lastVertex, int[] solution, int rank) {
        int vertexCount = graph.getSize();

        if (!verifySolution(graph, lastVertex, solution)) {
            return getInvalidSolution(vertexCount);
        }

        if (lastVertex + 1 == graph.getSize()) {
            return solution;
        }

        int changedVertex = lastVertex + 1;
        int source, destination;

        for (int color = 1; color <= graph.colors; color++) {
            destination = color;

            if (color != rank) {
                // index of the colored vertex + partial solution
                int[] colorsBuffer = Arrays.copyOf(solution, solution.length);
                colorsBuffer[changedVertex] = color;

                int[] buffer = new int[colorsBuffer.length + 1];
                buffer[0] = changedVertex;
                System.arraycopy(colorsBuffer, 0, buffer, 1, colorsBuffer.length);

//                System.out.println(rank + " sending " + Arrays.toString(buffer) + " to " + destination);
                MPI.COMM_WORLD.Isend(buffer, 0, buffer.length, MPI.INT, destination, 0);
            }
        }

        int[] result;

        if (rank != 0) {
            int[] colors = Arrays.copyOf(solution, solution.length);
            colors[changedVertex] = rank;
            result = graphColoringRecursive(graph, changedVertex, colors, rank);

            if (result[0] != -1) {
                return result;
            }
        }

        for (int color = 1; color <= graph.colors; color++) {
            source = color;
            
            if (color != rank) {
                int[] buffer = new int[vertexCount + 1];
                MPI.COMM_WORLD.Recv(buffer, 0, vertexCount + 1, MPI.INT, source, 0);
                int previousColoredVertex = buffer[0];
                int[] colors = new int[vertexCount];
                System.arraycopy(buffer, 1, colors, 0, colors.length);

                // If child process found a solution, send it to the main process
                if (rank == 0 && colors[0] != -1) {
                    System.out.println("Process " + source + " provided solution " + Arrays.toString(colors));
                    return colors;
                }

                // Otherwise, continue checking/sending partial solutions
                if (rank != 0) {
                    result = graphColoringRecursive(graph, previousColoredVertex, colors, rank);
                    if (result[0] != -1) {
                        return result;
                    }
                }
            }
        }

        return getInvalidSolution(vertexCount);
    }

    public static void graphColoringWorker(int rank, ColoredGraph graph) {
        int vertexCount = graph.getSize();
        int[] initialSolution = new int[vertexCount + 1];

        // receive the partial solution agreed upon
        MPI.COMM_WORLD.Recv(initialSolution, 0, vertexCount + 1, MPI.INT, 0, 0);
        int previousVertex = initialSolution[0];
        int[] initialColors = new int[vertexCount];
        System.arraycopy(initialSolution, 1, initialColors, 0, initialColors.length);

        int[] resultColors = graphColoringRecursive(graph, previousVertex, initialColors, rank);

        // send complete solution back to the main process
        int[] buf = new int[vertexCount + 1];
        buf[0] = graph.getSize() - 1;
        System.arraycopy(resultColors, 0, buf, 1, resultColors.length);
        MPI.COMM_WORLD.Isend(buf, 0, vertexCount + 1, MPI.INT, 0, 0);
    }

    private static boolean verifySolution(ColoredGraph graph, int lastVertex, int[] solution) {
        for (int vertex = 0; vertex < lastVertex; vertex++) {
            if (graph.getNeighbors(lastVertex).contains(vertex) && solution[lastVertex] == solution[vertex]) {
                return false;
            }
        }

        return true;
    }

    private static int[] getInvalidSolution(int length) {
        int[] array = new int[length];
        Arrays.fill(array, -1);
        return array;
    }
}

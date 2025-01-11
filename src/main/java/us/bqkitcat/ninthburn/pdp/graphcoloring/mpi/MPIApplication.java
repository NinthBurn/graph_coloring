package us.bqkitcat.ninthburn.pdp.graphcoloring.mpi;

import mpi.*;

public class MPIApplication {

    public static void run(String[] args) {
        MPI.Init(args);

        int me = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        System.out.println("Hello world from <"+me+"> of <"+size+">");

        MPI.Finalize();
    }

}

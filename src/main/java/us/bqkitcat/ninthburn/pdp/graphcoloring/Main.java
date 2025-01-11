package us.bqkitcat.ninthburn.pdp.graphcoloring;

import us.bqkitcat.ninthburn.pdp.graphcoloring.mpi.MPIApplication;
import us.bqkitcat.ninthburn.pdp.graphcoloring.multithread.MultithreadApplication;
import us.bqkitcat.ninthburn.pdp.graphcoloring.singlethread.SinglethreadApplication;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("The first parameter must be 'false' for multithread mode " +
                    "or anything else for MPJ Express mode.");
            startSinglethread(args);
            return;
        }

        if (args[0].equals("true")) {
            System.out.println("Running in multithread mode.");
            startMultithread(args);
        }
        else {
            System.out.println("Running in MPJ Express mode.");
            startMPI(args);
        }
    }

    public static void startSinglethread(String[] args) {
        SinglethreadApplication.run(args);
    }

    public static void startMultithread(String[] args) {
        MultithreadApplication.run(args);
    }

    public static void startMPI(String[] args) {
        MPIApplication.run(args);
    }

}
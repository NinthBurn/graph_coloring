# Graph Coloring
### Table of contents
1. [Overview](#overview)
2. [Single threaded implementation](#single-threaded-implementation)
3. [Multithreaded implementation](#multithreaded-implementation)
4. [MPI implementation](#mpi-implementation)
5. [Performance measurements](#performance-measurements)

## Overview
Find an n-coloring of a graph using multithread and/or MPI 🤨

### Lab 9 - Chaos
#### Due: week 14
#### Requirement
Each student or team of 2 students will take one project. It is ok to take a theme that is not listed below (but check with the lab advisor before starting).

Each project will have 2 implementations: one with "regular" threads or tasks/futures, and one distributed (possibly, but not required, using MPI).

The documentation will describe:
- the algorithms,
- synchronization used in the parallelized variants,
- the performance measurements
**All solutions must be validated!**



## Single threaded implementation
Initially, all vertices are uncolored by setting their color to -1; the first vertex is assigned color 0.

For each vertex, a boolean array available is used to track which colors are available for it. It checks all neighbors of the vertex. If a neighbor has a color assigned, it marks that color as unavailable. This process is repeated until we find a color that is not taken.

The algorithm uses a greedy approach to color each vertex, ensuring no adjacent vertices share the same color.



## Multithreaded implementation



## MPI Implementation
- The number of colors is determined by the number of assigned processes
- Workers (child processes) are assigned specific colors based on their rank, acting like a color bucket
- They work together by sharing partial solutions with each other to collaboratively find a solution (essentially it's backtracking using multiple processes)
- All send calls are non-blocking

#### Worker Process Steps:
1. Receive a sequence of vertices that are already colored
2. For the first uncolored vertex of the sequence, color it with the process rank
3. Recursively color using backtracking; if the sequence is valid, send it to other processes
    - This prevents the other processes from generating the same sequence, and thus we avoid duplicates
4. If the process colored all the vertices, send the solution to the main process
#### The main process only assigns the colors for each process at the begininng (sends a sequence in which the first vertex is colored with the rank of the receiving process) and then waits for a complete solution.



## Performance measurements
|       | Small graph | Medium graph | Large graph |
|-------|-------------|--------------|-------------|
| Nodes | 5           | 50           | 100        |
| Edges | 6           | 141          | 103         |

#### Results
| Method/Time  | Single threaded | Multithreaded | MPI      |
|--------------|-----------------|---------------|----------|
| Small graph  | 7ms             | -             | 105ms    |
| Medium graph | 11ms            | -             | 197ms    |
| Large graph  | 32ms            | -             | overflow |

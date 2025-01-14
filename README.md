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
Initially, all vertices are uncolored by setting their color to -1; the first 
vertex is assigned color 0.

For each vertex, a boolean array available is used to track which colors are 
available for it. It checks all neighbors of the vertex. If a neighbor has a 
color assigned, it marks that color as unavailable. This process is repeated 
until we find a color that is not taken.

The algorithm uses a greedy approach to color each vertex, ensuring no adjacent 
vertices share the same color.


## Multithreaded implementation
- This is a backtracking algorithm.
- Like the single thread impleemntation, all vertices are uncolored by setting 
  their color to -1;
- The graph files store how many colors we are allowed to use. We shall use this
  number for our implementation also.
- We start at vertex 0. Our goal is to get to vertex n - 1, where n is the size
  of the graph. This represents a correct solution.
- A partial solution is when k vertices are colored, and it means we must look
  to color the (k + 1)'th vertex.
- If we cannot find a solution, we will remove k's color and go back to the
  previous vertex (backtracking)
- If we do find a solution, we march forward to (k + 2) and repeat the steps.
- This algorithm is exhaustive and uses brute force, and thus does not run as
  fast as the greedy method, but is guaranteed to find an exact solution (or,
  to let you know if no solution can be found).

### Thread considerations
- To run most optimally, we will use the Future (async/await) class. We submit
  tasks to an Executor Service, which will return a Future that will at some
  later time be complete.
- Our tasks are run on a work stealing thread pool to incentivise the processor
  to take tasks equally and not starve other threads from work.
- For each color in the step, we will submit a new task. If it completes, then
  we will continue to the next vertex on that same task, which will then spawn
  more tasks for each color of that next vertex.
- We boot up all tasks at the same time. Then, in a separate loop, we check
  the results of every call by waiting for the Future to complete.

### Command line arguments
- The first argument must be this in order to enable multithreading: `true`
- The number of threads to use is equal to the number of processors available
  to work, but you can change this by setting the 2nd argument to the number
  that you would like.
- The file that is used by default is `graph.txt`, but you can change this by
  using the 3rd parameter.
- Please note that if you do use the 2nd and 3rd parameter, both must be used
  at the same time.

> Correct example:
```sh
java -jar target/GraphColoring-1.0.0-jar-with-dependencies.jar true 8 graph.txt
```


## MPI Implementation
- The number of colors is determined by the number of assigned processes
- Workers (child processes) are assigned specific colors based on their rank, 
  acting like a color bucket
- They work together by sharing partial solutions with each other to 
  collaboratively find a solution (essentially it's backtracking using multiple 
  processes)
- All send calls are non-blocking

#### Worker Process Steps:
1. Receive a sequence of vertices that are already colored
2. For the first uncolored vertex of the sequence, color it with the process rank
3. Recursively color using backtracking; if the sequence is valid, send it to 
   other processes
    - This prevents the other processes from generating the same sequence, and  
      thus we avoid duplicates
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
| Small graph  | 7ms             | 25ms          | 105ms    |
| Medium graph | 11ms            | 571ms         | 197ms    |
| Large graph  | 32ms            | 45ms          | overflow |

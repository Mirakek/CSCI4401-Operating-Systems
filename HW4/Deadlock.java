import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

// NOTE: My code does not work due to not being able to figure out how to get a working node data structure: explanation found in Analysis.pdf
// Thanks

public class Deadlock {
    public static void main(String[] args) {
        boolean deadlockDetected = false;

        if (args.length != 1) {
            System.out.println("Usage: java Deadlock <input-file>");
            return;
        }

        String inputFilename = args[0];
        String outputFilename = inputFilename.replace("input", "output");

        try {
            File inputFile = new File(inputFilename);
            Scanner scanner = new Scanner(inputFile);
            PrintWriter writer = new PrintWriter(outputFilename);

            RAG rag = new RAG();
            
            // Parse file
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split("\\s+");
                int processId = Integer.parseInt(line[0]);
                char IO_operation = line[1].charAt(0);
                int resourceId = Integer.parseInt(line[2]);

                if (IO_operation == 'W') {
                    boolean allocated = rag.requestResource(processId, resourceId);
                    String message = allocated ?
                        "Process " + processId + " wants resource " + resourceId + " - Resource " + resourceId + " is allocated to process " + processId + "." :
                        "Process " + processId + " wants resource " + resourceId + " - Process " + processId + " must wait.";
                    writer.println(message);
                } else if (IO_operation == 'R') {
                    int nextProcess = rag.releaseResource(processId, resourceId);
                    String message = (nextProcess == -1) ?
                        "Process " + processId + " releases resource " + resourceId + " - Resource " + resourceId + " is now free." :
                        "Process " + processId + " releases resource " + resourceId + " - Resource " + resourceId + " is allocated to process " + nextProcess + ".";
                    writer.println(message);
                }

                // Check for deadlock each cycle
                deadlockDetected = rag.detectDeadlock();
                if (deadlockDetected) {
                    Set<Integer> deadlockedNodes = rag.getDeadlockedNodes();
                    Set<Integer> processesInDeadlock = new LinkedHashSet<>();
                    Set<Integer> resourcesInDeadlock = new LinkedHashSet<>();
                    for (int nodeId : deadlockedNodes) {
                        NodeType nodeType = rag.getNodeType(nodeId);
                        if (nodeType == NodeType.PROCESS) {
                            processesInDeadlock.add(nodeId);
                        } else if (nodeType == NodeType.RESOURCE) {
                            resourcesInDeadlock.add(nodeId);
                        }
                    }

                    String processes = processesInDeadlock.stream().map(String::valueOf).collect(Collectors.joining(", "));
                    String resources = resourcesInDeadlock.stream().map(String::valueOf).collect(Collectors.joining(", "));
                    writer.println("DEADLOCK DETECTED: Processes " + processes + ", and Resources " + resources + ", are found in a cycle.");
                    break;
                }
            }

            if (!deadlockDetected) {
                writer.println("EXECUTION COMPLETED: No deadlock encountered.");
            }

            scanner.close();
            writer.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + inputFilename);
        }
    }
}

class RAG {
    // Had problem with my nodes data structure not being able to map resource/process nodes with the same ID (such as process 1 and resource 1, but I can have process 1 and resource 2 for example.)
    private Map<Integer, Node> nodes;
    private Map<Integer, Set<Integer>> edges;
    private Map<Integer, Integer> allocatedResources;   // Maps resource ID to the process holding it
    private Map<Integer, Queue<Integer>> waitingLists;  // Maps resource ID to a queue of waiting processes

    public RAG() {
        nodes = new HashMap<>();
        edges = new HashMap<>();
        allocatedResources = new HashMap<>();
        waitingLists = new HashMap<>();
    }


     public boolean requestResource(int processId, int resourceId) {

        nodes.putIfAbsent(processId, new Node(processId, NodeType.PROCESS));
        nodes.putIfAbsent(resourceId, new Node(resourceId, NodeType.RESOURCE));
        
        System.out.println("nodes: " + nodes);
        System.out.println("edges: " + edges);
        System.out.println("allocated: " + allocatedResources);
        if (allocatedResources.containsKey(resourceId) == false) {
            // Allocate the resource
            allocatedResources.put(resourceId, processId);
           System.out.println("allocated: " + allocatedResources);
            edges.computeIfAbsent(processId, k -> new HashSet<>()).add(resourceId); // Edge from process to resource
            return true;
        } else {
            waitingLists.computeIfAbsent(resourceId, k -> new LinkedList<>()).add(processId);
            System.out.println("waiting: " + allocatedResources);
            edges.computeIfAbsent(resourceId, k -> new HashSet<>()).add(processId); // Request edge
            return false; // Process needs to wait
        }
    }

    public int releaseResource(int processId, int resourceId) {
        if (allocatedResources.getOrDefault(resourceId, -1) == processId) {
            allocatedResources.remove(resourceId);
            edges.get(processId).remove(resourceId); // Remove edge from process to resource
            
            System.out.println("nodes: " + nodes);
            System.out.println("edges: " + edges);
            System.out.println("allocated: " + allocatedResources);

            if (waitingLists.containsKey(resourceId) && !waitingLists.get(resourceId).isEmpty()) {
                int nextProcess = waitingLists.get(resourceId).poll();
                allocatedResources.put(resourceId, nextProcess);
                edges.get(resourceId).remove(processId); // Remove request edge
                edges.computeIfAbsent(nextProcess, k -> new HashSet<>()).add(resourceId); // Edge from next process to resource
                return nextProcess;
            }
        }
        return -1;
    }

    // Deadlock Detection Logic
    public boolean detectDeadlock() {
        Map<Integer, Integer> parentMap = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        Set<Integer> recursionStack = new HashSet<>();

        for (Integer nodeId : nodes.keySet()) {
            //System.out.println(visited);
            if (visited.contains(nodeId) == false && DFS(nodeId, visited, recursionStack, parentMap) == true) {
                return true; // Deadlock detected
            }
        }
        return false; // No deadlock detected
    }

    private boolean DFS(int nodeId, Set<Integer> visited, Set<Integer> recursionStack, Map<Integer, Integer> parentMap) {
        if (recursionStack.contains(nodeId) == true) {
            // Cycle detected only if it's a process node
            if (nodes.get(nodeId).type == NodeType.RESOURCE) {
                return false;
            } 
        }
        if (visited.contains(nodeId) == true) {
            return false; // Node already visited
        }

        visited.add(nodeId);
        recursionStack.add(nodeId);

        for (Integer neighbor : edges.getOrDefault(nodeId, Collections.emptySet())) {
            parentMap.put(neighbor, nodeId); // Track parent node
            if (DFS(neighbor, visited, recursionStack, parentMap) == true) {
                return true; // Cycle found in neighbors
            }
        }

        recursionStack.remove(nodeId);
        return false;
    }

    public Set<Integer> getDeadlockedNodes() {
        Map<Integer, Integer> parentMap = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        Set<Integer> recursionStack = new HashSet<>();

        for (Integer nodeId : nodes.keySet()) {
            if (!visited.contains(nodeId)) {
                if (DFS(nodeId, visited, recursionStack, parentMap)) {
                    Set<Integer> deadlockNodes = new HashSet<>();
                    for (Integer nodeInCycle : recursionStack) {
                        if (edges.containsKey(nodeInCycle)) {
                            deadlockNodes.addAll(edges.get(nodeInCycle)); // Include resources in deadlock
                        }
                        deadlockNodes.add(nodeInCycle); // Include processes in deadlock
                    }
                    return deadlockNodes; // Return all nodes involved in deadlock
                }
            }
        }
        return new HashSet<>(); // Return empty if no deadlock
    }


    public NodeType getNodeType(int nodeId) {
        Node node = nodes.get(nodeId);
        if (node != null) {
            return node.type;
        }
        throw new IllegalArgumentException("Node with ID " + nodeId + " does not exist.");
    }
}

class Node {
    int id;
    NodeType type;

    public Node(int id, NodeType type) {
        this.id = id;
        this.type = type;
    }
    // Debugging stuff
    @Override
    public String toString() {
        return "Node{" + "id=" + id + ", type=" + type + '}';
    }
}

enum NodeType {
    PROCESS, RESOURCE
}

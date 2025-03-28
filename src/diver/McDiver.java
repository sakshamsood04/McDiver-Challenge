package diver;
import game.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.*;
import graph.*;


/** This is the place for your implementation of the {@code SewerDiver}.
 */
public class McDiver implements SewerDiver {

    /**
     * See {@code SewerDriver} for specification.
     */
    @Override
    public void seek(SeekState state) {
        ArrayList<Long> frontier = new ArrayList<>();
        helpSeek(state, frontier);
        // TODO : Look for the ring and return.
        // DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
        // Instead, write your method (it may be recursive) elsewhere, with a
        // good specification, and call it from this one.
        //
        // Working this way provides you with flexibility. For example, write
        // one basic method, which always works. Then, make a method that is a
        // copy of the first one and try to optimize in that second one.
        // If you don't succeed, you can always use the first one.
        //
        // Use this same process on the second method, scram.
    }

    /**
     * Helper method for seek, implemented using recursion. Each call checks for distance between
     * McDiver and ring, returning if it is 0. Maintains an ArrayList of the visited nodes; McDiver
     * only moves to unvisited nodes and adds them to the frontier as it moves. The program first
     * stores all neighbours of the current node in a Collection and sorts it. If the frontier
     * does not contain the node, it adds it and performs a recursive call. The sorting process
     * optimises the code to move McDiver in the direction of the ring.
     */
    private static void helpSeek(SeekState s, ArrayList<Long> frontier) {
        if (s.distanceToRing() == 0) {
            return;
        }
        long u = s.currentLocation();
        if (!frontier.contains(u)) {
            frontier.add(u);
        }
        Collection<NodeStatus> n = s.neighbors();
        List<NodeStatus> neighbors=(List)n;
        Collections.sort(neighbors);
        for (NodeStatus w : neighbors) {
            if (!frontier.contains(w.getId())) {
                    frontier.add(w.getId());
                    s.moveTo(w.getId());
                    helpSeek(s, frontier);
                    if (s.distanceToRing() == 0) {
                        return;
                    }
                    s.moveTo(u);
                }
        }
    }


    /**
     * See {@code SewerDriver} for specification.
     */
    @Override
    public void scram(ScramState state) {
        helpScram(state);
        // TODO: Get out of the sewer system before the steps are used up.
        // DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
        // with a good specification, and call it from this one.
    }

    /**
     * Helper method for scram. Gets a list of nodes with coin value of greater than 0. Gets a list
     * of paths to each of those coins. Gets the length of each of those paths in a list.
     * Creates a HashMap to store the path and the associated length. Sorts the list of lengths
     * and reverses it to get a descending order. For each of the paths, checks if its length
     * added to the length of path from its end to exit is less than the steps to go. If true,
     * then moves McDiver along that path. Repeats this till the steps to go is no longer greater
     * than the length of path to exit. The moment this condition is matched, McDiver is moved to
     * the exit.
     */
    private void helpScram(ScramState s) {
        Maze graph = new Maze(new HashSet(s.allNodes()));
        ShortestPaths path = new ShortestPaths(graph);
        path.singleSourceDistances(s.currentNode());
        List<Edge> bestPath = path.bestPath(s.exit());

        while(s.stepsToGo()>path.getDistance(s.exit())) {
            List<Node> cNodes = cNodes(s);
            List<List<Edge>> posPaths = posPaths(path, cNodes);
            List<Double> posDistances = posDistances(path, cNodes);
            HashMap<Integer, List<Edge>> moneyMoves = new HashMap<>();
            List<Integer> pathMoney = new ArrayList<>();
            int x=0;
            for (List<Edge> p : posPaths) {
                int sum = 0;
                for (Edge e : p) {
                    sum += e.destination().getTile().coins();
                }
                sum/=posDistances.get(x);
                moneyMoves.put(sum, p);
                pathMoney.add(sum);
                x++;
            }
            Collections.sort(pathMoney);
            Collections.reverse(pathMoney);
            boolean ifMove= false;
            for (Integer i : pathMoney){
                List<Edge> currentPath = moneyMoves.get(i);
                int pathSize= currentPath.size();
                double pathLength=path.getDistance(currentPath.get(pathSize-1).destination());
                path.singleSourceDistances(currentPath.get(pathSize-1).destination());
                double pathExitLength=path.getDistance(s.exit());
                if(pathLength+pathExitLength<s.stepsToGo()){
                    ifMove=true;
                    rush(s, currentPath);
                    break;
                }
                path.singleSourceDistances(s.currentNode());
                bestPath = path.bestPath(s.exit());
            }
            path.singleSourceDistances(s.currentNode());
            bestPath = path.bestPath(s.exit());
            if(!ifMove){
                rush(s,bestPath);
                break;
            }
        }
        path.singleSourceDistances(s.currentNode());
        bestPath = path.bestPath(s.exit());
        rush(s,bestPath);
    }

    /**
     * Moves McDiver along the provided path to the destination.
     */
    private void rush(ScramState s, List<Edge> bestPath) {
        for (Edge n : bestPath) {
            s.moveTo(n.destination());
        }
    }

    /**
     * Returns list of Nodes with a coin value of greater than 0.
     */
    private List<Node> cNodes(ScramState s) {
        List<Node> cNodes = new ArrayList<>();
        for (Node n : s.allNodes()) {
            if (n.getTile().coins() > 0) {
                cNodes.add(n);
            }
        }
        return cNodes;
    }

    /**
     * Returns a List of paths to every Node in the given List cNodes.
     */
    private List<List<Edge>> posPaths(ShortestPaths path, List<Node> cNodes) {
        List<List<Edge>> posPaths = new ArrayList<>();
        for (Node n : cNodes) {
            List<Edge> bp = path.bestPath(n);
            posPaths.add(bp);
        }
        return posPaths;
    }

    /**
     * Returns a List of distances to every Node in the given List cNodes.
     */
    private List<Double> posDistances(ShortestPaths path, List<Node> cNodes) {
        List<Double> posDistances= new ArrayList<>();
        for (Node n : cNodes) {
            double d = path.getDistance(n);
            posDistances.add(d);
        }
        return posDistances;
    }
}
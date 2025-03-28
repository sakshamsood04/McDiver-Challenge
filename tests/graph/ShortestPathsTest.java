package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ShortestPathsTest {
    /** The graph example from Prof. Myers's notes. There are 7 vertices labeled a-g, as
     *  described by vertices1. 
     *  Edges are specified by edges1 as triples of the form {src, dest, weight}
     *  where src and dest are the indices of the source and destination
     *  vertices in vertices1. For example, there is an edge from a to d with
     *  weight 15.
     */
    static final String[] vertices1 = { "a", "b", "c", "d", "e", "f", "g" };
    static final int[][] edges1 = {
        {0, 1, 9}, {0, 2, 14}, {0, 3, 15},
        {1, 4, 23},
        {2, 4, 17}, {2, 3, 5}, {2, 5, 30},
        {3, 5, 20}, {3, 6, 37},
        {4, 5, 3}, {4, 6, 20},
        {5, 6, 16}
    };

    static final String[] vertices2 ={"a","b","c","d","e","f"};
    static final int[][] edges2 = {
            {0,1,3},{0,2,7},{0,3,5},
            {1,2,8},
            {2,5,13},
            {3,4,11},
            {4,0,1},
            {5,4,15}
    };

    static final String[] vertices3 = {"a","b","c","d"};
    static final int[][] edges3 ={
            {0,1,3}, {0,3,4},
            {1,2,5},
            {3,2,4}
    };

    static final String[] vertices4 = {"a","b","c"};
    static final int[][] edges4 ={
            {0,1,10},{0,2,2},
            {2,1,3}
    };

    static class TestGraph implements WeightedDigraph<String, int[]> {
        int[][] edges;
        String[] vertices;
        Map<String, Set<int[]>> outgoing;

        TestGraph(String[] vertices, int[][] edges) {
            this.vertices = vertices;
            this.edges = edges;
            this.outgoing = new HashMap<>();
            for (String v : vertices) {
                outgoing.put(v, new HashSet<>());
            }
            for (int[] edge : edges) {
                outgoing.get(vertices[edge[0]]).add(edge);
            }
        }
        public Iterable<int[]> outgoingEdges(String vertex) { return outgoing.get(vertex); }
        public String source(int[] edge) { return vertices[edge[0]]; }
        public String dest(int[] edge) { return vertices[edge[1]]; }
        public double weight(int[] edge) { return edge[2]; }
    }
    static TestGraph testGraph1() {
        return new TestGraph(vertices1, edges1);
    }

    //Test graph with 2 loops taking you back to the source
    static TestGraph testGraph2() {return new TestGraph(vertices2,edges2);}

    //Test graph with 2 equal shortest paths from source to "c"
    static TestGraph testGraph3() {return new TestGraph(vertices3,edges3);}

    //Test graph with direct path longer than indirect
    static TestGraph testGraph4() {return new TestGraph(vertices4,edges4);}

    @Test
    void lectureNotesTest() {
        TestGraph graph = testGraph1();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(50, ssp.getDistance("g"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("g")) {
            sb.append(" " + vertices1[e[0]]);
        }
        sb.append(" g");
        assertEquals("best path: a c e f g", sb.toString());
    }

    @Test
    void loopTest() {
        TestGraph graph = testGraph2();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(0, ssp.getDistance("a"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("a")) {
            sb.append(" " + vertices1[e[0]]);
        }
        sb.append(" a");
        assertEquals("best path: a", sb.toString());
    }

    @Test
    void equalPathTest() {
        TestGraph graph = testGraph3();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(8, ssp.getDistance("c"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("c")) {
            sb.append(" " + vertices1[e[0]]);
        }
        sb.append(" c");
        assertEquals("best path: a b c", sb.toString());
    }

    @Test
    void indirectPathTest() {
        TestGraph graph = testGraph4();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(5, ssp.getDistance("b"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("b")) {
            sb.append(" " + vertices1[e[0]]);
        }
        sb.append(" b");
        assertEquals("best path: a c b", sb.toString());
    }
    // TODO: Add 2 more tests
}

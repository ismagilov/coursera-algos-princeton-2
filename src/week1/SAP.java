import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.*;

public class SAP {
    private final Digraph g;
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        g = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        return length(Arrays.asList(v), Arrays.asList(w));
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        return ancestor(Arrays.asList(v), Arrays.asList(w));
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        SapData sap = findSap(v, w);

        return sap.length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        SapData sap = findSap(v, w);

        return sap.ancestor;
    }

    private SapData findSap(Iterable<Integer> vv1, Iterable<Integer> vv2) {
        if (null == vv1) throw new IllegalArgumentException("Iterable of the first vertex set us null");
        if (null == vv2) throw new IllegalArgumentException("Iterable of the second vertex set us null");

        for (Integer v : vv1)
            validateVertex(v);
        for (Integer v : vv2)
            validateVertex(v);

        SapData sap = new SapData();

        int dist1 = 0, dist2 = 0;

        HashMap<Integer, Integer> visited1 = new HashMap<>();
        Queue<Integer> q1 = new ArrayDeque<>();
        for (Integer v : vv1)
            q1.offer(v);

        HashMap<Integer, Integer> visited2 = new HashMap<>();
        Queue<Integer> q2 = new ArrayDeque<>();
        for (Integer v : vv2)
            q2.offer(v);

        while (!q1.isEmpty() || !q2.isEmpty()) {
            Queue<Integer> nq1 = findPathsIntersection(sap, q1, dist1, visited1, visited2);
            if (!nq1.isEmpty())
                dist1++;
            if (sap.isEmpty() || dist1 < sap.length)
                q1 = nq1;

            Queue<Integer> nq2 = findPathsIntersection(sap, q2, dist2, visited2, visited1);
            if (!nq2.isEmpty())
                dist2++;
            if (sap.isEmpty() || dist2 < sap.length)
                q2 = nq2;
        }

        return sap;
    }

    private Queue<Integer> findPathsIntersection(SapData sap, Queue<Integer> q, int dist, HashMap<Integer, Integer> visited, HashMap<Integer, Integer> visitedByOtherSearch) {
        Queue<Integer> next = new ArrayDeque<>();

        while (!q.isEmpty()) {
            Integer cur = q.poll();
            if (visited.containsKey(cur))
                continue;
            else
                visited.put(cur, dist);

            if (visitedByOtherSearch.containsKey(cur)) {
                int length = dist + visitedByOtherSearch.get(cur);
                if (sap.isEmpty() || length < sap.length) {
                    sap.length = length;
                    sap.ancestor = cur;
                }
            }

            for (Integer v : g.adj(cur))
                next.offer(v);
        }

        return next;
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= g.V())
            throw new IllegalArgumentException("Vertex " + v + " is not between 0 and " + (g.V() - 1));
    }


    private static final class SapData {
        private int length;
        private int ancestor;

        public SapData() {
            this(-1, -1);
        }

        public SapData(int length, int ancestor) {
            this.length = length;
            this.ancestor = ancestor;
        }

        public boolean isEmpty() {
            return length == -1 && ancestor == -1;
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph g = new Digraph(in);
        SAP sap = new SAP(g);

        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
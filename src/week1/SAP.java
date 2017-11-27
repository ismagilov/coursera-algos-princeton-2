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

        return sap != null ? sap.length : -1;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        SapData sap = findSap(v, w);

        return sap != null ? sap.ancestor : -1;
    }

    private SapData findSap(Iterable<Integer> vv1, Iterable<Integer> vv2) {
        if (null == vv1) throw new IllegalArgumentException("Iterable of the first vertex set us null");
        if (null == vv2) throw new IllegalArgumentException("Iterable of the second vertex set us null");

        for (Integer v : vv1)
            validateVertex(v);
        for (Integer v : vv2)
            validateVertex(v);

        SapData sap = null;

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
            Queue<Integer> nq1 = new ArrayDeque<>();
            Queue<Integer> nq2 = new ArrayDeque<>();

            while (!q1.isEmpty()) {
                Integer cur = q1.poll();
                if (visited1.containsKey(cur))
                    continue;
                else
                   visited1.put(cur, dist1);

                if (visited2.containsKey(cur)) {
                    int length = dist1 + visited2.get(cur);
                    if (sap == null || length < sap.length)
                        sap = new SapData(length, cur);
                }

                for (Integer v : g.adj(cur))
                    nq1.offer(v);
            }
            if (!nq1.isEmpty())
                dist1++;
            if (null == sap || dist1 < sap.length)
                q1 = nq1;

            while (!q2.isEmpty()) {
                Integer cur = q2.poll();
                if (visited2.containsKey(cur))
                    continue;
                else
                    visited2.put(cur, dist2);

                if (visited1.containsKey(cur)) {
                    int length = dist2 + visited1.get(cur);
                    if (sap == null || length < sap.length)
                        sap = new SapData(length, cur);
                }

                for (Integer v : g.adj(cur))
                    nq2.offer(v);
            }
            if (!nq2.isEmpty())
                dist2++;
            if (null == sap || dist2 < sap.length)
                q2 = nq2;
        }

        return sap;
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= g.V())
            throw new IllegalArgumentException("Vertex " + v + " is not between 0 and " + (g.V() - 1));
    }


    private static final class SapData {
        private final int length;
        private final int ancestor;

        public SapData(int length, int ancestor) {
            this.length = length;
            this.ancestor = ancestor;
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
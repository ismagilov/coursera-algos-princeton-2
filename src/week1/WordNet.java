import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordNet {
    private final Map<Integer, String> synsetsMap = new HashMap<>();
    private final Map<String, Set<Integer>> nounsMap = new HashMap<>();
    private final Digraph g;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (null == synsets) throw new IllegalArgumentException("Synsets file name is null");
        if (null == hypernyms) throw new IllegalArgumentException("Hypernyms file name is null");

        In inSyn = new In(synsets);
        String[] lines = inSyn.readAllLines();
        for (String line : lines) {
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0]);
            String synset = parts[1];
            synsetsMap.put(id, synset);

            String[] nouns = synset.split(" ");
            for (String noun : nouns) {
                Set<Integer> ids = nounsMap.getOrDefault(noun, new HashSet<>());
                ids.add(id);
                nounsMap.put(noun, ids);
            }
        }

        In inHyp = new In(hypernyms);
        lines = inHyp.readAllLines();
        g = new Digraph(synsetsMap.size());

        for (String line : lines) {
            String[] ids = line.split(",");
            for (int i = 1; i < ids.length; i++)
                g.addEdge(Integer.parseInt(ids[0]), Integer.parseInt(ids[i]));
        }

        sap = new SAP(g);

        DirectedCycle cycle = new DirectedCycle(g);
        if (cycle.hasCycle())
            throw new IllegalArgumentException("Graph has cycle");

        int rootsCounter = 0;
        for (int v = 0; v < g.V(); v++) {
            if (0 == g.outdegree(v)) {
                rootsCounter++;
                if (rootsCounter > 1)
                    throw new IllegalArgumentException("Graph has more then one root");
            }
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounsMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (null == word) throw new IllegalArgumentException("word is null");

        return nounsMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA)) throw new IllegalArgumentException("nounA is not a noun");
        if (!isNoun(nounB)) throw new IllegalArgumentException("nounB is not a noun");

        return sap.length(nounsMap.get(nounA), nounsMap.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA)) throw new IllegalArgumentException("nounA is not a noun");
        if (!isNoun(nounB)) throw new IllegalArgumentException("nounB is not a noun");

        int ancestor = sap.ancestor(nounsMap.get(nounA), nounsMap.get(nounB));

        return synsetsMap.get(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet(args[0], args[1]);
    }
}

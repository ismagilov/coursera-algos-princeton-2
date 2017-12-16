import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;

public class BoggleSolver {
    private BoogleTrie trie;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        trie = new BoogleTrie(dictionary);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        HashSet<String> res = new HashSet<>();

        int rows = board.rows();
        int cols = board.cols();

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                searchWord(board, r, c, trie.getRoot(), new boolean[rows * cols], "", res);

        return res;
    }

    private void searchWord(BoggleBoard board, int r, int c, TrieNode node, boolean[] visited, String prefix, HashSet<String> res) {
        int[][] dirs = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        char ch = board.getLetter(r, c);
        TrieNode child = node.getChild(ch);
        if (null == child)
            return;

        prefix = prefix + ch;
        if (ch == 'Q') {
            child = child.getChild('U');
            if (null == child)
                return;

            prefix = prefix + 'U';
        }

        if (prefix.length() >= 3 && child.isLeaf())
            res.add(prefix);

        visited[r * board.cols() + c] = true;
        for (int i = 0; i < dirs.length; i++) {
            int nr = r + dirs[i][0], nc = c + dirs[i][1];

            if (isInsideBoard(board, nr, nc) && !visited[nr * board.cols() + nc])
                searchWord(board, nr, nc, child, visited, prefix, res);
        }
        visited[r * board.cols() + c] = false;
    }

    private boolean isInsideBoard(BoggleBoard board, int nr, int nc) {
        if (nr >= 0 && nr < board.rows() && nc >= 0 && nc < board.cols())
            return true;
        else
            return false;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!trie.contains(word))
            return 0;

        int lng = word.length();
        if (lng <= 2)
            return  0;
        else if (3 <= lng && lng <= 4)
            return 1;
        else if (lng == 5)
            return 2;
        else if (lng == 6)
            return 3;
        else if (lng == 7)
            return 5;
        else
            return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();

        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);

        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}

class BoogleTrie {
    private TrieNode root;

    public BoogleTrie(String[] dictionary) {
        this.root = new TrieNode();

        for (String word : dictionary)
            addWord(word);
    }

    public TrieNode getRoot() {
        return root;
    }

    public boolean contains(String word) {
        TrieNode cur = root;
        for (int i = 0; i < word.length(); i++) {
            cur = cur.getChild(word.charAt(i));

            if (cur == null)
                return false;
        }

        return cur.isLeaf();
    }

    private void addWord(String word) {
        if (null == word || word.isEmpty()) return;

        TrieNode cur = root;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);

            TrieNode child = cur.getChild(ch);
            if (child == null) {
                child = new TrieNode(ch);
                cur.setChild(ch, child);
            }

            if (i == word.length() - 1)
                child.setLeaf();

            cur = child;
        }
    }
}

class TrieNode {
    private TrieNode[] children = new TrieNode[26];
    private char character;
    private boolean leaf;

    public TrieNode() {
    }

    public TrieNode(char character) {
        this();

        this.character = character;
    }

    public TrieNode getChild(char ch) {
        return children[ch - 'A'];
    }

    public void setChild(char ch, TrieNode node) {
        children[ch - 'A'] = node;
    }

    public void setLeaf() {
        leaf = true;
    }

    public boolean isLeaf() {
        return  leaf;
    }

    public char getCharacter() {
        return character;
    }
}

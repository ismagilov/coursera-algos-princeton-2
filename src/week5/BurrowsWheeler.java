import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    // apply Burrows-Wheeler transform, reading from standard input and writing to standard output

    public static void transform() {
        String str = BinaryStdIn.readString();

        CircularSuffixArray cfa = new CircularSuffixArray(str);
        for (int i = 0; i < str.length(); i++)
            if (cfa.index(i) == 0)
                BinaryStdOut.write(i);

        for (int i = 0; i < str.length(); i++) {
            int idx = cfa.index(i);
            char ch = str.charAt((idx - 1 + str.length()) % str.length());
            BinaryStdOut.write(ch);
        }

        BinaryStdOut.flush();
    }

    // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();

        String input = BinaryStdIn.readString();
        char[] t = input.toCharArray();
        char[] f = new char[t.length];

        int[] pos = sortByKeyCounting(t, f);
        pos = restoreStartPos(pos);

        int[] next = constructNext(t, pos);

        int cur = first;
        for (int i = 0; i < t.length; i++) {
            BinaryStdOut.write(f[cur]);
            cur = next[cur];
        }

        BinaryStdOut.flush();
    }

    private static int[] sortByKeyCounting(char[] input, char[] output) {
        int[] count = new int[256 + 1];
        for (int i = 0; i < input.length; i++)
            count[input[i] + 1]++;

        for (int i = 0; i < 256; i++)
            count[i + 1] += count[i];

        for (int i = 0; i < input.length; i++) {
            output[count[input[i]]] = input[i];
            count[input[i]]++;
        }
        return count;
    }

    private static int[] restoreStartPos(int[] pos) {
        for (int i = 255; i >= 0; i--) {
            if (i == 0) {
                pos[0] = 0;
                continue;
            }
            pos[i] = pos[i - 1];
        }

        return pos;
    }

    private static int[] constructNext(char[] input, int[] pos) {
        int[] next = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            int value = i;
            int idx = pos[input[i]];
            pos[input[i]]++;

            next[idx] = value;
        }

        return next;
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-"))
            transform();

        if (args[0].equals("+"))
            inverseTransform();
    }
}
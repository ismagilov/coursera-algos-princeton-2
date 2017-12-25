import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] order = new char[256]; //index - position, valye - char
        for (int i = 0; i < order.length; i++)
            order[i] = (char)i;

        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar();

            if (order[0] == ch) {
                BinaryStdOut.write((char)0);
                continue;
            }

            char prev = order[0];
            for (int i = 1; i < order.length; i++) {
                if (order[i] == ch) {
                    order[0] = ch;
                    order[i] = prev;

                    BinaryStdOut.write((char)i);
                    break;
                } else {
                    char tmp = order[i];
                    order[i] = prev;
                    prev = tmp;
                }
            }
        }

        BinaryStdOut.flush();
    }


    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] order = new char[256];
        for (int i = 0; i < order.length; i++)
            order[i] = (char)i;

        while (!BinaryStdIn.isEmpty()) {
            int pos = BinaryStdIn.readChar();

            BinaryStdOut.write(order[pos]);

            if (pos == 0)
                continue;

            char prev = order[0];
            order[0] = order[pos];
            for (int i = 1; i <= pos; i++) {
                char tmp = order[i];
                order[i] = prev;
                prev = tmp;
            }
        }

        BinaryStdOut.flush();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-"))
            encode();

        if (args[0].equals("+"))
            decode();
    }
}
import edu.princeton.cs.algs4.StdRandom;

public class CircularSuffixArray {
    private final int length;
    private final int[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (null == s) throw new IllegalArgumentException("Input string is null");

        length = s.length();
        index = new int[length];
        for (int i = 0; i < length; i++)
            index[i] = i;

        Quick3string.sort(s, index);
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i > length - 1) throw new IllegalArgumentException("Index i is outside [0, " + (length - 1) + "] range");

        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray cfa = new CircularSuffixArray("ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        for (int i = 0; i < cfa.length(); i++)
            System.out.println(cfa.index(i));
    }
}


class Quick3string {
    private static final int CUTOFF =  15;   // cutoff to insertion sort

    // do not instantiate
    private Quick3string() { }

    public static void sort(String s, int[] startPos) {
        StdRandom.shuffle(startPos);
        sort(s, startPos, 0, startPos.length - 1, 0);
    }

    // return the dth character of s, -1 if d = length of s
    private static int charAt(String s, int startPos, int d) {
        assert d >= 0 && d <= s.length();
        if (d == s.length()) return -1;

        return s.charAt((startPos + d) % s.length());
    }


    // 3-way string quicksort a[lo..hi] starting at dth character
    private static void sort(String s, int[] startPos, int lo, int hi, int d) {
        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(s, startPos, lo, hi, d);
            return;
        }

        int lt = lo, gt = hi;
        int v = charAt(s, startPos[lo], d);
        int i = lo + 1;
        while (i <= gt) {
            int t = charAt(s, startPos[i], d);
            if      (t < v) exch(startPos, lt++, i++);
            else if (t > v) exch(startPos, i, gt--);
            else              i++;
        }

        // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
        sort(s, startPos, lo, lt-1, d);
        if (v >= 0) sort(s, startPos, lt, gt, d+1);
        sort(s, startPos, gt+1, hi, d);
    }

    // sort from a[lo] to a[hi], starting at the dth character
    private static void insertion(String s, int[] startPos, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(s, startPos[j], startPos[j-1], d); j--)
                exch(startPos, j, j-1);
    }

    // exchange a[i] and a[j]
    private static void exch(int[] startPos, int i, int j) {
        int temp = startPos[i];
        startPos[i] = startPos[j];
        startPos[j] = temp;
    }

    // is v less than w, starting at character d
    // DEPRECATED BECAUSE OF SLOW SUBSTRING EXTRACTION IN JAVA 7
    // private static boolean less(String v, String w, int d) {
    //    assert v.substring(0, d).equals(w.substring(0, d));
    //    return v.substring(d).compareTo(w.substring(d)) < 0;
    // }

    // is v less than w, starting at character d
    private static boolean less(String s, int v, int w, int d) {
        for (int i = d; i < s.length(); i++) {
            if (charAt(s, v, i) < charAt(s, w, i)) return true;
            if (charAt(s, v, i) > charAt(s, w, i)) return false;
        }

        return false;
    }
}

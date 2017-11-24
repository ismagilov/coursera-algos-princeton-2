import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        ArrayList<Integer> al = new ArrayList<>();

        al.add(1);
        al.add(2);

        Integer[] arr = al.toArray(new Integer[0]);
        System.out.println(Arrays.toString(arr));
    }
}

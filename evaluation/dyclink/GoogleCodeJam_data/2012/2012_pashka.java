
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class A {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File(A.class.getSimpleName() + ".in"));
        PrintWriter out = new PrintWriter(new File(A.class.getSimpleName() + ".out"));
        int T = in.nextInt();
        for (int i = 0; i < T; i++) {
            String s = "Case #" + (i + 1) + ": " + new A().solve(in);
            out.println(s);
            System.out.println(s);
        }
        out.close();
    }

    private String solve(Scanner in) {
        int n = in.nextInt();
        final double [] l = new double[n];
        final double [] r = new double[n];
        for (int i = 0; i < n; i++) {
            l[i] = in.nextInt();
        }
        for (int i = 0; i < n; i++) {
            r[i] = 1 - in.nextInt() * 0.01;
        }
        List<Integer> p = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            p.add(i);
        }
        Collections.sort(p, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                double a = (l[o1] / r[o1] + l[o2]) / r[o2];
                double b = (l[o2] / r[o2] + l[o1]) / r[o1];
                return Double.compare(a, b);
            }
        });
        String res = "";
        for (int i = 0; i < n; i++) res += p.get(i) + " ";
        return res;
    }
}
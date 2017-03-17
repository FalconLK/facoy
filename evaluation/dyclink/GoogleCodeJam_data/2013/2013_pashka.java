
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

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
        long b = in.nextLong();
        int n = 37;
        long[] a = new long[n];
        int nn = in.nextInt();
        for (int i = 0; i < nn; i++) {
            a[i] = in.nextLong();
        }
        Arrays.sort(a);
        double max = 0;
        for (int i = 0; i < n - 1; i++) {
            long l = a[i] - 1;
            long r = a[n - 1] - 1;
            while (r > l + 1) {
                long m = (l + r) / 2;
                long s = 0;
                for (int j = 0; j < n; j++) {
                    if (j <= i)
                        s += (m - a[j]);
                    else if (a[j] < m + 1) {
                        s += (m + 1 - a[j]);
                    }
                }
                if (s > b) {
                    r = m;
                } else {
                    l = m;
                }
            }
            if (l >= a[i]) {
                long s = 0;
                long s2 = 0;
                for (int j = 0; j < n; j++) {
                    if (j <= i) {
                        s += (l - a[j]);
                        s2 += (l - a[j]);
                    } else if (a[j] < l + 1) {
                        s += (l + 1 - a[j]);
                    }
                }
                double w = s2 * (36.0 / (i + 1)) - s;
//                System.out.println(i + " " + l + " " + w);
                max = Math.max(max, w);
            }
        }
        return "" + max;
    }
}
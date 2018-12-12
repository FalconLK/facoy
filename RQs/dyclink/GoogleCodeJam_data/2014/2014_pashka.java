
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
        int n = in.nextInt();
        long P = in.nextInt();
        long Q = in.nextInt();
        long R = in.nextInt();
        long S = in.nextInt();
        long[] a = new long[n];
        long ss = 0;
        for (int i = 0; i < n; i++) {
            a[i] = ((i * P + Q) % R + S);
            ss += a[i];
        }
        long l = 0;
        long r = ss;
        while (r > l + 1) {
            long m = (l + r) / 2;
            boolean ok = true;
            for (int i = 0; i < n; i++) {
                if (a[i] > m) ok = false;
            }
            if (!ok) {
                l = m;
            } else {
                int c = 0;
                long q = 0;
                while (c < n && q + a[c] <= m) {
                    q += a[c];
                    c++;
                }
                q = 0;
                while (c < n && q + a[c] <= m) {
                    q += a[c];
                    c++;
                }
                q = 0;
                while (c < n && q + a[c] <= m) {
                    q += a[c];
                    c++;
                }
                if (c == n) {
                    r = m;
                } else {
                    l = m;
                }
            }
        }
        return "" + 1.0 * (ss - r) / ss;
    }
}
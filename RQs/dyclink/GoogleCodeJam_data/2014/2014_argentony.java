import java.io.*;
import java.util.*;

public class A {
    private final Scanner in;
    private final PrintWriter out;

    public A(Scanner in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    public static void main(String[] args) {
        final String filename = "A-large";
        try (Scanner in = new Scanner(new FileReader(new File(filename + ".in")));
             PrintWriter out = new PrintWriter(filename + ".out")) {
            new A(in, out).solve();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }
    }

    private void solve() {
        int tests = in.nextInt();
        for (int test = 1; test <= tests; ++test) {
            System.out.println(test);

            int N = in.nextInt();
            long[] a = new long[N];
            {
                int p = in.nextInt();
                int q = in.nextInt();
                int r = in.nextInt();
                int s = in.nextInt();

                for (int i = 0; i < a.length; ++i) {
                    a[i] = ((i * (long) p + q) % r + s);
                }
            }

            long sl = 0;
            long sm = a[0];
            long sr = 0;
            for (int i = 1; i < a.length; ++i) {
                sr += a[i];
            }
            long sum = sm + sr;

            long ans = Math.max(sm, sr);
            int l = 0;
            for (int r = 1; r < a.length; ++r) {
                sm += a[r];
                sr -= a[r];

                while (l < r && Math.max(sl + a[l], sm - a[l]) < Math.max(sl, sm)) {
                    sl += a[l];
                    sm -= a[l];
                    ++l;
                }

                ans = Math.min(ans, Math.max(sr, Math.max(sm, sl)));
//                System.out.println(" " + l + " " + r + " " + sl + " " + sm + " " + sr + " " + ans);
            }

            out.print("Case #" + test + ": ");
            out.println((sum - ans) / 1.0 / sum);
        }
    }
}

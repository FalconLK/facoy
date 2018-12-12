import java.math.BigInteger;
import java.util.*;
import java.io.*;

public class A {
    FastScanner in;
    PrintWriter out;

    long[] a;
    long b;

    long eval(int cnt, long val) {
        long needMin = 0;
        for (int k = 0; k < cnt; k++) {
            needMin += val - a[k];
        }
        for (int k = cnt; k < 37; k++) {
            if (a[k] <= val) {
                needMin += val + 1 - a[k];
            }
        }
        return needMin;
    }

    public void solve() throws IOException {
        int testNo = in.nextInt();
        for (int test = 1; test <= testNo; test++) {
            System.out.println("Test " + test);

            b = in.nextLong();
            int n = in.nextInt();
            a = new long[37];
            long sumb = 0;
            for (int i = 0; i < n; i++) {
                a[i] = in.nextLong();
                sumb += a[i];
            }
            Arrays.sort(a);

            double ans = 0;
            for (int i = 0; i <= 37; i++) {
                long L = i == 0 ? 0 : a[i - 1];
                long R = b + sumb + 1;
                if (L >= R || eval(i, L) > b) {
                    continue;
                }

                while (L < R - 1) {
                    long m = (L + R) / 2;
                    long need = eval(i, m);
                    if (need > b) {
                        R = m;
                    } else {
                        L = m;
                    }
                }

                long[] put = new long[37];
                long total = 0;
                for (int j = 0; j < 37; j++) {
                    if (j < i) {
                        if (a[j] <= L) {
                            put[j] = L - a[j];
                        } else {
                            throw new AssertionError();
                        }
                    } else {
                        if (a[j] < L + 1) {
                            put[j] = L + 1 - a[j];
                        }
                    }
                    total += put[j];
                }

                double win = 0;
                for (int j = 0; j < i; j++) {
                    win += 1.0 / i * put[j] * 36.0;
                }
                win -= total;
                if (win > ans) {
                    ans = win;
                }
            }

            out("Case #%d: ", test);
            out("%.15f", ans);
            out("\n");
        }
    }
    
    void out(String format, Object... s) {
        System.out.printf(format, s);
        out.printf(format, s);
        out.flush();
    }

    public void run() {
        try {
            in = new FastScanner(new File("A-large.in"));
            out = new PrintWriter(new File("A-large.out"));

            solve();

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] arg) {
        new A().run();
    }

    static class FastScanner {
        BufferedReader br;
        StringTokenizer st;

        FastScanner(File file) throws IOException {
            br = new BufferedReader(new FileReader(file));
        }

        String next() {
            while (st == null || !st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                    return "";
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
        long nextLong() {
             return Long.parseLong(next());
         }
    }
}
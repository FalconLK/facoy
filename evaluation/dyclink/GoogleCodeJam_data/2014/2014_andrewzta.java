import java.math.BigInteger;
import java.util.*;
import java.io.*;

public class A {
    FastScanner in;
    PrintWriter out;

    int n;
    long ans;
    long[] sum;

    void relax(int a, int b) {
        long s1 = sum[a];
        long s2 = sum[b + 1] - sum[a];
        long s3 = sum[n] - sum[b + 1];
        long v = sum[n] - Math.max(s1, Math.max(s2, s3));
        if (v > ans) {
            ans = v;
        }
    }

    public void solve() throws IOException {
        int testNo = in.nextInt();
        for (int test = 1; test <= testNo; test++) {
            n = in.nextInt();
            long p = in.nextLong();
            long q = in.nextLong();
            long r = in.nextLong();
            long s = in.nextLong();
            long[] v = new long[n];
            for (int i = 0; i < n; i++) {
                v[i] = (i * p  + q) % r + s;
            }
            sum = new long[n + 1];
            for (int i = 0; i < n; i++) {
                sum[i + 1] = sum[i] + v[i];
            }
            int b = 0;
            ans = 0;
            for (int a = 0; a < n; a++) {
                relax(a, b);
                while (b < n && sum[b + 1] - sum[a] < sum[n] - sum[b + 1]) {
                    b++;
                    relax(a, b);
                }
            }

            out("Case #%d: %.20f\n", test, 1.0 * ans / sum[n]);
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
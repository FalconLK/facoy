import java.io.*;
import java.util.*;

public class A {
    FastScanner in;
    PrintWriter out;

    Random rnd = new Random(123);

    long fit(long[] a, int to, long value) {
        long res = 0;
        for (int i = 0; i < to; i++) {
            res += value - a[i];
        }
        for (int i = to; i < 37; i++)
            if (a[i] <= value) {
                res += value + 1 - a[i];
            }
        return res;
    }

    double getProfit(long[] a, int to, long value) {
        long res = 0;
        for (int i = 0; i < to; i++) {
            res += value - a[i];
        }
        double ss = res * 1.0 / to * 36;
        for (int i = to; i < 37; i++)
            if (a[i] <= value) {
                res += value + 1 - a[i];
            }
        ss -= res;
        return ss;
    }

    void solve() throws IOException {
        long b = in.nextLong();
        int n = in.nextInt();
        long[] a = new long[37];
        for (int i = 0; i < n; i++)
            a[i] = in.nextLong();
        Arrays.sort(a);
        double best = 0;
        for (int i = 1; i < 37; i++) {
            long l = 0;
            for (int j = 0; j < i; j++)
                l = Math.max(l, a[j]);
            long r = (long) 1e14;
            while (r - l > 1) {
                long m = (l + r) / 2;
                long need = fit(a, i, m);
                if (need <= b) {
                    l = m;
                } else {
                    r = m;
                }
            }
            if (fit(a, i, l) <= b) {
                best = Math.max(best, getProfit(a, i, l));
            }
        }
        out.println(best);
    }

    void run() throws IOException {
        try {
            in = new FastScanner(new File("A.in"));
            out = new PrintWriter(new File("A.out"));

            int testNumber = in.nextInt();
            for (int test = 1; test <= testNumber; test++) {
                out.print("Case #" + (test) + ": ");
                solve();
                System.out.println(test);
            }

            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    void runIO() {

        in = new FastScanner(System.in);
        out = new PrintWriter(System.out);

        try {
            solve();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        out.close();
    }

    class FastScanner {
        BufferedReader br;
        StringTokenizer st;

        public FastScanner(File f) {
            try {
                br = new BufferedReader(new FileReader(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public FastScanner(InputStream f) {
            br = new BufferedReader(new InputStreamReader(f));
        }

        String next() {
            while (st == null || !st.hasMoreTokens()) {
                String s = null;
                try {
                    s = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (s == null)
                    return null;
                st = new StringTokenizer(s);
            }
            return st.nextToken();
        }

        boolean hasMoreTokens() {
            while (st == null || !st.hasMoreTokens()) {
                String s = null;
                try {
                    s = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (s == null)
                    return false;
                st = new StringTokenizer(s);
            }
            return true;
        }

        int nextInt() {
            return Integer.parseInt(next());
        }

        long nextLong() {
            return Long.parseLong(next());
        }
    }

    public static void main(String[] args) throws IOException {
        new A().run();
    }
}
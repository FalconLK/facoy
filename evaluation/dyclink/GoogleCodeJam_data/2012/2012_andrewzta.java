import java.util.*;
import java.io.*;

public class A {
    FastScanner in;
    PrintWriter out;
    
    class Level implements Comparable<Level> {
        int i;
        int p;
        int l;
        double q;

        Level(int i) {
            this.i = i;
        }

        public int compareTo(Level o) {
            if (l * o.p  < o.l * p) {
                return -1;
            }
            if (l * o.p > o.l * p) {
                return 1;
            }
            return i - o.i;
        }
    }
    
    int n;
    Level[] x;
    int[] a;
    int[] ans;
    boolean[] u;
    double best;
    
    double eval(int[] a) {
        double[] pq = new double[n];
        pq[n - 1] = x[a[n - 1]].q;
        for (int i = n - 2; i >= 0; i--) {
            pq[i] = pq[i + 1] * x[a[i]].q;
        }
        double val = 0;
        for (int i = 0; i < n; i++) {
            val += x[a[i]].l / pq[i];
        }
        return val; 
    }
    
    void bt(int p) {
        if (p == n) {
            double val = eval(a);
            if (val < best) {
                best = val;
                ans = a.clone();
            }
        } else {
            for (int i = 0; i < n; i++) {
                if (!u[i]) {
                    u[i] = true;
                    a[p] = i;
                    bt(p + 1);
                    u[i] = false;
                }
            }
        }
    }

    public void solve() throws IOException {
        int testNo = in.nextInt();
        for (int test = 1; test <= testNo; test++) {
            System.out.println("Test " + test);

            n = in.nextInt();
            x = new Level[n];
            for (int i = 0; i < n; i++) {
                x[i] = new Level(i);
            }
            for (int i = 0; i < n; i++) {
                x[i].l = in.nextInt();
            }
            for (int i = 0; i < n; i++) {
                x[i].p = in.nextInt();
                x[i].q = 1.0 - x[i].p / 100.0;
            }
            
            best = Double.POSITIVE_INFINITY;
            a = new int[n];
            u = new boolean[n];

            Arrays.sort(x);

            out("Case #%d:", test);
            for (int i = 0; i < n; i++) {
                out(" %d", x[i].i);
            }
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
    }
}
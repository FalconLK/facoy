import java.io.*;
import java.util.*;

public class A {
    FastScanner in;
    PrintWriter out;

    Random rnd = new Random(123);

    void solve() throws IOException {
        int n = in.nextInt();
        int p = in.nextInt();
        int q = in.nextInt();
        int r = in.nextInt();
        int s = in.nextInt();
        long[] a = new long[n];
        for (int i = 0; i < n; i++) {
            a[i] = ((i * 1L * p + q) % r + s);
        }
        long[] sum = new long[n];
        for (int i = 0; i < n; i++)
            sum[i] = a[i] + (i == 0 ? 0 : sum[i - 1]);
        long left = 0, right = (long) 1e15;
        while (right - left > 1) {
            long mid = (left + right) / 2;
            int last = 0;
            boolean ok = false;
            long sum1 = 0;
            int first = 0;
            while (first != n && sum1 < mid) {
                sum1 += a[first];
                first++;
            }
            int second = n - 1;
            long sum2 = 0;
            while (second != -1 && sum2 < mid) {
                sum2 += a[second];
                second--;
            }
            if (sum1 >= mid && sum2 >= mid) {
                if (second >= first) {
                    ok = true;
                } else {
                    long total = 0;
                    if (second != -1)
                        total += sum[second];
                    if (first != n)
                        total += sum[n - 1] - sum[first - 1];
                    if (total >= mid)
                        ok = true;
                }
            }
            if (ok) {
                left = mid;
            } else {
                right = mid;
            }
        }
        Locale.setDefault(Locale.US);
        out.println(left / (sum[n - 1] + 0.));
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
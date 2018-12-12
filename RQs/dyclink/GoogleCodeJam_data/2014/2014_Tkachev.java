import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.StringTokenizer;

public class A {

    int n;
    int[] a;
    long total;
    long[] sum;
    long best;

    private void solve() throws Exception {
        n = nextInt();
        a = getDev(n);
        int l = 0, r = 0;
        long curSum = 0;
        best = 0;
        for (l = 0; l < n; ++l) {
            long leftSum = getSum(l - 1);
            int lo = l, hi = n;
            while (hi - lo > 1) {
                int mid = (lo + hi) / 2;
                long rightSum = total - getSum(mid);
                long middleSum = total - leftSum - rightSum;
                for (int delta = -10; delta <= 10; ++delta)
                    best = Math.max(best, getCur(l, mid + delta));
                if (middleSum < leftSum && middleSum < rightSum) {
                    lo = mid;
                } else {
                    hi = mid;
                }
            }
            best = Math.max(best, getCur(l, lo));
            //if (n < 100)
            //out.print(lo + " ");
            curSum -= a[l];
        }
        out.printf("%.10f\n", (double) best / total);
    }

    private long getCur(int l, int r) {
        if (r < l || r < 0 || r >= n) {
            return 0;
        }
        long ma = Math.max(getSum(r) - getSum(l - 1), Math.max(getSum(l - 1), total - getSum(r)));
        return total - ma;
    }

    private long getSum(int x) {
        if (x < 0) {
            return 0;
        }
        return sum[x];
    }

    private int[] getDev(int n) {
        int p = nextInt(), q = nextInt(), r = nextInt(), s = nextInt();
        int[] res = new int[n];
        total = 0;
        sum = new long[n];
        for (int i = 0; i < n; ++i) {
            res[i] = ((int) (((long) i * p + q) % r)) + s;
            total += res[i];
            sum[i] = res[i];
            if (i > 0) {
                sum[i] += sum[i - 1];
            }
        }
        return res;
    }

    public void run() {
        try {
            int tc = nextInt();
            for (int it = 1; it <= tc; ++it) {
                System.err.println(it);
                out.print("Case #" + it + ": ");
                solve();
            }
        } catch (Exception e) {
            NOO(e);
        } finally {
            out.close();
        }
    }

    PrintWriter out;
    BufferedReader in;
    StringTokenizer St;

    void NOO(Exception e) {
        e.printStackTrace();
        System.exit(42);
    }

    int nextInt() {
        return Integer.parseInt(nextToken());
    }

    long nextLong() {
        return Long.parseLong(nextToken());
    }

    double nextDouble() {
        return Double.parseDouble(nextToken());
    }

    String nextToken() {
        while (!St.hasMoreTokens()) {
            try {
                String line = in.readLine();
                if (line == null)
                    return null;
                St = new StringTokenizer(line);
            } catch (Exception e) {
                NOO(e);
            }
        }
        return St.nextToken();
    }

    private A(String name) {
        try {
            in = new BufferedReader(new FileReader("input.txt"));
            St = new StringTokenizer("");
            out = new PrintWriter(new FileWriter("output.txt"));
        } catch (Exception e) {
            NOO(e);
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        new A("a").run();
    }
}

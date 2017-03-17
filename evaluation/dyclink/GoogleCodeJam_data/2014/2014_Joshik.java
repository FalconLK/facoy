import java.util.*;
import java.io.*;
import java.math.BigInteger;

public class SolA {

	public static void main(String[] args) {
		try {
			new SolA().run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() throws IOException {
		br = new BufferedReader(new FileReader(FNAME + ".in"));
		out = new PrintWriter(FNAME + ".out");
		solve();
		out.close();
	}

	BufferedReader br;
	PrintWriter out;
	StringTokenizer st;
	boolean eof;

	String nextToken() {
		while (st == null || !st.hasMoreTokens()) {
			try {
				st = new StringTokenizer(br.readLine(), " \n\t/");
			} catch (IOException e) {
				eof = true;
				return "0";
			}
		}
		return st.nextToken();
	}

	int nextInt() {
		return Integer.parseInt(nextToken());
	}
	
	long nextLong() {
		return Long.parseLong(nextToken());
	}
	
	String FNAME = "A-large";
	
	void solve() {
		int tests = nextInt();
		for (int test = 1; test <= tests; test++) {
			out.print("Case #" + test + ": ");
            int n = nextInt();
            long p = nextInt();
            int q = nextInt();
            int r = nextInt();
            int s = nextInt();
            long[] a = new long[n];
            long sum = 0;
            for (int i = 0; i < n; i++) {
                a[i] = ((i * p + q) % r) + s;
                sum += a[i];
            }
            long t1 = 0;
            long t2 = 0;
            long rest = sum;
            int j = 0;
            double ans = 0;
            for (int i = 0; i < n; i++) {
                while (j < n && t2 + a[j] <= rest - a[j]) {
                    t2 += a[j];
                    rest -= a[j];
                    j++;
                }
                double pp = 1.0 * (sum - Math.max(t1, Math.max(t2, rest))) / sum;
                ans = Math.max(ans, pp);
                if (j < n) {
                    pp = 1.0 * (sum - Math.max(t1, Math.max(t2 + a[j], rest - a[j]))) / sum;
                    ans = Math.max(ans, pp);
                }
                t1 += a[i];
                t2 -= a[i];
                if (j == i) {
                    rest -= a[j];
                    j++;
                }
            }
            out.println(ans);
        }
	}
}

import java.util.*;
import java.io.*;
import java.math.*;
import java.awt.geom.*;

import static java.lang.Math.*;

public class Solution implements Runnable {
	
	long getSum(long[] n, int L, int R) {
		long sum = 0;
		if (R >= 0) {
			sum = n[R];
		}
		if (L > 0) {
			sum -= n[L - 1];
		}
		return sum;
	}
	
	long getMax(long[] n, int L, int R) {
		return max(getSum(n, L, R), getSum(n, 0, L - 1));
	}

	public void solve() throws Exception {
		int T = sc.nextInt();
		for (int Case = 1; Case <= T; Case++) {
			int N = sc.nextInt(), p = sc.nextInt(), q = sc.nextInt(), r = sc.nextInt(), s = sc.nextInt();
			long[] n = new long[N];
			for (int i = 0; i < N; i++) {
				n[i] = ((long)i * p + q) % r + s;
				if (i > 0) {
					n[i] += n[i - 1];
				}
			}
			long ans = Long.MAX_VALUE / 4;
			for (int R = 0, L = 0; R < N; R++) {
				while (L + 1 <= R && getMax(n, L + 1, R) <= getMax(n, L, R)) {
					L++;
				}
				ans = min(ans, max(getMax(n, L, R), getSum(n, R + 1, N - 1)));
			}
			out.printf(Locale.US, "Case #%d: %.12f\n", Case, 1.0 - (double)ans / getSum(n, 0, N - 1));
		}
	}

	static Throwable uncaught;

	BufferedReader in;
	FastScanner sc;
	PrintWriter out;

	@Override
	public void run() {
		try {
			//in = new BufferedReader(new InputStreamReader(System.in));
			//out = new PrintWriter(System.out);
			in = new BufferedReader(new FileReader("A-small-attempt0.in"));
			out = new PrintWriter("A-small-attempt0.out");
			sc = new FastScanner(in);
			solve();
		} catch (Throwable uncaught) {
			Solution.uncaught = uncaught;
		} finally {
			out.close();
		}
	}

	public static void main(String[] args) throws Throwable {
		Thread thread = new Thread(null, new Solution(), "", (1 << 26));
		thread.start();
		thread.join();
		if (Solution.uncaught != null) {
			throw Solution.uncaught;
		}
	}

}

class FastScanner {

	BufferedReader in;
	StringTokenizer st;

	public FastScanner(BufferedReader in) {
		this.in = in;
	}

	public String nextToken() throws Exception {
		while (st == null || !st.hasMoreTokens()) {
			st = new StringTokenizer(in.readLine());
		}
		return st.nextToken();
	}

	public int nextInt() throws Exception {
		return Integer.parseInt(nextToken());
	}

	public long nextLong() throws Exception {
		return Long.parseLong(nextToken());
	}

	public double nextDouble() throws Exception {
		return Double.parseDouble(nextToken());
	}

}
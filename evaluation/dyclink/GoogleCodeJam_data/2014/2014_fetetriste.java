import java.io.*;
import java.util.*;

public class Main implements Runnable {

	public void _main() throws IOException {
		int numTests = nextInt();
		for (int test = 1; test <= numTests; test++) {
			int n = nextInt();
			long p = nextInt();
			long q = nextInt();
			long r = nextInt();
			long s = nextInt();
			int[] a = new int[n];
			for (int i = 0; i < n; i++) {
				a[i] = (int)((((i * p + q) % r) + s)); 
			}
			out.printf("Case #%d: %.15f\n", test, solve(n, a));
		}
	}

	private double solve(int n, int[] a) {
		long[] s = new long[n + 1];
		for (int i = 0; i < n; i++) {
			s[i + 1] = s[i] + a[i];
		}
		int l = 1;
		long best = s[n];
		for (int r = 1; r <= n; r++) {
			while (l < r && s[l - 1] < s[r] - s[l - 1]) {
				++l;
			}
			for (int delta = -2; delta <= 2; delta++) {
				l += delta;
				if (l >= 1 && l <= r) {
					long cur = 0;
					cur = Math.max(cur, s[l - 1]);
					cur = Math.max(cur, s[r] - s[l - 1]);
					cur = Math.max(cur, s[n] - s[r]);
					best = Math.min(best, cur);
				}
				l -= delta;
			}
		}
		return (double)(s[n] - best) / s[n];
	}


	private BufferedReader in;
	private PrintWriter out;
	private StringTokenizer st;

	private String next() throws IOException {
		while (st == null || !st.hasMoreTokens()) {
			String rl = in.readLine();
			if (rl == null)
				return null;
			st = new StringTokenizer(rl);
		}
		return st.nextToken();
	}

	private int nextInt() throws IOException {
		return Integer.parseInt(next());
	}

	private long nextLong() throws IOException {
		return Long.parseLong(next());
	}

	private double nextDouble() throws IOException {
		return Double.parseDouble(next());
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.UK);
		new Thread(new Main()).start();
	}

	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(System.in));
			out = new PrintWriter(System.out);
			in = new BufferedReader(new FileReader("A-large.in"));
			out = new PrintWriter(new FileWriter("A-large.out"));

			_main();

			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(202);
		}
	}

}

import java.io.*;
import java.util.*;

public class A {

	BufferedReader br;
	PrintWriter out;
	StringTokenizer st;
	boolean eof;

	enum InputType {
		EXAMPLE, SMALL, LARGE
	}

	static final InputType INPUT_TYPE = InputType.LARGE;
	static final int ATTEMPT_NUMBER = 0;

	void solve() throws IOException {
		int n = nextInt();
		int p = nextInt();
		int q = nextInt();
		int r = nextInt();
		int s = nextInt();

		int[] a = new int[n];
		for (int i = 0; i < n; i++) {
			a[i] = (int) (((long) i * p + q) % r + s);
		}

		long[] pref = new long[n + 1];
		for (int i = 0; i < n; i++) {
			pref[i + 1] = pref[i] + a[i];
		}
		
//		System.err.println(Arrays.toString(pref));
//		System.err.println(Arrays.toString(a));

		long low = 0; // can't
		long high = pref[n]; // can
		
//		low = 99;
//		high = 101;
		
		
		outer: while (low + 1 < high) {
			long mid = (low + high) / 2;
			int cur = 0;
			for (int i = 0; i < 3; i++) {
				long need = pref[cur] + mid;
				int x = Arrays.binarySearch(pref, need);
				if (x < 0) {
					x = -x - 2;
				}
//				System.err.println(x);
				if (x == n) {
					high = mid;
					continue outer;
				}
				cur = x;
			}
			low = mid;
		}
		
//		System.err.println(high);
		double ans = 1.0 * (pref[n] - high) / pref[n];
		out.printf(Locale.US, "%.12f\n", ans);
	}

	A() throws IOException {
		switch (INPUT_TYPE) {
		case EXAMPLE: {
			br = new BufferedReader(new InputStreamReader(System.in));
			out = new PrintWriter(System.out);
			break;
		}
		case SMALL: {
			br = new BufferedReader(new FileReader("A-small-attempt"
					+ ATTEMPT_NUMBER + ".in"));
			out = new PrintWriter("A-small-attempt" + ATTEMPT_NUMBER + ".out");
			break;
		}
		case LARGE: {
			br = new BufferedReader(new FileReader("A-large.in"));
			out = new PrintWriter("A-large.out");
			break;
		}
		}
		int t = nextInt();
		for (int i = 1; i <= t; i++) {
			System.err.println("Test " + i);
			out.print("Case #" + i + ": ");
			solve();
		}
		out.close();
	}

	public static void main(String[] args) throws IOException {
		new A();
	}

	String nextToken() {
		while (st == null || !st.hasMoreTokens()) {
			try {
				st = new StringTokenizer(br.readLine());
			} catch (Exception e) {
				eof = true;
				return null;
			}
		}
		return st.nextToken();
	}

	String nextString() {
		try {
			return br.readLine();
		} catch (IOException e) {
			eof = true;
			return null;
		}
	}

	int nextInt() throws IOException {
		return Integer.parseInt(nextToken());
	}

	long nextLong() throws IOException {
		return Long.parseLong(nextToken());
	}

	double nextDouble() throws IOException {
		return Double.parseDouble(nextToken());
	}
}
import java.io.*;
import java.util.*;

public class Casino {
	public static void main(String[] args) {
		new Casino().run();
	}

	BufferedReader br;
	StringTokenizer st;
	PrintWriter out;
	boolean eof = false;

	private void run() {
		Locale.setDefault(Locale.US);
		try {
			br = new BufferedReader(new FileReader(FNAME + ".in"));
			out = new PrintWriter(FNAME + ".out");
			solve();
			out.close();
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(566);
		}
	}

	String nextToken() {
		while (st == null || !st.hasMoreTokens()) {
			try {
				st = new StringTokenizer(br.readLine());
			} catch (Exception e) {
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

	double nextDouble() {
		return Double.parseDouble(nextToken());
	}

	String FNAME = "a";

	private void solve() throws IOException {
		for (int test = 1, testn = nextInt(); test <= testn; test++) {
			out.print("Case #" + test + ": ");
			System.out.println("Case #" + test + ": ");
			long b = nextLong();
			long[] a = new long[37];
			{
				int n = nextInt();
				for (int i = 0; i < n; i++) {
					a[i] = nextLong();
				}
			}
			Arrays.sort(a);
			double max = 0;
			long s = 0;
			for (int n = 0; n < a.length; n++) {
				s += a[n];
				double s2 = 0;
				for (int l = 0; l <= n; l++) {
					s2 += a[l];
					long x = (b - (n - l) + s) / (n + 1);
					if (x >= a[l] && (l + 1 >= a.length || x + 1 >= a[n])
							&& (n + 1 >= a.length || x < a[n + 1])) {
						if (max < 36 * (x - s2 / (l + 1)) - (n + 1) * x + s
								- (n - l)) {
							max = 36 * (x - s2 / (l + 1)) - (n + 1) * x + s
									- (n - l);
						}
					}
				}
			}
			out.println(max);
		}
	}
}

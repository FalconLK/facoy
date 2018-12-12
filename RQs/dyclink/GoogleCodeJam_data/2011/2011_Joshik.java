import java.io.*;
import java.util.*;

public class SolA implements Runnable {
	public static void main(String[] args) {
		new Thread(new SolA()).start();
	}

	BufferedReader br;
	StringTokenizer st;
	PrintWriter out;
	boolean eof;

	@Override
	public void run() {
		try {
			Locale.setDefault(Locale.US);
			br = new BufferedReader(new FileReader(FNAME + ".in"));
			out = new PrintWriter(FNAME + ".out");
			solve();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
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

//	String FNAME = "A-small";

//	 String FNAME = "A-small-attempt0";
	 String FNAME = "A-large";

	void solve() {
		int tests = nextInt();
		for (int test = 1; test <= tests; test++) {
			out.print("Case #" + test + ": ");
			int w = nextInt();
			int n = nextInt();
			int m = nextInt();
			int g = nextInt();
			int[] x1 = new int[n];
			int[] y1 = new int[n];
			int[] x2 = new int[m];
			int[] y2 = new int[m];
			long s = 0;
			for (int i = 0; i < n; i++) {
				x1[i] = nextInt();
				y1[i] = nextInt();
				if (i > 0) {
					s -= (x1[i] - x1[i - 1]) * (y1[i] + y1[i - 1]);
				}
			}
			for (int i = 0; i < m; i++) {
				x2[i] = nextInt();
				y2[i] = nextInt();
				if (i > 0) {
					s += (x2[i] - x2[i - 1]) * (y2[i] + y2[i - 1]);
				}
			}
			out.println();
			double gs = 1.0 * s / g;
			int i = 0;
			int j = 0;
			double last = 0;
			double ss = 0;
			int cnt = 0;
			while (cnt < g - 1) {
				double ds = 0;
				int newx = 0;
				if (j == m - 1 || (i < n - 1 && x1[i + 1] < x2[j + 1])) {
					ds -= (x1[i + 1] - last)
							* (get(x1, y1, x1[i + 1]) + get(x1, y1, last));
					ds += (x1[i + 1] - last)
							* (get(x2, y2, x1[i + 1]) + get(x2, y2, last));
					newx = x1[i + 1];
				} else {
					ds -= (x2[j + 1] - last)
							* (get(x1, y1, x2[j + 1]) + get(x1, y1, last));
					ds += (x2[j + 1] - last)
							* (get(x2, y2, x2[j + 1]) + get(x2, y2, last));
					newx = x2[j + 1];
				}
				if (ds + ss >= gs) {
					double l = last;
					double r = newx;
					for (int iter = 0; iter < 500; iter++) {
						double dds = 0;
						double mid = (l + r) / 2;
						dds -= (mid - last)
								* (get(x1, y1, mid) + get(x1, y1, last));
						dds += (mid - last)
								* (get(x2, y2, mid) + get(x2, y2, last));
						if (dds + ss < gs) {
							l = mid;
						} else {
							r = mid;
						}
					}
					out.println((l + r) / 2);
					cnt++;
					last = (l + r) / 2;
					ss = 0;
				} else {
					ss += ds;
					last = newx;
					if (x1[i + 1] < x2[j + 1]) {
						i++;
					} else {
						j++;
					}
				}
			}
		}
	}

	double get(int[] x, int[] y, double xx) {
		int i = 1;
		while (x[i] < xx) {
			i++;
		}
		return 1.0 * (y[i] - y[i - 1]) * (xx - x[i - 1]) / (x[i] - x[i - 1])
				+ y[i - 1];
	}
}
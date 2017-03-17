import java.io.*;
import java.util.*;
import java.math.*;

public class A implements Runnable {

	static class Point {
		double x;
		double y;

		public Point(double x, double y) {
			super();
			this.x = x;
			this.y = y;
		}

	}

	void solve() {
		int t = nextInt();
		for (int test = 0; test < t; test++) {
			int w = nextInt();
			int n = nextInt();
			int m = nextInt();
			int g = nextInt();
			Point[] p = new Point[n];
			Point[] q = new Point[m];
			for (int i = 0; i < n; i++) {
				p[i] = new Point(nextDouble(), nextDouble());
			}
			for (int i = 0; i < m; i++) {
				q[i] = new Point(nextDouble(), nextDouble());
			}
			out.println("Case #" + (test + 1) + ":");
			double[] ans = solve(w, n, m, g, p, q);
			for (double i : ans) {
				out.println(i);
			}
		}
	}

	static double[] solve(int w, int n, int m, int g, Point[] p, Point[] q) {
		double area = 0;
		for (int i = 0; i + 1 < n; i++) {
			area += p[i].x * (p[i + 1].y) - p[i].y * p[i + 1].x;
		}
		for (int i = m - 1; i > 0; i--) {
			area += q[i].x * q[i - 1].y - q[i].y * q[i - 1].x;
		}
		area += p[n - 1].x * q[m - 1].y - q[m - 1].x * p[n - 1].y;
		area += q[0].x * p[0].y - p[0].x * q[0].y;
		double curArea = 0;
		double[] ans = new double[g - 1];
		for (int div = 0; div < g - 1; div++) {
			curArea += area / g;
			double l = 0;
			double r = w;
			double mid = (l + r) * .5;
			while (l != mid && r != mid) {
				double s = 0;
				double x1 = Double.NEGATIVE_INFINITY;
				double y1 = Double.NEGATIVE_INFINITY;
				double x2 = Double.NEGATIVE_INFINITY;
				double y2 = Double.NEGATIVE_INFINITY;
				for (int i = 0; i + 1 < n; i++) {
					if (compare(p[i].x, mid) <= 0
							&& compare(p[i + 1].x, mid) >= 0) {
						x1 = mid;
						y1 = (mid - p[i].x) / (p[i + 1].x - p[i].x)
								* (p[i + 1].y - p[i].y) + p[i].y;
						s += p[i].x * y1 - x1 * p[i].y;
						break;
					}
					s += p[i].x * p[i + 1].y - p[i].y * p[i + 1].x;
				}
				for (int i = 0; i + 1 < m; i++) {
					if (compare(q[i].x, mid) <= 0
							&& compare(q[i + 1].x, mid) >= 0) {
						x2 = mid;
						y2 = (mid - q[i].x) / (q[i + 1].x - q[i].x)
								* (q[i + 1].y - q[i].y) + q[i].y;
						s -= q[i].x * y2 - x2 * q[i].y;
						break;
					}
					s -= q[i].x * q[i + 1].y - q[i].y * q[i + 1].x;
				}
				s += x1 * y2 - y1 * x2;
				s += q[0].x * p[0].y - p[0].x * q[0].y;
				if (s > curArea) {
					r = mid;
				} else {
					l = mid;
				}
				mid = (l + r) * .5;
			}
			ans[div] = l;
		}
		return ans;
	}

	static final double EPS = 1e-8;

	static int compare(double a, double b) {
		return Math.abs(a - b) < EPS ? 0 : Double.compare(a, b);
	}

	FastScanner sc;
	PrintWriter out;

	public void run() {
		try {
			sc = new FastScanner("a.in");
			out = new PrintWriter("a.out");
			solve();
			sc.close();
			out.close();
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	int nextInt() {
		return sc.nextInt();
	}

	String nextToken() {
		return sc.nextToken();
	}

	long nextLong() {
		return sc.nextLong();
	}

	double nextDouble() {
		return sc.nextDouble();
	}

	BigInteger nextBigInteger() {
		return sc.nextBigInteger();
	}

	class FastScanner extends BufferedReader {
		StringTokenizer st;
		boolean eof;
		String buf;
		String curLine;
		boolean createST;

		public FastScanner(String fileName) throws FileNotFoundException {
			this(fileName, true);
		}

		public FastScanner(String fileName, boolean createST)
				throws FileNotFoundException {
			super(new FileReader(fileName));
			this.createST = createST;
			nextToken();
		}

		public FastScanner(InputStream stream) {
			this(stream, true);
		}

		public FastScanner(InputStream stream, boolean createST) {
			super(new InputStreamReader(stream));
			this.createST = createST;
			nextToken();
		}

		String nextLine() {
			String ret = curLine;
			if (createST) {
				st = null;
			}
			nextToken();
			return ret;
		}

		String nextToken() {
			if (!createST) {
				try {
					curLine = readLine();
				} catch (Exception e) {
					eof = true;
				}
				return null;
			}
			while (st == null || !st.hasMoreTokens()) {
				try {
					curLine = readLine();
					st = new StringTokenizer(curLine);
				} catch (Exception e) {
					eof = true;
					break;
				}
			}
			String ret = buf;
			buf = eof ? "-1" : st.nextToken();
			return ret;
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

		BigInteger nextBigInteger() {
			return new BigInteger(nextToken());
		}

		public void close() {
			try {
				buf = null;
				st = null;
				curLine = null;
				super.close();
			} catch (Exception e) {

			}
		}

		boolean isEOF() {
			return eof;
		}
	}

	public static void main(String[] args) {
		new A().run();
	}
}
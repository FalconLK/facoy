import java.util.*;
import java.math.*;
import java.io.*;

public class a {
	
	class Point {
		int x, y;

		public Point(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
	}
	
	int w, l, u, g;
	Point[] lower, upper;

	private void solve() throws Exception {
		w = nextInt();
		l = nextInt();
		u = nextInt();
		g = nextInt();
		lower = new Point[l];
		for (int i = 0; i < l; ++i) {
			lower[i] = new Point(nextInt(), nextInt());
		}
		upper = new Point[u];
		for (int i = 0; i < u; ++i) {
			upper[i] = new Point(nextInt(), nextInt());
		}
		double totalArea = getArea(w);
		double oneArea = totalArea / g;
		double last = 0;
		out.println();
		for (int i = 0; i < g - 1; ++i) {
			double lo = last, hi = w;
			for (int it = 0; it < 200; ++it) {
				double mid = (lo + hi) / 2.;
				double curArea = getArea(mid) - getArea(last);
				if (curArea > oneArea)
					hi = mid;
				else
					lo = mid;
			}
			last = (hi + lo) / 2.;
			out.printf("%.10f\n", last);
		}
	}
	
	double getArea(double x) {
		return getArea(upper, x) - getArea(lower, x);
	}
	
	final double LOWER_Y = -2000;

	private double getArea(Point[] p, double x) {
		double res = 0;
		for (int i = 1; i < p.length; ++i) {
			if (p[i].x > x) {
				double lastY = p[i - 1].y, curY;
				double dx = p[i].x - p[i - 1].x;
				curY = lastY + (double)(p[i].y - p[i - 1].y) * (x - p[i - 1].x) / dx;
				double midY = (lastY + curY) / 2.;
				midY += LOWER_Y;
				res += midY * (x - p[i - 1].x);
				break;
			} else {
				double lastY = p[i - 1].y, curY = p[i].y;
				double midY = (lastY + curY) / 2.;
				midY += LOWER_Y;
				res += midY * (p[i].x - p[i - 1].x);
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

	private a(String name) {
		try {
			in = new BufferedReader(new FileReader(name + ".in"));
			St = new StringTokenizer("");
			out = new PrintWriter(new FileWriter(name + ".out"));
		} catch (Exception e) {
			NOO(e);
		}
	}

	private a() {
		try {
			in = new BufferedReader(new InputStreamReader(System.in));
			St = new StringTokenizer("");
			out = new PrintWriter(System.out);
		} catch (Exception e) {
			NOO(e);
		}
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		new a("a").run();
	}
}

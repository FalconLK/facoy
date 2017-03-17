import java.io.*;
import java.util.*;

public class A {

	static class Point {
		int x, y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	static int[] getEvents(Point[] low, Point[] high) {
		TreeSet<Integer> ts = new TreeSet<Integer>();
		for (Point p : low) {
			ts.add(p.x);
		}
		for (Point p : high) {
			ts.add(p.x);
		}
		return toArray(ts);
	}

	static int[] toArray(Collection<Integer> collection) {
		int[] res = new int[collection.size()];
		int i = 0;
		for (int element : collection) {
			res[i++] = element;
		}
		return res;
	}

	private void solve() throws IOException {
		int w = nextInt();
		int l = nextInt();
		int u = nextInt();
		int guests = nextInt();
		Point[] low = new Point[l];
		for (int i = 0; i < l; i++) {
			low[i] = new Point(nextInt(), nextInt());
		}
		Point[] high = new Point[u];
		for (int i = 0; i < u; i++) {
			high[i] = new Point(nextInt(), nextInt());
		}
		int[] events = getEvents(low, high);
		double area = 0;
		int a = 0, b = 0;
		for (int i = 0; i < events.length - 1; i++) {
			int x1 = events[i];
			int x2 = events[i + 1];
			if (low[a + 1].x < x2) {
				++a;
			}
			if (high[b + 1].x < x2) {
				++b;
			}
			double d1 = getY(high[b], high[b + 1], x1) - getY(low[a], low[a + 1], x1);
			double d2 = getY(high[b], high[b + 1], x2) - getY(low[a], low[a + 1], x2);
			area += (d1 + d2) * .5 * (x2 - x1);
		}
		double slice = area / guests;
		double cur = 0;
		// System.err.println("TEST");
		// System.err.println(area);
		ArrayList<Double> res = new ArrayList<Double>();
		// System.err.println(slice);
		a = 0;
		b = 0;
		for (int i = 0; i < events.length - 1; i++) {
			int x1 = events[i];
			int x2 = events[i + 1];
			if (low[a + 1].x < x2) {
				++a;
			}
			if (high[b + 1].x < x2) {
				++b;
			}
			double d1 = getY(high[b], high[b + 1], x1) - getY(low[a], low[a + 1], x1);
			double d2 = getY(high[b], high[b + 1], x2) - getY(low[a], low[a + 1], x2);
			// System.err.println(d1 + " " + d2 + " asf ");
			double ok = (d1 + d2) * .5 * (x2 - x1);
			// System.err.println(ok + " ok " + slice + " " + x1 + " " + x2);
			// System.err.println(x1 + " " + x2 + " -> " + ok + " " + d1 + " " +
			// d2);

			// System.err.println(cur);
			double x_1 = x1;

			while (cur + ok >= slice && res.size() < guests - 1) {
				double xx = getX(d1, d2, x2 - x_1, slice - cur) + x_1;
				// System.err.println("CUT AT " + xx);
				res.add(xx);
				// System.err.println("SLICE");
				x_1 = xx;
				d1 = getY(high[b], high[b + 1], x_1) - getY(low[a], low[a + 1], x_1);
				ok = (d1 + d2) * .5 * (x2 - x_1);
				cur = 0;
			}
			cur += ok;
		}
		if (Math.abs(cur - slice) >= 1e-6) {
			System.err.println("BAD " + cur + " " + slice);
		}
		out.println();
		for (double d : res) {
			out.println(d);
		}

	}

	double getX(double d1, double d2, double dx, double want) {
		double k = (d2 - d1) / dx;
		if (Math.abs(k) < 1e-9) {
			return want / d1;
		}
		double t = Math.sqrt(Math.max(d1 * d1 + 2 * k * want, 0));
		double res = (t - d1) / k;
		if (res < 1e-8 || res > dx + 1e-8) {
			res = (-t - d1) / k;
			System.err.println("ASDAS");
		}
		// double res = k < 0 ? (-t - d1) / k : (t - d1) / k;
		double temp = (d1 + (d1 + k * res)) * .5 * res;
		if (Math.abs(temp - want) > 1e-5) {
			System.err.println("WTF ");
			System.err.println(temp + " = " + want + " " + res);
		}
		return res;
	}

	double getY(Point p1, Point p2, double x) {
		double dy = p2.y - p1.y;
		double dx = p2.x - p1.x;
		return p1.y + (x - p1.x) * dy / dx;
	}

	public static void main(String[] args) {
		new A().run();
	}

	BufferedReader br;
	StringTokenizer st;
	PrintWriter out;

	static final boolean large = false;

	public void run() {
		try {
			String fileName = "A-" + (large ? "large" : "small");
			br = new BufferedReader(new FileReader(fileName + ".in"));
			out = new PrintWriter(fileName + ".out");
			st = new StringTokenizer("");
			int T = nextInt();
			for (int i = 1; i <= T; i++) {
				out.print("Case #" + i + ": ");
				solve();
			}
			out.close();
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(239);
		}
	}

	String nextToken() throws IOException {
		while (!st.hasMoreTokens()) {
			String line = br.readLine();
			if (line == null) {
				return null;
			}
			st = new StringTokenizer(line);
		}
		return st.nextToken();
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
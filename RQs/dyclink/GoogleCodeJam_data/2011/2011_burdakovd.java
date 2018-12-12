import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class A {

	static {
		final Locale us = Locale.US;
		if (!Locale.getDefault().equals(us)) {
			Locale.setDefault(us);
		}
	}

	static boolean file = true;
	static boolean isLocal = true;

	private static int nextInt() throws IOException {
		in.nextToken();
		return (int) in.nval;
	}

	private static String nextWord() throws IOException {
		in.nextToken();
		return in.sval;
	}

	static StreamTokenizer in;
	static {
		try {
			// in = new Scanner(file ? new
			// FileInputStream("f:\\var\\tmp\\in.txt")
			// : System.in);

			// in = new BufferedReader(new InputStreamReader(
			// file ? new FileInputStream("f:\\var\\tmp\\in.txt")
			// : System.in));

			in = new StreamTokenizer(new BufferedReader(new InputStreamReader(
					file ? new FileInputStream("f:\\var\\tmp\\in.txt")
							: System.in)));
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	static PrintWriter out;
	static {
		try {
			out = file ? new PrintWriter(
					new FileWriter("f:\\var\\tmp\\out.txt")) : new PrintWriter(
					System.out);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	static PrintStream err;
	static {
		err = System.err;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final long startTime = System.nanoTime();
		final int t = nextInt();

		for (int i = 0; i < t; ++i) {
			solve(i + 1);
			if (isLocal && file) {
				err.println(i + 1 + "/" + t);
			}
			if (isLocal && !file) {
				out.flush();
			}
		}

		if (isLocal) {
			err.println(String.format("Completed after %d ms.",
					(System.nanoTime() - startTime) / 1000000));
		}

		out.flush();
		if (file) {
			out.close();
		}

	}

	private static final class Point {
		public final double x, y;

		@Override
		public String toString() {
			return String.format("Point [x=%s, y=%s]", x, y);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Point other = (Point) obj;
			if (x != other.x) {
				return false;
			}
			if (y != other.y) {
				return false;
			}
			return true;
		}

		public Point(final int x, final int y) {
			this.x = x;
			this.y = y;
		}
	}

	private static void solve(final int testId) throws IOException {
		final int w = nextInt();
		final int l = nextInt();
		final int u = nextInt();
		final int g = nextInt();

		double area = 0;

		final List<Point> lower = new ArrayList<Point>();
		for (int i = 0; i < l; ++i) {
			final Point next = nextPoint();
			if (!lower.isEmpty()) {
				area -= (next.x - lower.get(lower.size() - 1).x)
						* (next.y + lower.get(lower.size() - 1).y);
			}
			lower.add(next);
		}

		final List<Point> upper = new ArrayList<Point>();
		for (int i = 0; i < u; ++i) {
			final Point next = nextPoint();
			if (!upper.isEmpty()) {
				area += (next.x - upper.get(upper.size() - 1).x)
						* (next.y + upper.get(upper.size() - 1).y);
			}
			upper.add(next);
		}

		final double delta = area / g;

		out.printf("Case #%d:\n", testId);

		for (int i = 1; i < g; ++i) {
			out.printf("%.9f\n", solve(lower, upper, i * delta, w));
		}
	}

	private static double solve(final List<Point> lower,
			final List<Point> upper, final double d, final int w) {

		double left = 1e-10, right = w - 1e-10;

		while (right - left > 1e-7) {
			final double m = (right + left) / 2;
			final double willBe = sum(upper, m) - sum(lower, m);
			if (willBe < d) {
				left = m;
			} else {
				right = m;
			}
		}

		return (left + right) / 2;
	}

	private static double sum(final List<Point> points, final double m) {
		double area = 0;
		int i = 0;
		while (m >= points.get(i).x) {
			if (i > 0) {
				area += (points.get(i).x - points.get(i - 1).x)
						* (points.get(i).y + points.get(i - 1).y);
			}
			++i;
		}
		--i;
		final double dx = m - points.get(i).x;
		area += dx
				* (2 * points.get(i).y + dx
						* (points.get(i + 1).y - points.get(i).y)
						/ (points.get(i + 1).x - points.get(i).x));
		return area;
	}

	private static Point nextPoint() throws IOException {
		final int x = nextInt();
		final int y = nextInt();
		return new Point(x, y);
	}
}

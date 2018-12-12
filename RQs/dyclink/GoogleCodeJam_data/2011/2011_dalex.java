
import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;
import static java.lang.Math.*;

/**
 * Google Code Jam 2011
 * Round 3
 * Problem A
 * 
 * @author dalex
 */
public class A implements Runnable {
	
	BufferedReader in;
	PrintWriter out;
	StringTokenizer tok = new StringTokenizer("");
	
	String readString() throws IOException {
		while (!tok.hasMoreTokens()) {
			tok = new StringTokenizer(in.readLine());
		}
		return tok.nextToken();
	}
	
	int readInt() throws IOException {
		return Integer.parseInt(readString());
	}
	
	long readLong() throws IOException {
		return Long.parseLong(readString());
	}
	
	double readDouble() throws IOException {
		return Double.parseDouble(readString());
	}
	
	@Override
	public void run() {
		try {
			long t1 = System.currentTimeMillis();
			in = new BufferedReader(new FileReader("input.txt"));
			out = new PrintWriter("output.txt");
			Locale.setDefault(Locale.US);
			int tests = readInt();
			int p = max(1, tests / 10);
			for (int t = 1; t <= tests; t++) {
				out.printf("Case #%d:\n", t);
				solve();
				if (t % p == 0) System.err.println("Test " + t + " of " + tests + " completed");
			}
			in.close();
			out.close();
			long t2 = System.currentTimeMillis();
			System.err.println("Time = " + (t2 - t1));
		} catch (Throwable e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}
	}
	
	public static void main(String[] args) {
		new Thread(null, new A(), "", 256 * (1L << 20)).start();
	}
	
	// solution
	
	class Point extends Point2D.Double implements Comparable<Point> {

		public Point() {
			super();
			// TODO Auto-generated constructor stub
		}

		public Point(double arg0, double arg1) {
			super(arg0, arg1);
			// TODO Auto-generated constructor stub
		}

		@Override
		public int compareTo(Point o) {
			if (this.x < o.x) return -1;
			if (this.x > o.x) return 1;
			return 0;
		}
		
		@Override
		public String toString() {
			return "[" + x + ", " + y + "]";
		}
		
	}
	
	Point[] a, b;
	int n, m;
	int g;
	double w;
	
	void solve() throws IOException {
		w = readInt();
		n = readInt();
		m = readInt();
		g = readInt();
		a = new Point[n+m];
		b = new Point[m+n];
		for (int i = 0; i < n; i++) {
			a[i] = new Point(readInt(), readInt());
		}
		for (int j = 0; j < m; j++) {
			b[j] = new Point(readInt(), readInt());
		}
		for (int i = n; i < n + m; i++) {
			a[i] = (Point) b[m + n - i - 1].clone();
		}
		for (int j = m; j < n + m; j++) {
			b[j] = (Point) a[m + n - j - 1].clone();
		}
		
		double S = 0;
		for (int i = 0; i < n+m; i++) {
			S += a[(i) % (n+m)].x * a[(i+1) % (n+m)].y - a[(i+1) % (n+m)].x * a[(i) % (n+m)].y;
		}
		S = 0.5 * abs(S);
		S = S / g;
		
		for (int k = 1; k <= g-1; k++) {
			double left = 0, right = w, mid = -1;
			for (int it = 0; it < 111; it++) {
				mid = (left + right) / 2;
				double sq = sq(mid);
				if (abs(sq - S*k) < 1e-12) break;
				if (sq > S*k) {
					right = mid;
				} else {
					left = mid;
				}
			}
			out.printf("%.12f\n", mid);
		}
	}
	
	double sq(double x) {
		int pos1 = Arrays.binarySearch(a, 0, n, new Point(x,0));
		int pos2 = Arrays.binarySearch(b, 0, m, new Point(x,0));
		Point p1 = null, p2 = null;
		boolean f1 = false, f2 = false;
		if (pos1 >= 0) { p1 = a[pos1]; f1 = true; }
		if (pos2 >= 0) { p2 = b[pos2]; f2 = true; }
		if (pos1 < 0) {
			pos1 = -pos1 - 1;
			Point left = a[pos1 - 1];
			Point right = a[pos1];
			double y = left.y + ((x - left.x) * (right.y - left.y)) / (right.x - left.x);
			p1 = new Point(x, y);
		}
		if (pos2 < 0) {
			pos2 = -pos2 - 1;
			Point left = b[pos2 - 1];
			Point right = b[pos2];
			double y = left.y + ((x - left.x) * (right.y - left.y)) / (right.x - left.x);
			p2 = new Point(x, y);
		}
		Point[] t = new Point[n + m + 2];
		int j = 0;
		for (int i = 0; i < pos1; i++) {
			t[j++] = a[i];
		}
		if (!f1) t[j++] = p1; else t[j++] = a[pos1];
		if (!f2) t[j++] = p2; else t[j++] = b[pos2];
		for (int i = pos2 - 1; i >= 0; i--) {
			t[j++] = b[i];
		}
		double S = 0;
		for (int i = 0; i < j; i++) {
			S += t[(i) % j].x * t[(i+1) % j].y - t[(i+1) % j].x * t[(i) % j].y;
		}
		S = 0.5 * abs(S);
		return S;
	}
	
}

import java.io.*;
import java.util.*;

public class IrregularCake {

	class Point {
		double x, y;

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	Point[] low;
	Point[] up;
	
	double get(Point a, Point b, double x) {
		return (b.y - a.y) / (b.x - a.x) * (x - a.x) + a.y; 
	}
	
	double getsq(double x) {
		
		double area = 0;
		int t1 = -1;
		int t2 = -1;
		for (int i = 0; i + 1 < up.length; i++) {
			if (up[i + 1].x >= x) {
				t1 = i;
				break;
			}
			area += (up[i].y + up[i + 1].y) * (up[i + 1].x - up[i].x) / 2;
		}
		
		for (int i = 0; i  + 1< low.length; i++) {
			if (low[i + 1].x >= x) {
				t2 = i;
				break;
			}
			area -= (low[i].y + low[i + 1].y) * (low[i + 1].x - low[i].x) / 2;
		}
		
		double y1 = get(up[t1], up[t1 + 1], x);

		double y2 = get(low[t2], low[t2 + 1], x);
		
		area += (y1 + up[t1].y) * (x - up[t1].x) / 2;
		area -= (y2 + low[t2].y) * (x - low[t2].x) / 2;
		return area;
	}
	
	void solve() throws Exception {
		double w = nextDouble();
		int l = nextInt();
		int u = nextInt();
		int g = nextInt();
		low = new Point[l];
		up = new Point[u];
		for (int i = 0; i < l; i++)
			low[i] = new Point(nextInt(), nextInt());
		for (int i = 0; i < u; i++)
			up[i] = new Point(nextInt(), nextInt());
		double area = 0;
		for (int i = 0; i + 1 < u; i++)
			area += (up[i].y + up[i + 1].y) * (up[i + 1].x - up[i].x) / 2;

		for (int i = 0; i + 1 < l; i++)
			area -= (low[i].y + low[i + 1].y) * (low[i + 1].x - low[i].x) / 2;
		
		double s = area / g;
		
		for (int i = 1; i < g; i++) {
			double left = 0;
			double right = w;
			for (int it = 0; it < 1000; it++) {
				double mid = (left + right) / 2;
				double f = getsq(mid);
				if (f > i * s)
					right = mid;
				else
					left = mid;
			}
			out.println(left);
		}
	}

	void run() {
		try {
			in = new BufferedReader(new FileReader("input.txt"));
			out = new PrintWriter("output.txt");
			int tests = nextInt();
			for (int i = 0; i < tests; i++) {
				out.println("Case #" + (i + 1) + ": ");
				solve();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	BufferedReader in;
	StringTokenizer st;
	PrintWriter out;
	final String filename = new String("IrregularCake").toLowerCase();

	String nextToken() throws Exception {
		while (st == null || !st.hasMoreTokens())
			st = new StringTokenizer(in.readLine());
		return st.nextToken();
	}

	int nextInt() throws Exception {
		return Integer.parseInt(nextToken());
	}

	long nextLong() throws Exception {
		return Long.parseLong(nextToken());
	}

	double nextDouble() throws Exception {
		return Double.parseDouble(nextToken());
	}

	public static void main(String[] args) {
		new IrregularCake().run();
	}

}

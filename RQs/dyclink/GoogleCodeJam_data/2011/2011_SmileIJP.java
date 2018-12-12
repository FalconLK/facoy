import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;


public class A implements Runnable {

	
	class Point {
		int x, y;
		Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	class Seg {
		int x;
		double yu, yl;
		double dif= 0;
		Seg(int x, double yu, double yl) {		
			this.x = x;
			this.yu = yu;
			this.yl = yl;
			this.dif = yu - yl;
		}
	}
	
	double getY(Point a, Point b, double x) {
		return (double)(a.y - b.y) / (double)(a.x - b.x) * (double)(x - b.x) + (double)b.y;
	}
	
	double getX(double sq, Seg A, Seg B, double xf) {
		double EPS = 1e-9;
		double up = B.x;
		double down = xf;
		double S = 0;
		double alpha = (B.dif - A.dif) / (double)(B.x - A.x);
		double b = A.dif;
		while (Math.abs(up - down) > EPS) {
			double mid = (up + down) / 2.;
			S = (mid - xf) * (alpha * (mid + xf - 2 * A.x) + 2 * b) / 2.;
			if (S < sq) {
				down = mid;
			} else {
				up = mid;
			}
		}
		return (up + down) / 2.;		
	}
	
	private void solve() throws IOException {
		int W = nextInt();
		int L = nextInt();
		int U = nextInt();
		int G = nextInt();
		Point[] up = new Point[U];
		Point[] low = new Point[L];
		for (int i = 0; i < L; ++i) {
			int x = nextInt();
			int y = nextInt();			
			low[i] = new Point(x, y);
		}
		for (int i = 0; i < U; ++i) {
			int x = nextInt();
			int y = nextInt();
			up[i] = new Point(x, y);
		}
		ArrayList<Seg> ls = new ArrayList<A.Seg>();
		for (int i = 0, j = 0; i < L && j < U;) {
			if (low[i].x == up[j].x) {
				ls.add(new Seg(low[i].x, up[j].y, low[i].y));
				++i;
				++j;
			} else if (low[i].x < up[j].x) {
				double yu = getY(up[j], up[j - 1], low[i].x);
				ls.add(new Seg(low[i].x, yu, low[i].y));				
				++i;
			} else {
				double yl = getY(low[i], low[i - 1], up[j].x);
				ls.add(new Seg(up[j].x, up[j].y, yl));
				++j;
			}
		}
		double x0 = 0;
		double y0 = ls.get(0).dif;
		double S = 0;
		for (int i = 1; i < ls.size(); ++i) {
			S += (y0 + ls.get(i).dif) / 2. * (ls.get(i).x - x0);
//			System.err.println(ls.get(i).x + " " + ls.get(i).yl + " " + ls.get(i).yu);
			x0 = ls.get(i).x;
			y0 = ls.get(i).dif;
		}
		S /= (double)G;
		double curS = 0;
		double ts = 0;
		double xf = 0;
		x0 = 0;
		y0 = ls.get(0).dif;
		ArrayList<Double > ans = new ArrayList<Double>();
		for (int i = 1; i < ls.size(); ++i) {
			ts = (y0 + ls.get(i).dif) / 2. * (ls.get(i).x - x0);
			xf = x0;
			while (curS + ts > S) {
				xf = getX(S - curS, ls.get(i - 1), ls.get(i), xf);
				ans.add(xf);
				ts -= S - curS;
				curS = 0;				
			}
			curS += ts;
			
			x0 = ls.get(i).x;
			y0 = ls.get(i).dif;
		}
		if (ans.size() == G) {
			ans.remove(G - 1);
		}
		out.println();
		for (Double x : ans) {
			out.printf("%.8f\n", x);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Thread(new A()).start();
	}

	private BufferedReader br;
	private StringTokenizer st;
	private PrintWriter out;
	
	@Override
	public void run() {
		try {
			Locale.setDefault(Locale.US);
			br = new BufferedReader(new FileReader("input.txt"));
			st = new StringTokenizer("");
			out = new PrintWriter("out.txt");

			
			int T = nextInt();
			for (int i = 1; i <= T; ++i) {
				out.print("Case #" + i + ": ");
				solve();
			}
			out.close();			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}

	
	
	String next() throws IOException {
		while (!st.hasMoreTokens()) {
			String temp = br.readLine();
			if (temp == null) {
				return null;
			}
			st = new StringTokenizer(temp);
		}
		return st.nextToken();
	}

	int nextInt() throws IOException {
		return Integer.parseInt(next());
	}
	double nextDouble() throws IOException {
		return Double.parseDouble(next());
	}
	long nextLong() throws IOException {
		return Long.parseLong(next());
	}
	
}

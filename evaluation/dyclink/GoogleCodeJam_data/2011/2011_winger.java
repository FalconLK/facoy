import java.io.*;
import java.util.*;

public class Solution implements Runnable {


	private StringTokenizer st;
	private BufferedReader in;
	private PrintWriter out;
	
	final String file = "A-small-attempt0";

	static class Vector {
		double x, y;
		
		Vector(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		Vector diff(Vector o) {
			return new Vector(x - o.x, y - o.y);
		}
		
		Vector add(Vector o) {
			return new Vector(x + o.x, y + o.y);
		}
		
		Vector mul(double o) {
			return new Vector(x * o, y * o);
		}
	}
	
	public void solve(int test) throws IOException {
		out.println("Case #" + (test + 1) + ": ");
		int w = nextInt();
		int n1 = nextInt();
		int n2 = nextInt();
		int m = nextInt();
		Vector[] b1 = new Vector[n1];
		Vector[] b2 = new Vector[n2];
		for (int i = 0; i < n1; ++i) {
			b1[i] = new Vector(nextDouble(), nextDouble());
		}
		for (int i = 0; i < n2; ++i) {
			b2[i] = new Vector(nextDouble(), nextDouble());
		}
		double s0 = 0.;
		for (int i = 0; i + 1 < n1; ++i) {
			s0 -= (b1[i].y + b1[i + 1].y) * (b1[i + 1].x - b1[i].x);
		}
		for (int i = 0; i + 1 < n2; ++i) {
			s0 += (b2[i].y + b2[i + 1].y) * (b2[i + 1].x - b2[i].x);
		}
		for (int t = 1; t < m; ++t) {
			double l = 0., r = w;
			for (int it = 0; it < 100; ++it) {
				double mid = (l + r) / 2.;
				double s = 0.;
				for (int i = 0; i + 1 < n1; ++i) {
					if (b1[i + 1].x > mid) {
						s -= (2 * b1[i].y + (b1[i + 1].y - b1[i].y) * (mid - b1[i].x) / (b1[i + 1].x - b1[i].x)) * (mid - b1[i].x);
						break;
					} else {
						s -= (b1[i].y + b1[i + 1].y) * (b1[i + 1].x - b1[i].x);
					}
				}
				for (int i = 0; i + 1 < n2; ++i) {
					if (b2[i + 1].x > mid) {
						s += (2 * b2[i].y + (b2[i + 1].y - b2[i].y) * (mid - b2[i].x) / (b2[i + 1].x - b2[i].x)) * (mid - b2[i].x);
						break;
					} else {
						s += (b2[i].y + b2[i + 1].y) * (b2[i + 1].x - b2[i].x);
					}
				}
				if (s * m > s0 * t) {
					r = mid;
				} else {
					l = mid;
				}
			}
			out.println((l + r) / 2.);
		}
	}

	public void run() {
		try {
			in = new BufferedReader(new FileReader(file + ".in"));
			out = new PrintWriter(file + ".out");
			eat("");
			
			int tests = nextInt();
			for (int test = 0; test < tests; ++test) {
				solve(test);
			}
			
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			failed = true;
		}
	}
	
	void eat(String s) {
		st = new StringTokenizer(s);
	}
	
	String next() throws IOException {
		while (!st.hasMoreTokens()) {
			String line = in.readLine();
			if (line == null) {
				return null;
			}
			eat(line);
		}
		return st.nextToken();
	}
	
	int nextInt() throws IOException {
		return Integer.parseInt(next());
	}
	
	long nextLong() throws IOException {
		return Long.parseLong(next());
	}
	
	double nextDouble() throws IOException {
		return Double.parseDouble(next());
	}
	
	static boolean failed = false;
	
	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		Thread th = new Thread(new Solution());
		th.start();
		try {
			th.join();
		} catch (InterruptedException iex) {
		}
		if (failed) {
			throw new RuntimeException();
		}
	}
	
}

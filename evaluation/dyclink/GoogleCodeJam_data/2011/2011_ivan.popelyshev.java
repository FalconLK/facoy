import java.io.*;

import java.awt.geom.Point2D;
import java.text.*;
import java.math.*;
import java.util.*;

public class Main implements Runnable {

	final String problem = "A";
//	final String filename = problem + "-sample";

//	 final String filename= problem+"-small-attempt0";
	// final String filename= problem+"-small-attempt1";
	 final String filename= problem+"-large";

	public void solve() throws Exception {
		int W = iread();
		int L = iread(), U = iread(), G = iread();
		TreeSet<Integer> xx = new TreeSet<Integer>();
		int[][] x = new int[][] { new int[L], new int[U] }, y = new int[][] {
				new int[L], new int[U] };
		int[] c = new int[] { L, U };
		for (int j = 0; j < 2; j++)
			for (int i = 0; i < c[j]; i++) {
				x[j][i] = iread();
				xx.add(x[j][i]);
				y[j][i] = iread();
			}
		Integer[] xxx = xx.toArray(new Integer[] {});
		int n = xxx.length;
		double[][] y1 = new double[2][n];
		int a = 0, b = 0;
		for (int i = 0; i < n; i++) {
			while (xxx[i] > x[0][a + 1])
				a++;
			while (xxx[i] > x[1][b + 1])
				b++;
			y1[0][i] = (y[0][a + 1] - y[0][a]) * 1.0 * (xxx[i] - x[0][a])
					/ (x[0][a + 1] - x[0][a]) + y[0][a];
			y1[1][i] = (y[1][b + 1] - y[1][b]) * 1.0 * (xxx[i] - x[1][b])
					/ (x[1][b + 1] - x[1][b]) + y[1][b];
		}
		double[] ans = new double[G - 1];
		double S = 0.0;
		for (int i = 0; i + 1 < n; i++) {
			double dy1 = y1[1][i] - y1[0][i], dy2 = y1[1][i + 1] - y1[0][i + 1], dx = xxx[i + 1]
					- xxx[i];
			S += (dy1 + dy2) * dx / 2.0;
		}

		double S_left = 0.0;
		int j = 1;
		for (int i = 0; i + 1 < n; i++) {
			double dy1 = y1[1][i] - y1[0][i], dy2 = y1[1][i + 1] - y1[0][i + 1], dx = xxx[i + 1]
					- xxx[i];
			double S1 = (dy1 + dy2) * dx / 2.0;
			while (j < G) {
				double S0 = j * S / G - S_left;
				if (S0 < 0.0) {
					j++;
					continue;
				} else if (S0 > S1) {
					break;
				}
				double left = 0.0, right = dx;
				for (int k = 0; k < 60; k++) {
					double mid = (left + right) / 2.0;
					double dy2_ = (dy2 - dy1) * (mid / dx) + dy1;
					double S2 = (dy1 + dy2_) * mid / 2.0;
					if (S2 < S0)
						left = mid;
					else
						right = mid;
				}
				ans[j - 1] = (left + right) / 2.0 + xxx[i];
				j++;
			}
			S_left += S1;
		}
		DecimalFormat df = new DecimalFormat("0.000000000");
		for (int i = 0; i < G - 1; i++) {
			out.write("\n" + df.format(ans[i]));
		}
	}

	public void solve_gcj() throws Exception {
		int tests = iread();
		for (int test = 1; test <= tests; test++) {
			out.write("Case #" + test + ": ");
			solve();
			out.write("\n");
		}
	}

	public void run() {
		try {
			// in = new BufferedReader(new InputStreamReader(System.in));
			// out = new BufferedWriter(new OutputStreamWriter(System.out));
			in = new BufferedReader(new FileReader(filename + ".in"));
			out = new BufferedWriter(new FileWriter(filename + ".out"));
			solve_gcj();
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public int iread() throws Exception {
		return Integer.parseInt(readword());
	}

	public double dread() throws Exception {
		return Double.parseDouble(readword());
	}

	public long lread() throws Exception {
		return Long.parseLong(readword());
	}

	BufferedReader in;

	BufferedWriter out;

	public String readword() throws IOException {
		StringBuilder b = new StringBuilder();
		int c;
		c = in.read();
		while (c >= 0 && c <= ' ')
			c = in.read();
		if (c < 0)
			return "";
		while (c > ' ') {
			b.append((char) c);
			c = in.read();
		}
		return b.toString();
	}

	public static void main(String[] args) {
		try {
			Locale.setDefault(Locale.US);
		} catch (Exception e) {

		}
		new Thread(new Main()).start();
		// new Thread(null, new Main(), "1", 1<<25).start();
	}
}
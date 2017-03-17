import java.io.*;
import java.util.*;

public class A {
	private static String fileName = A.class.getSimpleName().replaceFirst("_.*", "").toLowerCase();
	private static String inputFileName = fileName + ".in";
	private static String outputFileName = fileName + ".out";
	private static Scanner in;
	private static PrintWriter out;

	class Polyline {
		int n;
		int[] x;
		int[] y;
		
		public Polyline(int n, Scanner in) {
			this.n = n;
			x = new int[n];
			y = new int[n];
			for (int i = 0; i < n; i++) {
				x[i] = in.nextInt();
				y[i] = in.nextInt();
			}
		}

		public double area(double t) {
			double res = 0;
			for (int i = 0; i < n - 1; i++) {
				double x0 = x[i];
				double y0 = y[i];
				double x1 = x[i + 1];
				double y1 = y[i + 1];
				boolean last = (t <= x[i + 1]);
				if (last) {
					y1 = y0 + (y1 - y0) * (t - x0) / (x1 - x0);
					x1 = t;
				}
				res += (x1 - x0) * (y1 + y0);
				if (last) {
					break;
				}
			}
			return res;
		}
	}
	
	private void solve() {
		int X = in.nextInt();
		int an = in.nextInt();
		int bn = in.nextInt();
		int g = in.nextInt();
		Polyline low = new Polyline(an, in);
		Polyline high = new Polyline(bn, in);
		double area = high.area(X) - low.area(X);
		out.println();
		double eps = 1e-9;
		for (int i = 1; i < g; i++) {
			double need = i * area / g;
			double l = 0;
			double r = X;
			while (l + eps < r) {
				double x = (l + r) / 2;
				double a = high.area(x) - low.area(x);
				if (a < need) {
					l = x;
				} else {
					r = x;
				}
			}
			out.println(l);
		}
	}

	public static void main(String[] args) throws IOException {
		Locale.setDefault(Locale.US);
		if (args.length >= 2) {
			inputFileName = args[0];
			outputFileName = args[1];
		}
		in = new Scanner(new FileReader(inputFileName));
		out = new PrintWriter(outputFileName);
		int tests = in.nextInt();
		in.nextLine();
		for (int t = 1; t <= tests; t++) {
			out.print("Case #" + t + ": ");
			new A().solve();
			System.out.println("Case #" + t + ": solved");
		}
		in.close();
		out.close();
	}
}

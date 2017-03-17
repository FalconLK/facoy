import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Cakes {
	static class P {
		double x, y;

		public P(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "("+x+", "+y+")";
		}
	}
	
	static double getArea(P p0, P p1) {
		return (p1.x - p0.x) * (p0.y + p1.y) / 2;
	}
	
	static double getArea(P[] p) {
		return getArea(p, 0);
	}
	
	static double getArea(P[] p, double x) {
		double s = 0;
		for (int i = 0; i < p.length - 1; ++i) {
			P p1 = p[i];
			P p2 = p[i+1];
			if (p2.x <= x) {
				s += getArea(p1, p2);
				continue;
			}
			if (p1.x >= x) {
				break;
			}
			double y = p1.y + (p2.y - p1.y) * (x - p1.x) / (p2.x - p1.x); 
			s += getArea(p1, new P(x, y));
		}
		return s;
	}
	
	static double getArea(P[] p, int x) {
		double s = 0;
		for (int i = x; i < p.length - 1; ++i) {
			s += getArea(p[i], p[i + 1]);
		}
		return s;
	}
	
	static double getArea(P l0, P l1, P u0, P u1) {
		return getArea(u0, u1) - getArea(l0, l1);
	}
	
	public static void main(final String... args) throws IOException {
		final String fname = "A-large";
		final Scanner sc = new Scanner(new File(fname + ".in"));
		final PrintWriter pw = new PrintWriter(fname + ".out");
		final int t = sc.nextInt();
		
		for (int i = 0; i < t; ++i) {
			final int w = sc.nextInt();
			final int l = sc.nextInt();
			final int u = sc.nextInt();
			final int g = sc.nextInt();
			P[] lb = new P[l];
			P[] ub = new P[u];
			for (int j = 0; j < l; ++j) {
				lb[j] = new P(sc.nextInt(), sc.nextInt());
			}
			for (int j = 0; j < u; ++j) {
				ub[j] = new P(sc.nextInt(), sc.nextInt());
			}
			pw.println("Case #" + (i + 1) + ":");
			double a = getArea(ub) - getArea(lb);
			for (int j = 0; j < g - 1; ++j) {
				double q = get(lb, ub, (j + 1.0) / g * a);
				pw.println(q);
			}
		}
		pw.close();
	}

	private static double get(P[] lb, P[] ub, double a) {
		double x1 = 0;
		double x2 = lb[lb.length - 1].x;
		while (x2 - x1 > 1e-7) {
			double m = (x1 + x2) / 2;
			if (getArea(ub, m) - getArea(lb, m) > a) {
				x2 = m;
			}
			else {
				x1 = m;
			}
		}
		return (x1 + x2) / 2;
	}
}

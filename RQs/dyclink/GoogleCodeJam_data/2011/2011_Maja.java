package round3;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class C {
	static class Tacka {
		double x, y;
	}

	static double povrsinaDo(double x) {
		int ui = 0;
		while (u[ui].x < x)
			ui++;
		Tacka au = new Tacka();
		au.x = x;
		au.y = u[ui - 1].y + ((x - u[ui - 1].x) / (u[ui].x - u[ui - 1].x))
				* (u[ui].y - u[ui - 1].y);
		int li = 0;
		while (l[li].x < x)
			li++;
		Tacka al = new Tacka();
		al.x = x;
		al.y = l[li - 1].y + ((x - l[li - 1].x) / (l[li].x - l[li - 1].x))
				* (l[li].y - l[li - 1].y);

		double ret = 0;

		for (int i = 0; i < li - 1; i++)
			ret += l[i].x * l[i + 1].y;
		ret += l[li - 1].x * al.y;
		ret += al.x * au.y;
		ret += au.x * u[ui - 1].y;
		for (int i = ui - 1; i > 0; i--)
			ret += u[i].x * u[i - 1].y;
		ret += u[0].x * l[0].y;

		for (int i = 0; i < li - 1; i++)
			ret -= l[i].y * l[i + 1].x;
		ret -= l[li - 1].y * al.x;
		ret -= al.y * au.x;
		ret -= au.y * u[ui - 1].x;
		for (int i = ui - 1; i > 0; i--)
			ret -= u[i].y * u[i - 1].x;
		ret -= u[0].y * l[0].x;

		return ret;
	}
	static Tacka[] l, u;

	static double ukupnaPovrsina() {
		double ret = 0;

		for (int i = 0; i < l.length - 1; i++)
			ret += l[i].x * l[i + 1].y;
		ret += l[l.length - 1].x * u[u.length - 1].y;
		for (int i = u.length - 1; i > 0; i--)
			ret += u[i].x * u[i - 1].y;
		ret += u[0].x * l[0].y;

		for (int i = 0; i < l.length - 1; i++)
			ret -= l[i].y * l[i + 1].x;
		ret -= l[l.length - 1].y * u[u.length - 1].x;
		for (int i = u.length - 1; i > 0; i--)
			ret -= u[i].y * u[i - 1].x;
		ret -= u[0].y * l[0].x;

		return Math.abs(ret);
	}

	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new File("C.in"));
		PrintWriter out = new PrintWriter(new File("C.out"));
		int tt = in.nextInt();
		for (int ttt = 1; ttt <= tt; ttt++) {
			int w = in.nextInt();
			int ln = in.nextInt();
			int un = in.nextInt();
			int g = in.nextInt();

			l = new Tacka[ln];
			for (int i = 0; i < ln; i++) {
				l[i] = new Tacka();
				l[i].x = in.nextInt();
				l[i].y = in.nextInt();
			}
			u = new Tacka[un];
			for (int i = 0; i < un; i++) {
				u[i] = new Tacka();
				u[i].x = in.nextInt();
				u[i].y = in.nextInt();
			}

			double trazenaPovrsina = ukupnaPovrsina() / (double) g;

			double[] res = new double[g - 1];
			for (int i = 0; i < g - 1; i++) {
				double p = (i + 1) * trazenaPovrsina;
				double lg = 0;
				double dg = w;
				int counter = 0;
				while (lg + 0.00000001 < dg && counter < 1000) {
					counter++;
					double s = (lg + dg) / 2;
					double tp = povrsinaDo(s);
					if (tp < p)
						lg = s;
					else
						dg = s;
				}
				if (counter == 1000)
					System.err.println((dg - lg) + " " + lg + " " + dg);
				res[i] = (lg + dg) / 2;
			}

			out.printf("Case #%d: ", ttt);
			out.println();
			for (int i = 0; i < g - 1; i++) {
				out.printf("%.8f", res[i]);
				out.println();
			}
		}
		out.flush();
		out.close();
		in.close();
	}
}

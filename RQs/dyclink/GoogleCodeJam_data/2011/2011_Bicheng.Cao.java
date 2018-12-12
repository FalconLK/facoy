import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * @author Bicheng Cao
 */
public class A {
	
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new FileInputStream("input.txt"));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("output.txt")));
		int tests = Integer.parseInt(in.nextLine());
		for (int t = 0; t < tests; t++) {
			int w = in.nextInt();
			int l = in.nextInt();
			int u = in.nextInt();
			int g = in.nextInt();
			if (t == 5) {
				System.out.println(w + " " + l + " " + u + " " + g);
			}
			double[] len = new double[w + 1];
			double[] low = new double[w + 1];
			int x0 = in.nextInt();
			int y0 = in.nextInt();
			low[x0] = y0;
			for (int i = 1; i < l; i++) {
				int x = in.nextInt();
				int y = in.nextInt();
				double k = 1.0 * (y - y0) / (x - x0);
				for (int j = x0 + 1; j <= x; j++) {
					low[j] = k * (j - x0) + y0;
				}
				x0 = x;
				y0 = y;
			}
			double[] upper = new double[w + 1];
			x0 = in.nextInt();
			y0 = in.nextInt();
			upper[x0] = y0;
			for (int i = 1; i < u; i++) {
				int x = in.nextInt();
				int y = in.nextInt();
				double k = 1.0 * (y - y0) / (x - x0);
				for (int j = x0 + 1; j <= x; j++) {
					upper[j] = k * (j - x0) + y0;
				}
				x0 = x;
				y0 = y;
			}
			for (int i = 0; i <= w; i++) {
				len[i] = upper[i] - low[i];
			}
			double area = 0;
			for (int i = 1; i <= w; i++) {
				area += (len[i - 1] + len[i]) / 2;
			}
			area /= g;
			out.println("Case #" + (t + 1) + ":");
			double x = 0;
			for (int i = 1; i < g; i++) {
				double remain = area;
				x0 = (int) x;
				int x1 = x0 + 1;
				double k = 1.0 * (len[x1] - len[x0]);
				double y = k * (x - x0) + len[x0];
				while (remain > (y + len[x1]) * (x1 - x) / 2) {
					remain -= (y + len[x1]) * (x1 - x) / 2;
					x = x1;
					x0 = (int) x;
					x1 = x0 + 1;
					k = 1.0 * (len[x1] - len[x0]);
					y = k * (x - x0) + len[x0];
				}
				if (Math.abs(k) > 10e-6) {
					double a = k;
					double b = 2 * (y - k * x);
					double c = k * x * x - 2 * x * y - 2 * remain;
					double d = Math.sqrt(b * b - 4 * a * c);
					double x2 = (d - b) / (2 * a);
					double x3 = -1.0 * (d + b) / (2 * a);
					if (x2 > x && x2 <= x1) {
						out.println(format(x2));
						x = x2;
					} else {
						out.println(format(x3));
						x = x3;
					}
				} else {
					x += remain / y;
					out.println(format(x));
				}
			}
		}
		in.close();
		out.close();
	}
	
	static double format(double d) {
		return Math.round(d * 10e5) / 10e5;
	}
	
}

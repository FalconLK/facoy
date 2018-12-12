import java.io.*;
import java.util.*;

public class IrregularCakes implements Runnable {
	public static void main(String[] args) throws IOException {
		new Thread(new IrregularCakes()).start();
	}

	public BufferedReader br;

	public StringTokenizer in;

	public PrintWriter out;

	public String nextToken() throws IOException {
		while (in == null || !in.hasMoreTokens()) {
			in = new StringTokenizer(br.readLine());
		}

		return in.nextToken();
	}

	public int nextInt() throws IOException {
		return Integer.parseInt(nextToken());
	}

	public void solve() throws IOException {
		int W = nextInt();
		int L = nextInt();
		int U = nextInt();
		int G = nextInt();

		double[] up = new double[W + 1];
		double[] down = new double[W + 1];
		double[] angleUp = new double[W + 1];
		double[] angleDown = new double[W + 1];

		double[] x = new double[L];
		double[] y = new double[L];
		for (int i = 0; i < L; i++) {
			x[i] = nextInt();
			y[i] = nextInt();
		}

		down[0] = y[0];
		for (int i = 1; i < L; i++) {
			double Y = (y[i] - y[i - 1]) / (x[i] - x[i - 1]);
			double z = y[i - 1] + Y;
			for (int j = (int)x[i - 1] + 1; j <= x[i]; j++) {
				down[j] = z;
				z += Y;
				angleDown[j] = Y;
			}
		}

		x = new double[U];
		y = new double[U];
		for (int i = 0; i < U; i++) {
			x[i] = nextInt();
			y[i] = nextInt();
		}

		up[0] = y[0];
		for (int i = 1; i < U; i++) {
			double Y = (y[i] - y[i - 1]) / (x[i] - x[i - 1]);
			double z = y[i - 1] + Y;
			for (int j = (int)x[i - 1] + 1; j <= x[i]; j++) {
				up[j] = z;
				z += Y;
				angleUp[j] = Y;
			}
		}

		double S = 0;
		for (int i = 0; i < W; i++) {
			S += (up[i] + up[i + 1] - down[i] - down[i + 1]) / 2;
		}

		S /= G;

		double X = 0;
		for (int i = 0; i < G - 1; i++) {
			double D = S;
			while (true) {
				double upY = (X - (int) X) * angleUp[(int) X + 1] + up[(int) X];
				double downY = (X - (int) X) * angleDown[(int) X + 1]
						+ down[(int) X];

				double St = (upY - downY + up[(int) X + 1] - down[(int) X + 1])
						* ((int) X + 1 - X) / 2;

				if (St <= D) {
					D -= St;
					X = (int)X + 1;
				} else {
					break;
				}
			}

			double upY = (X - (int) X) * angleUp[(int) X + 1] + up[(int) X];
			double downY = (X - (int) X) * angleDown[(int) X + 1]
					+ down[(int) X];

			double l = X;
			double r = (int) X + 1;
			for (int j = 0; j < 100; j++) {
				double m = (l + r) / 2;

				double upY1 = (m - (int) m) * angleUp[(int) m + 1] + up[(int) m];
				double downY1 = (m - (int) m) * angleDown[(int) m + 1]
						+ down[(int) m];
				double St = (upY + upY1 - downY - downY1) * (m - X) / 2;
				if (St > D) {
					r = m;
				} else {
					l = m;
				}
			}

			X = l;
			out.println(X);
		}
	}

	public void run() {
		try {
			br = new BufferedReader(new FileReader("A-large.in"));
			out = new PrintWriter("a.out");

			int t = nextInt();
			for (int i = 0; i < t; i++) {
				out.println("Case #" + (i + 1) + ": ");
				solve();
			}

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}

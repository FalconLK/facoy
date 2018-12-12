import static java.lang.Math.min;
import static java.util.Arrays.deepToString;
import static java.util.Arrays.sort;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;

public class A {
	final static int C = 37;
	static int gtest;

	static double ans;

	static void relaxProfit(long[] init, long[] have) {
		int eq = 0;
		while (true) {
			if (eq + 1 < C && have[eq] == have[eq + 1]) {
				eq++;
			} else {
				break;
			}
		}
		double res = 0;
		for (int i = 0; i <= eq; i++) {
			res += (have[i] - init[i]) * 36;
		}
		res *= 1D / (eq + 1);
		for (int i = 0; i < have.length; i++) {
			res -= have[i] - init[i];
		}
		// if (gtest == 9)
		// debug(have, res);
		if (res >= ans) {

			ans = res;
		}
	}

	static void solve1(int test) {
		gtest = test;
		ans = 0;
		long B = nextLong();
		int n = nextInt();
		long[] left = new long[C];
		for (int i = 0; i < n; i++) {
			left[i] = nextLong();
		}
		Arrays.sort(left);
		long[] a = left.clone();
		relaxProfit(left, a);
		while (B > 0) {
			a[0]++;
			B--;
			sort(a);
			relaxProfit(left, a);
		}
		writer.printf("Case #%d: %.9f\n", test, ans);
	}

	static void solve2(int test) {
		gtest = test;
		ans = 0;
		long B = nextLong();
		int n = nextInt();
		long[] left = new long[C];
		for (int i = 0; i < n; i++) {
			left[i] = nextLong();
		}
		Arrays.sort(left);
		for (int c = 1; c < C; c++) {
			long[] a = left.clone();
			long tmpB = B;
			for (int i = 0; i < c; i++) {
				tmpB -= a[c - 1] - a[i];
				a[i] = a[c - 1];
			}
			for (int i = c; i < C; i++) {
				if (a[i] == a[0]) {
					a[i]++;
					tmpB--;
				}
			}
			if (tmpB < 0)
				continue;
			relaxProfit(left, a);
			while (true) {
				int cntX = c;
				int cntX1 = 0;
				for (int i = c; i < C; i++) {
					if (a[i] == a[0] + 1) {
						cntX1++;
					}
				}
				if (tmpB < cntX + cntX1 || cntX + cntX1 == C)
					break;
				long maxAdd = tmpB / (cntX + cntX1);
				long canAdd = a[cntX + cntX1] - (a[0] + 1);
				long add = min(maxAdd, canAdd);
				for (int i = 0; i < cntX + cntX1; i++) {
					a[i] += add;
					tmpB -= add;
				}
				relaxProfit(left, a);
			}
		}
		writer.printf("Case #%d: %.9f\n", test, ans);
	}

	public static void main(String[] args) throws Exception {
		reader = new BufferedReader(new FileReader("A-large.in"));
		writer = new PrintWriter("A-large.out");

		Locale.setDefault(Locale.US);

		setTime();
		int t = nextInt();
		for (int i = 0; i < t; i++) {
			solve2(i + 1);
			debug("done: " + i);
		}

		printTime();
		printMemory();

		writer.close();
	}

	static BufferedReader reader;
	static PrintWriter writer;
	static StringTokenizer tok = new StringTokenizer("");
	static long systemTime;

	static void debug(Object... o) {
		System.err.println(deepToString(o));
	}

	static void setTime() {
		systemTime = System.currentTimeMillis();
	}

	static void printTime() {
		System.err.println("Time consumed: " + (System.currentTimeMillis() - systemTime));
	}

	static void printMemory() {
		System.err.println("Memory consumed: "
				+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000 + "kb");
	}

	static String next() {
		while (!tok.hasMoreTokens()) {
			String w = null;
			try {
				w = reader.readLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (w == null)
				return null;
			tok = new StringTokenizer(w);
		}
		return tok.nextToken();
	}

	static int nextInt() {
		return Integer.parseInt(next());
	}

	static long nextLong() {
		return Long.parseLong(next());
	}

	static double nextDouble() {
		return Double.parseDouble(next());
	}

	static BigInteger nextBigInteger() {
		return new BigInteger(next());
	}
}
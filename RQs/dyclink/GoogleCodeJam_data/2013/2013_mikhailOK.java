import static java.lang.Math.max;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.StringTokenizer;

public class A {

	static void solveMultiTest() throws IOException {
		int testCases = nextInt();
		for (int testCase = 1; testCase <= testCases; testCase++) {
			out.print("Case #" + testCase + ": ");
			solveOneTest();
		}
	}

	static void solveOneTest() throws IOException {
		long budget = nextLong();
		int n = nextInt();

		long[] x = new long[37];
		for (int i = 0; i < n; i++) {
			x[i] = nextLong();
		}
		Arrays.sort(x);
		// System.err.println(Arrays.toString(x));

		double best = 0;

		for (int winningThings = 1; winningThings <= 37; ++winningThings) {

			for (long interestingBet : x) {
				for (long bet = interestingBet - 50; bet <= interestingBet + 50; bet++) {
					if (bet < 0) {
						continue;
					}
					double current = calcOurWinning(budget, x, winningThings,
							bet);
					best = max(best, current);
				}
			}

			long value = calcMaxBet(budget, x, winningThings);

			for (long bet = value - 1000; bet <= value + 1000; bet++) {
				double current = calcOurWinning(budget, x, winningThings, bet);
				best = max(best, current);
			}

		}

		out.println(best);
	}

	static double calcOurWinning(long budget, long[] x, int winningThings,
			long lowestBet) {
		long payMoney = 0;
		for (int i = 0; i < winningThings; i++) {
			if (x[i] > lowestBet) {
				return -1;
			}
			payMoney += -x[i] + lowestBet;
		}
		for (int i = winningThings; i < x.length; i++) {
			if (x[i] <= lowestBet) {
				payMoney += lowestBet + 1 - x[i];
			}
		}

		if (payMoney > budget) {
			return -1;
		}

		long ourWin = 0;
		for (int i = 0; i < winningThings; i++) {
			ourWin += lowestBet - x[i];
		}

		return (double) ourWin / (double) winningThings * 36.0 - payMoney;
	}

	private static long calcMaxBet(long budget, long[] x, int winningThings) {
		long low = x[winningThings - 1];

		long payMoney = 0;
		for (int i = 0; i < winningThings; i++) {
			payMoney += low - x[i];
		}
		if (payMoney > budget) {
			return -1;
		}

		--low;
		long high = budget + 1;
		while (high - low > 1) {
			long mid = low + high >> 1;
			if (canDo(budget, x, winningThings, mid)) {
				low = mid;
			} else {
				high = mid;
			}
		}
		if (low < x[winningThings - 1]) {
			return -1;
		} else {
			return low;
		}
	}

	private static boolean canDo(long budget, long[] x, int winningThings,
			long lowestBet) {
		long payMoney = 0;
		for (int i = 0; i < x.length; i++) {
			if (x[i] < lowestBet) {
				payMoney += -x[i] + lowestBet;
			}
		}

		return payMoney <= budget;
	}

	static BufferedReader br;
	static StringTokenizer st;
	static PrintWriter out;

	public static void main(String[] args) throws IOException {
		InputStream input = System.in;
		PrintStream output = System.out;
		File file = new File("a.in");
		if (file.exists() && file.canRead()) {
			input = new FileInputStream(file);
			output = new PrintStream(new File("a.out"));
		}
		br = new BufferedReader(new InputStreamReader(input));
		out = new PrintWriter(output);
		solveMultiTest();
		out.close();
	}

	static long nextLong() throws IOException {
		return Long.parseLong(nextToken());
	}

	static double nextDouble() throws IOException {
		return Double.parseDouble(nextToken());
	}

	static int nextInt() throws IOException {
		return Integer.parseInt(nextToken());
	}

	static String nextToken() throws IOException {
		while (st == null || !st.hasMoreTokens()) {
			String line = br.readLine();
			if (line == null) {
				return null;
			}
			st = new StringTokenizer(line);
		}
		return st.nextToken();
	}
}

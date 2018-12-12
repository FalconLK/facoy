import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;

public class Solution implements Runnable {
	
	private static final long MAXBET = 1000000000000L;
	
	private BufferedReader in;
	private StringTokenizer st;
	private PrintWriter out;
	
	private void solve() throws IOException {
		int t = nextInt();
		for (int test = 1; test <= t; test++) {
			long b = nextLong();
			int n = nextInt();
			long[] x = new long[n];
			for (int i = 0; i < n; i++) {
				x[i] = nextLong();
			}
			double answer = solve(b, x);
			out.println("Case #" + test + ": " + answer);
		}
	}
	
	private double solve(long b, long[] x) {
		double result = 0;
		long[] a = new long[37];
		for (int i = 0; i < x.length; i++) {
			a[i] = x[i];
		}
		Arrays.sort(a);
		for (int count = 1; count <= 37; count++) {
			long maxBet = maxBet(a, count, b);
			if (maxBet == -1) {
				continue;
			}
			long minBet = a[count - 1];
			result = Math.max(result, calcWin(a, count, maxBet));
			for (int i = count; i < a.length; i++) {
				long bet = a[i] - 1;
				if (bet >= minBet && bet <= maxBet) {
					result = Math.max(result, calcWin(a, count, bet));
				}
			}
		}
		return result;
	}
	
	private double calcWin(long[] a, int count, long bet) {
		double win = 0;
		for (int i = 0; i < count; i++) {
			win -= (bet - a[i]);
			win += (1.0 / count) * 36 * (bet - a[i]);
		}
		for (int i = count; i < a.length; i++) {
			if (a[i] <= bet) {
				win -= (bet + 1 - a[i]);
			}
		}
		return win;
	}

	private long cost(long[] a, int count, long bet) {
		long result = 0;
		for (int i = 0; i < count; i++) {
			result += (bet - a[i]);
		}
		for (int i = count; i < a.length; i++) {
			if (a[i] <= bet) {
				result += (bet + 1 - a[i]);
			}
		}
		return result;
	}

	private long maxBet(long[] a, int count, long b) {
		long left = a[count - 1] - 1;
		long right = MAXBET + b / 37 + 1;
		while (left + 1 < right) {
			long mid = (left + right) / 2;
			if (cost(a, count, mid) <= b) {
				left = mid;
			} else {
				right = mid;
			}
		}
		if (left == a[count - 1] - 1) {
			return -1;
		} else {
			return left;
		}
	}

	@Override
	public void run() {
		try {
			solve();
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			out.close();
		}
	}
	
	private long nextLong() throws IOException {
		return Long.parseLong(next());
	}
	
	private int nextInt() throws IOException {
		return Integer.parseInt(next());
	}
	
	private String next() throws IOException {
		while (!st.hasMoreTokens()) {
			String line = in.readLine();
			if (line == null) {
				return null;
			}
			st = new StringTokenizer(line);
		}
		return st.nextToken();
	}
	
	public Solution(String filename) {
		try {
			in = new BufferedReader(new FileReader(filename + ".in"));
			st = new StringTokenizer("");
			out = new PrintWriter(new FileWriter(filename + ".out"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		new Solution("data").run();
	}
}

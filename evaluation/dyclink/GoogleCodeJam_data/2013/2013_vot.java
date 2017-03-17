import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

public class A {

	int n;
	long b;
	long[] x;

	private void solve() throws Exception {
		b = nextLong();
		n = nextInt();
		x = new long[37];
		for (int i = 0; i < n; ++i) {
			x[i] = nextLong();
		}
		Arrays.sort(x);
		double best = 0;
		long sum = 0;
		for (int count = 1; count < x.length; ++count) {
			best = Math.max(best, getExp(count, x[count]));
			best = Math.max(best, getExp(count, x[count] - 1));
			sum += x[count - 1];
			long toGet = x[count - 1] * count - sum;
			if (toGet <= b) {
				best = Math.max(best, getExp(count, x[count - 1] + (b - toGet) / count));
			}
		}
		//out.println(best);
		for (int count = 1; count < x.length; ++count) {
			for (int count2 = 1; count + count2 < x.length; ++count2) {
				long to = x[count + count2 - 1];
				long sum1 = 0, sum2 = 0;
				for (int i = 0; i < count + count2; ++i) {
					if (i < count) {
						sum1 += x[i];
					} else {
						sum2 += x[i];
					}
				}
				long toGet = (to - 1) * count - sum1;
				toGet += to * count2 - sum2;
				if (toGet > b) {
					continue;
				}
				best = Math.max(best, getExp2(count, count2, to));
				best = Math.max(best, getExp2(count, count2, x[count + count2]));
				best = Math.max(best, getExp2(count, count2, to + (b - toGet) / (count + count2)));
			}
		}
		out.printf("%.12f\n", best);
	}

	private double getExp2(int count, int count2, long val) {
		for (int i = 0; i < count; ++i) {
			if (x[i] > val - 1) {
				return 0;
			}
		}
		long bet = 0;
		for (int i = 0; i < count; ++i) {
			bet += val - 1 - x[i];
		}
		long o = 0;
		for (int i = 0; i < count2; ++i) {
			o += val - x[count + i];
		}
		for (int i = count + count2; i < x.length; ++i) {
			if (x[i] < val) {
				return 0;
			}
		}
		if (bet + o > b) {
			return 0;
		}
		return (double) bet * 36 / count - bet - o;
	}

	private double getExp(int count, long val) {
		long bet = 0;
		for (int i = 0; i < count; ++i) {
			if (x[i] > val) {
				return 0;
			}
			bet += val - x[i];
		}
		int cnt = count;
		for (int i = count; i < x.length; ++i) {
			if (x[i] < val) {
				return 0;
			}
			if (x[i] == val) {
				++count;
			}
		}
		if (bet > b) {
			return 0;
		}
		return (double) bet * 36 / count - bet;
	}

	public void run() {
		try {
			int tc = nextInt();
			for (int it = 1; it <= tc; ++it) {
				System.err.println(it);
				out.print("Case #" + it + ": ");
				solve();
			}
		} catch (Exception e) {
			NOO(e);
		} finally {
			out.close();
		}
	}

	PrintWriter out;
	BufferedReader in;
	StringTokenizer St;

	void NOO(Exception e) {
		e.printStackTrace();
		System.exit(42);
	}

	int nextInt() {
		return Integer.parseInt(nextToken());
	}

	long nextLong() {
		return Long.parseLong(nextToken());
	}

	double nextDouble() {
		return Double.parseDouble(nextToken());
	}

	String nextToken() {
		while (!St.hasMoreTokens()) {
			try {
				String line = in.readLine();
				if (line == null)
					return null;
				St = new StringTokenizer(line);
			} catch (Exception e) {
				NOO(e);
			}
		}
		return St.nextToken();
	}

	private A(String name) {
		try {
			in = new BufferedReader(new FileReader(name + ".in"));
			St = new StringTokenizer("");
			out = new PrintWriter(new FileWriter(name + ".out"));
		} catch (Exception e) {
			NOO(e);
		}
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		long start = System.currentTimeMillis();
		new A("A").run();
		System.err.println("Done in " + (System.currentTimeMillis() - start) + "ms");
	}
}

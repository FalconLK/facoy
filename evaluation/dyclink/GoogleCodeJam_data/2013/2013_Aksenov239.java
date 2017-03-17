import java.io.*;
import java.util.*;

public class A implements Runnable {
	public static void main(String[] args) throws IOException {
		new Thread(new A()).start();
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

	public long nextLong() throws IOException {
		return Long.parseLong(nextToken());
	}

	long[] a;
	long b;

	public double profit(int equal, long f, long t) {
		long l = f - 1;
		long r = t;
		while (l < r - 1) {
			long to = (l + r) / 2;
			long get = 0;
			for (int i = 0; i < equal; i++) {
				get += to - a[i];
			}
			for (int i = equal; i < a.length; i++) {
				if (a[i] <= to) {
					get += to + 1 - a[i];
				}
			}
			if (get > b)
				r = to;
			else
				l = to;
		}

		if (l == f - 1)
			return 0;

		double profit = 0;
		long get = 0;
		for (int i = 0; i < equal; i++) {
			get += l - a[i];
			profit += 36. * (l - a[i]) / equal;
		}
		for (int i = equal; i < a.length; i++) {
			if (a[i] <= l) {
				get += l + 1 - a[i];
			}
		}

		return -get + profit;
	}

	public void solve() throws IOException {
		b = nextLong();
		long n = nextInt();

		a = new long[37];
		for (int i = 0; i < n; i++) {
			a[i] = nextLong();
		}

		double ans = 0;

		Arrays.sort(a);
		for (int equal = 1; equal <= a.length; equal++) {
			for (int to_get = equal; to_get < a.length; to_get++) {
				ans = Math.max(ans, profit(equal, a[to_get - 1], a[to_get]));
			}
		}

		out.println(ans);
	}

	public void run() {
		try {
			br = new BufferedReader(new FileReader("input.txt"));
			out = new PrintWriter("output.txt");

			int t = nextInt();
			for (int i = 0; i < t; i++) {
				out.print("Case #" + (i + 1) + ": ");
				solve();
			}

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}

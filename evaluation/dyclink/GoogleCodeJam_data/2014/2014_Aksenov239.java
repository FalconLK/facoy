import java.io.*;
import java.util.*;

public class A {
	public static void main(String[] args) {
		new A().run();
	}

	BufferedReader br;
	StringTokenizer in;
	PrintWriter out;

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
		int n = nextInt();
		int p = nextInt();
		int q = nextInt();
		int r = nextInt();
		int s = nextInt();

		long[] a = new long[n];
		long sum = 0;
		for (int i = 0; i < a.length; i++) {
			a[i] = (1L * i * p + q) % r + s;
			sum += a[i];
		}

		TreeSet<Long> all = new TreeSet<Long>();
		all.add(0L);
		long x = 0;
		long best = Long.MAX_VALUE;
		for (int i = 0; i < n; i++) {
			x += a[i];
			Long up = all.ceiling(x / 2 + 1);
			if (up != null) {
				long now = Math.max(up, Math.max(x - up, sum - x));
				best = Math.min(best, now);
			}
			Long down = all.floor(x / 2);
			if (down != null) {
				long now = Math.max(down, Math.max(x - down, sum - x));
				best = Math.min(best, now);
			}

			all.add(x);
		}

		out.println(1. * (sum - best) / sum);
	}

	public void run() {
		try {
			br = new BufferedReader(new FileReader("input.txt"));
			out = new PrintWriter("output.txt");

			int t = nextInt();
			for (int i = 0; i < t; i++) {
				out.print(String.format("Case #%d: ", i + 1));
				solve();
			}

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

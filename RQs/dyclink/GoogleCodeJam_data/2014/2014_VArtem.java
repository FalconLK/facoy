import java.io.*;
import java.util.*;

public class MMTour {

	FastScanner in;
	PrintWriter out;

	void solve() {
		int n = in.nextInt();
		long p = in.nextInt(), q = in.nextInt(), r = in.nextInt(), s = in.nextInt();
		long[] ar = new long[n];

		for (int i = 0; i < n; i++) {
			ar[i] = (i * p + q) % r + s;
		}

		long[] pref = new long[n];
		for (int i = 0; i < n; i++) {
			pref[i] = ar[i];
			if (i > 0) {
				pref[i] += pref[i - 1];
			}
		}

		long max = Long.MAX_VALUE;

		for (int rght = 0; rght < n; rght++) {
			long rs = pref[n - 1] - pref[rght];
			
			
			long sum = pref[rght];
			
			int left = -1, right = rght;
			while (left < right - 1) {
				int mid = (left + right) >> 1;
				if (pref[mid] * 2 >= sum) {
					right = mid;
				} else {
					left = mid;
				}
			}
			
			
			for (int t = right - 2; t <= right + 2; t++) {
				if (0 <= t && t <= rght) {
					long ans = rs;
					ans = Math.max(ans, Math.max(sum - pref[t], pref[t]));
					max = Math.min(max, ans);
				}
			}
			
			
		}

		out.println(1.0 * (pref[n - 1] - max) / pref[n - 1]);
	}

	void run() {
		try {
			in = new FastScanner("input.txt");
			out = new PrintWriter("output.txt");
			int T = in.nextInt();
			for (int i = 1; i <= T; i++) {
				long time = System.currentTimeMillis();
				out.printf("Case #%d: ", i);
				solve();
				System.err.println("Test #" + i + " done in " + (System.currentTimeMillis() - time)
						+ " ms");
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class FastScanner {
		BufferedReader br;
		StringTokenizer st;

		public FastScanner(String s) {
			try {
				br = new BufferedReader(new FileReader(s));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String nextToken() {
			while (st == null || !st.hasMoreElements()) {
				try {
					st = new StringTokenizer(br.readLine());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return st.nextToken();
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
	}

	public static void main(String[] args) {
		new MMTour().run();
	}
}

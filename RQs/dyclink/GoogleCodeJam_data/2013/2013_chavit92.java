import java.io.*;
import java.util.*;

public class A {

	static final int places = 37;
	static final int winc = 36;

	double count(long[] a, long up, long sum) {
		if (up < a[0]) {
			return 0;
		}
		double win = 0;
		double must = 0;
		for (int i = 0; i < places; i++) {
			if (a[i] < up) {
				must += up - a[i];
			}
		}
		sum -= must;
		for (int i = places; i > 0; i--) {
			double newmust = 0;
			for (int j = i; j < places; j++) {
				if (a[j] <= up) {
					newmust++;
				}
			}
			if (newmust > sum) {
				break;
			}
			double curwin = 0;
			for (int j = 0; j < i; j++) {
				if (a[j] <= up) {
					curwin = curwin + (up - a[j]) * winc * 1.0 / i;
				}
			}
			win = Math.max(win, curwin - must - newmust);
		}
		return win;

	}

	double solve(long[] a, long l, long r, long sum) {
		if (r < l) {
			return 0;
		}
		double win = 0;
		for (int i = 0; i < 1000; i++) {
			win = Math.max(count(a, l + i, sum), win);
			win = Math.max(count(a, r - i, sum), win);
		}
		
		while (l < r - 1) {
			long mid = (l + r) / 2;
			if (count(a, mid, sum) == 0) {
				r = mid;
			} else {
				l = mid;
			}
		}
		for (int i = 0; i < 1000; i++) {
			win = Math.max(count(a, l + i, sum), win);
			win = Math.max(count(a, r - i, sum), win);
		}
		return win;
	}

	void solve() throws IOException {
		long sum = nextLong();
		double win = 0;
		int n = nextInt();
		long[] a = new long[places];
		for (int i = 0; i < n; i++) {
			a[i] = nextLong();
		}
		Arrays.sort(a);
		for (int i = 0; i < places - 1; i++) {
			win = Math.max(win, solve(a, a[i], a[i + 1] - 1, sum));
		}
		win = Math.max(solve(a, a[places - 1], sum + a[places - 1], sum), win);
		out.println(win);
	}

	public void run() throws IOException {
		br = new BufferedReader(new FileReader("A.in"));
		out = new PrintWriter("A.out");
		int n = nextInt();
		for (int i = 0; i < n; i++) {
			System.err.println(i);
			out.print("Case #" + (i + 1) + ": ");
			solve();
		}
		br.close();
		out.close();
	}

	public static void main(String[] args) throws IOException {
		new A().run();
	}

	BufferedReader br;
	StringTokenizer str;
	PrintWriter out;

	String next() throws IOException {
		while (str == null || !str.hasMoreTokens()) {
			String s = br.readLine();
			if (s != null) {
				str = new StringTokenizer(s);
			} else {
				return null;
			}
		}
		return str.nextToken();
	}

	long nextLong() throws IOException {
		return Long.parseLong(next());
	}

	int nextInt() throws IOException {
		return Integer.parseInt(next());
	}

	double nextDouble() throws IOException {
		return Double.parseDouble(next());
	}

}

import java.io.*;
import java.util.*;

public class A {
	public static void main(String[] args) {
		new A().run();
	}

	BufferedReader br;
	StringTokenizer st;
	PrintWriter out;
	boolean eof = false;
	Random rand = new Random(12345);

	private void run() {
		Locale.setDefault(Locale.US);
		try {
			br = new BufferedReader(new FileReader(FNAME + ".in"));
			out = new PrintWriter(FNAME + ".out");
			solve();
			out.close();
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(566);
		}
	}

	String nextToken() {
		while (st == null || !st.hasMoreTokens()) {
			try {
				st = new StringTokenizer(br.readLine());
			} catch (Exception e) {
				eof = true;
				return "0";
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

	String FNAME = "a";

	private void solve() throws IOException {
		int tests = nextInt();
		for (int test = 1; test <= tests; test++) {
			out.print("Case #" + test + ":");
			int n = nextInt();
			int[] len = new int[n];
			for (int i = 0; i < len.length; i++) {
				len[i] = nextInt();
			}
			Item[] a = new Item[n];
			for (int i = 0; i < a.length; i++) {
				a[i] = new Item(i, len[i], nextInt());
			}
			Arrays.sort(a);
			for (int i = 0; i < a.length; i++) {
				out.print(" " + a[i].num);
			}
			out.println();
		}
	}

	class Item implements Comparable<Item> {
		int p, num, l;

		public Item(int n, int l, int p) {
			num = n;
			this.l = l;
			this.p = p;
		}

		@Override
		public int compareTo(Item o) {
			if (o.p * l - p * o.l == 0) {
				return num - o.num;
			}
			return o.p * l - p * o.l;
		}
	}
}

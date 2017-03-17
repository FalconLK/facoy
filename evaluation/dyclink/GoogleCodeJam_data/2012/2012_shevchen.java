import java.io.*;
import java.util.*;

public class A implements Runnable {
	private MyScanner in;
	private PrintWriter out;

	private void solve() {
		int n = in.nextInt();
		Level[] lev = new Level[n];
		for (int i = 0; i < n; ++i) {
			lev[i] = new Level(i, -1, -1);
		}
		for (int i = 0; i < n; ++i) {
			lev[i].time = in.nextInt();
		}
		for (int i = 0; i < n; ++i) {
			lev[i].prob = 100 - in.nextInt();
		}
		Arrays.sort(lev);
		for (Level l : lev) {
			out.print(l.id + " ");
		}
		out.println();
	}

	@Override
	public void run() {
		in = new MyScanner();
		try {
			out = new PrintWriter(new File("A-large.out"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int tests = in.nextInt();
		for (int i = 0; i < tests; ++i) {
			out.print("Case #" + (i + 1) + ": ");
			solve();
		}
		in.close();
		out.close();
	}

	public static void main(String[] args) {
		new A().run();
	}

	class Level implements Comparable<Level> {
		public int id, time, prob;

		public Level(int id, int time, int prob) {
			this.id = id;
			this.time = time;
			this.prob = prob;
		}

		@Override
		public int compareTo(Level o) {
			int one = 100 * time + prob * o.time;
			int two = 100 * o.time + o.prob * time;
			if (one != two) {
				return one - two;
			}
			return id - o.id;
		}
	}

	class MyScanner {
		private BufferedReader br;
		private StringTokenizer st;

		public MyScanner() {
			try {
				br = new BufferedReader(new FileReader("A-large.in"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		public void close() {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public boolean hasNext() {
			while (st == null || !st.hasMoreTokens()) {
				try {
					String s = br.readLine();
					if (s == null) {
						return false;
					}
					st = new StringTokenizer(s);
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			return st != null && st.hasMoreTokens();
		}

		private String next() {
			while (st == null || !st.hasMoreTokens()) {
				try {
					String s = br.readLine();
					if (s == null) {
						return null;
					}
					st = new StringTokenizer(s);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			return st.nextToken();
		}

		public String nextLine() {
			try {
				st = null;
				return br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		public int nextInt() {
			return Integer.parseInt(next());
		}

		public long nextLong() {
			return Long.parseLong(next());
		}

		public double nextDouble() {
			return Double.parseDouble(next());
		}
	}
}
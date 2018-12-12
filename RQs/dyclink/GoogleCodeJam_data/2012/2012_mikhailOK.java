import static java.util.Arrays.sort;

import java.io.*;
import java.util.*;

public class A {

	private static void solve() throws IOException {
		int testCases = nextInt();
		for (int test = 1; test <= testCases; test++) {
			out.print("Case #" + test + ":");
			solveOneTest();
		}
	}

	static class Level implements Comparable<Level> {
		int time;
		int prob;
		int id;

		private Level(int time, int prob, int id) {
			this.time = time;
			this.prob = prob;
			this.id = id;
		}

		@Override
		public int compareTo(Level o) {
			int cmp = time * o.prob - o.time * prob;
			return cmp;
		}

	}

	private static void solveOneTest() throws IOException {
		int n = nextInt();
		int[] time = new int[n];
		for (int i = 0; i < n; i++) {
			time[i] = nextInt();
		}
		int[] prob = new int[n];
		for (int i = 0; i < n; i++) {
			prob[i] = nextInt();
		}
		Level[] levels = new Level[n];
		for (int i = 0; i < n; i++) {
			levels[i] = new Level(time[i], prob[i], i);
		}
		// boolean zero = false;
		// for (int i = 0; i < n; i++) {
		// if (prob[i] == 100) {
		// zero = true;
		// }
		// }
		// if (zero) {
		// for (int i = 0; i < n; i++) {
		// out.print(" " + i);
		// }
		// out.println();
		// return;
		// }
		sort(levels);
		for (Level l : levels) {
			out.print(" " + l.id);
		}
		out.println();
	}

	public static void main(String[] args) {
		try {
			br = new BufferedReader(new FileReader("A.in"));
			out = new PrintWriter("A.out");
			solve();
			out.close();
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(239);
		}
	}

	static BufferedReader br;
	static StringTokenizer st;
	static PrintWriter out;

	static String nextToken() throws IOException {
		while (st == null || !st.hasMoreTokens()) {
			String line = br.readLine();
			if (line == null)
				return null;
			st = new StringTokenizer(line);
		}
		return st.nextToken();
	}

	static int nextInt() throws IOException {
		return Integer.parseInt(nextToken());
	}

	static long nextLong() throws IOException {
		return Long.parseLong(nextToken());
	}

	static double nextDouble() throws IOException {
		return Double.parseDouble(nextToken());
	}
}
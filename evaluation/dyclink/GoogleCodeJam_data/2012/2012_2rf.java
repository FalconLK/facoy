import java.io.*;
import java.util.*;

public class A {

	BufferedReader br;
	PrintWriter out;
	StringTokenizer st;
	boolean eof;

	enum InputType {
		SAMPLE, SMALL, LARGE;
	}

	static final InputType currentInputType = InputType.LARGE;
	static final int attemptNumber = 0; // for small inputs only

	class Level implements Comparable<Level> {
		int ind;
		int time;
		int prob;

		public Level(int ind, int time, int prob) {
			this.ind = ind;
			this.time = time;
			this.prob = prob;
		}

		@Override
		public int compareTo(Level o) {
			int val1 = time * o.prob;
			int val2 = o.time * prob;
			if (val1 != val2)
				return Integer.compare(val1, val2);
			return Integer.compare(ind, o.ind);
		}

	}

	void solve() throws IOException {
		int n = nextInt();
		Level[] a = new Level[n];

		int[] len = new int[n];
		int[] p = new int[n];

		for (int i = 0; i < n; i++)
			len[i] = nextInt();

		for (int i = 0; i < n; i++)
			p[i] = nextInt();

		for (int i = 0; i < n; i++)
			a[i] = new Level(i, len[i], p[i]);

		Arrays.sort(a);
		for (Level lev : a)
			out.print(lev.ind + " ");
		out.println();
	}

	void inp() throws IOException {
		switch (currentInputType) {
		case SAMPLE:
			br = new BufferedReader(new InputStreamReader(System.in));
			out = new PrintWriter(System.out);
			break;
		case SMALL:
			String fileName = "A-small-attempt" + attemptNumber;
			br = new BufferedReader(new FileReader(fileName + ".in"));
			out = new PrintWriter(fileName + ".out");
			break;
		case LARGE:
			fileName = "A-large";
			br = new BufferedReader(new FileReader(fileName + ".in"));
			out = new PrintWriter(fileName + ".out");
			break;
		}
		int test = nextInt();
		for (int i = 1; i <= test; i++) {
			System.err.println("Running test " + i);
			out.print("Case #" + i + ": ");
			solve();
		}
		out.close();
	}

	public static void main(String[] args) throws IOException {
		new A().inp();
	}

	String nextToken() {
		while (st == null || !st.hasMoreTokens()) {
			try {
				st = new StringTokenizer(br.readLine());
			} catch (Exception e) {
				eof = true;
				return null;
			}
		}
		return st.nextToken();
	}

	String nextString() {
		try {
			return br.readLine();
		} catch (Exception e) {
			eof = true;
			return null;
		}
	}

	int nextInt() throws IOException {
		return Integer.parseInt(nextToken());
	}

	long nextLong() throws IOException {
		return Long.parseLong(nextToken());
	}

	double nextDouble() throws IOException {
		return Double.parseDouble(nextToken());
	}
}

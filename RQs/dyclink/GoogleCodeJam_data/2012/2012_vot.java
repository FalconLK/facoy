import java.util.*;
import java.math.*;
import java.io.*;

public class A {
	
	int n;

	private void solve() throws Exception {
		n = nextInt();
		final int[] prob = new int[n];
		final int[] len = new int[n];
		for (int i = 0; i < n; ++i) {
			len[i] = nextInt();
		}
		for (int i = 0; i < n; ++i) {
			prob[i] = nextInt();
		}
		Integer[] indexes = new Integer[n];
		for (int i = 0; i < n; ++i) {
			indexes[i] = i;
		}
		Arrays.sort(indexes, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				int val1 = prob[o1] * len[o2];
				int val2 = prob[o2] * len[o1];
				if (val1 != val2) {
					return val2 - val1;
				} else {
					return o1 - o2;
				}
			}
		});
		for (int i : indexes) {
			out.print(i + " ");
		}
		out.println();
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
			in = new BufferedReader(new FileReader("input.txt"));
			St = new StringTokenizer("");
			out = new PrintWriter(new FileWriter("output.txt"));
		} catch (Exception e) {
			NOO(e);
		}
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		new A("a").run();
	}
}

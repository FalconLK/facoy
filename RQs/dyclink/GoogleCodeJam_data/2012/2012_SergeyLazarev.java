package round3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.StringTokenizer;

public class A {
	private static final String TASKNAME = "input";
	
	private void solve() throws IOException {
		int tests = nextInt();
		for (int t = 1; t <= tests; t++) {
			int n = nextInt();
			int[] len = new int[n];
			int[] p = new int[n];
			int[] ind = new int[n];
			
			for (int i = 0; i < n; i++) {
				len[i] = nextInt();
				ind[i] = i;
			}
			for (int i = 0; i < n; i++) {
				p[i] = nextInt();
			}
			
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					if (p[i] * len[j] * (100 - p[j]) < p[j] * len[i] * (100 - p[i]) || 
							p[i] * len[j] * (100 - p[j]) == p[j] * len[i] * (100 - p[i]) &&
							ind[i] > ind[j]) {
						int x = p[i]; p[i] = p[j]; p[j] = x;
						x = len[i]; len[i] = len[j]; len[j] = x;
						x = ind[i]; ind[i] = ind[j]; ind[j] = x;
					}
				}
			}
			
			print("Case #" + t + ":");
			for (int i = 0; i < n; i++) {
				print(" " + ind[i]);
			}
			println("");
		}
	}

	private String nextToken() throws IOException {
		while (tokenizer == null || !tokenizer.hasMoreTokens()) {
			tokenizer = new StringTokenizer(reader.readLine());
		}
		return tokenizer.nextToken();
	}

	private int nextInt() throws NumberFormatException, IOException {
		return Integer.parseInt(nextToken());
	}

	private double nextDouble() throws NumberFormatException, IOException {
		return Double.parseDouble(nextToken());
	}

	private long nextLong() throws IOException {
		return Long.parseLong(nextToken());
	}

	private void print(Object o) {
		writer.print(o);
	}

	private void println(Object o) {
		writer.println(o);
	}

	private void printf(String format, Object... o) {
		writer.printf(format, o);
	}

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		Locale.setDefault(Locale.US);
		new A().run();
		System.err.printf("%.3f\n", 1e-3 * (System.currentTimeMillis() - time));
	}

	BufferedReader reader;
	StringTokenizer tokenizer;
	PrintWriter writer;

	private void run() {
		try {
			reader = new BufferedReader(new InputStreamReader(System.in));
			writer = new PrintWriter(System.out);
			reader = new BufferedReader(new FileReader(TASKNAME + ".in"));
			writer = new PrintWriter(TASKNAME + ".out");
			solve();
			reader.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(13);
		}
	}
}
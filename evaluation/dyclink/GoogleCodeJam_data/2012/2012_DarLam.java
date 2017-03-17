import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.StringTokenizer;


public class Solution implements Runnable {
	
	private BufferedReader in;
	private StringTokenizer st;
	private PrintWriter out;
	
	private void solve() throws IOException {
		int t = nextInt();
		for (int test = 1; test <= t; test++) {
			int n = nextInt();
			int[] len = new int[n];
			for (int i = 0; i < n; i++) {
				len[i] = nextInt();
			}
			int[] p = new int[n];
			for (int i = 0; i < n; i++) {
				p[i] = nextInt();
			}
			int[] answer = solve(n, len, p);
			out.print("Case #" + test + ":");
			for (int i = 0; i < n; i++) {
				out.print(" " + answer[i]);
			}
			out.println();
		}
	}
	
	private int[] solve(int n, int[] len, int[] p) {
		int[][] a = new int[n][2];
		for (int i = 0; i < n; i++) {
			a[i][0] = p[i];
			a[i][1] = i;
		}
		Arrays.sort(a, new Comparator<int[]>() {
			@Override
			public int compare(int[] arg0, int[] arg1) {
				if (arg0[0] > arg1[0]) {
					return -1;
				}
				if (arg0[0] < arg1[0]) {
					return 1;
				}
				if (arg0[1] < arg1[1]) {
					return -1;
				}
				if (arg0[1] > arg1[1]) {
					return 1;
				}
				return 0;
			}
		});
		int[] answer = new int[n];
		for (int i = 0; i < n; i++) {
			answer[i] = a[i][1];
		}
		return answer;
	}

	@Override
	public void run() {
		try {
			solve();
		} catch (Throwable e) {
			apstenu(e);
		} finally {
			out.close();
		}
	}
	
	private int nextInt() throws IOException {
		return Integer.parseInt(next());
	}
	
	private String next() throws IOException {
		while (!st.hasMoreTokens()) {
			String line = in.readLine();
			if (line == null) {
				return null;
			}
			st = new StringTokenizer(line);
		}
		return st.nextToken();
	}
	
	private void apstenu(Throwable e) {
		e.printStackTrace();
		System.exit(1);
	}
	
	public Solution(String filename) {
		try {
			in = new BufferedReader(new FileReader(filename + ".in"));
			st = new StringTokenizer("");
			out = new PrintWriter(new FileWriter(filename + ".out"));
		} catch (IOException e) {
			apstenu(e);
		}
	}
	
	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		new Solution("data").run();
	}
	
}

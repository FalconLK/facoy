import java.io.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;

public class A {
	BufferedReader in;
	StringTokenizer str;
	PrintWriter out;
	String SK;

	String next() throws IOException {
		while ((str == null) || (!str.hasMoreTokens())) {
			SK = in.readLine();
			if (SK == null)
				return null;
			str = new StringTokenizer(SK);
		}
		return str.nextToken();
	}

	int nextInt() throws IOException {
		return Integer.parseInt(next());
	}

	double nextDouble() throws IOException {
		return Double.parseDouble(next());
	}

	long nextLong() throws IOException {
		return Long.parseLong(next());
	}

	class Sob implements Comparable<Sob> {
		int t;
		int p;
		int num;

		public int compareTo(Sob o) {
			return o.p * t - p * o.t;
		}
	}

	void solve() throws IOException {
		int n = nextInt();
		Sob[] a = new Sob[n];
		for (int i = 0; i < n; i++) {
			a[i] = new Sob();
			a[i].num = i;
		}
		for (int i = 0; i < n; i++) {
			a[i].t = nextInt();
		}
		for (int i = 0; i < n; i++) {
			a[i].p = nextInt();
		}
		Arrays.sort(a);
		for (int i = 0; i < n; i++) {
			out.print(" " + a[i].num);
		}
		out.println();
	}

	void run() throws IOException {
		in = new BufferedReader(new FileReader("input.txt"));
		out = new PrintWriter("output.txt");
		int t = nextInt();
		for (int i = 0; i < t; i++) {
			out.print("Case #" + (i + 1) + ":");
			solve();
		}
		out.close();
	}

	public static void main(String[] args) throws IOException {
		new A().run();
	}

}
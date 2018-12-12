import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Locale;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.File;
import java.util.StringTokenizer;
import java.io.FilenameFilter;
import java.io.InputStream;

/**
 * Built using CHelper plug-in
 * Actual solution is at the top
 */
public class Main {
	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		InputStream inputStream;
		try {
			final String regex = "A-(small|large).*[.]in";
			File directory = new File(".");
			File[] candidates = directory.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.matches(regex);
				}
			});
			File toRun = null;
			for (File candidate : candidates) {
				if (toRun == null || candidate.lastModified() > toRun.lastModified())
					toRun = candidate;
			}
			inputStream = new FileInputStream(toRun);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream("a.out");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		InputReader in = new InputReader(inputStream);
		PrintWriter out = new PrintWriter(outputStream);
		TaskA solver = new TaskA();
		int testCount = Integer.parseInt(in.next());
		for (int i = 1; i <= testCount; i++)
			solver.solve(i, in, out);
		out.close();
	}
}

class TaskA {
	public void solve(int testNumber, InputReader in, PrintWriter out) {
		int n = in.nextInt();
		int p = in.nextInt();
		int q = in.nextInt();
		int r = in.nextInt();
		int s = in.nextInt();

		int[] t = new int[n];
		long[] sum = new long[n + 1];
		for (int i = 0; i < n; i++) {
			t[i] = (int) ((i * (long) p + q) % r + s);
			sum[i + 1] = sum[i] + t[i];
		}

		double res = 0;

		for (int a = 0, b = 0; a < n; a++) {
			while (sum[b + 1] - sum[a] < sum[n] - sum[b + 1] && b < n) {
				b++;
			}

			for (int i = 0; i < 2; i++) {
				if (b - i < a) break;
				long s1 = sum[b - i + 1] - sum[a];
				long s2 = sum[a];
				long s3 = sum[n] - sum[b - i + 1];
				long smax = Math.max(s1, Math.max(s2, s3));
				res = Math.max(res, 1 - (double) smax / sum[n]);
			}
		}

		out.printf("Case #" + testNumber + ": %.10f\n", res);
	}
}

class InputReader {
	public BufferedReader reader;
	public StringTokenizer tokenizer;

	public InputReader(InputStream stream) {
		reader = new BufferedReader(new InputStreamReader(stream));
	}

	public String next() {
		while (tokenizer == null || !tokenizer.hasMoreTokens()) {
			try {
				tokenizer = new StringTokenizer(reader.readLine());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return tokenizer.nextToken();
	}

	public int nextInt() {
		return Integer.parseInt(next());
	}
}


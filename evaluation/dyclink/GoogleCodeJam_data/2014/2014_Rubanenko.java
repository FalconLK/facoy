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
 * @author Rubanenko
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
		solver.solve(1, in, out);
		out.close();
	}
}

class TaskA {
    public void solve(int testNumber, InputReader in, PrintWriter out) {
        int tc = in.nextInt();
        for (int tcc = 1; tcc <= tc; tcc++) {
            out.print("Case #" + tcc + ": ");
            int n = in.nextInt();
            int p = in.nextInt();
            int q = in.nextInt();
            int r = in.nextInt();
            int s = in.nextInt();
            long[] sum = new long[n + 1];
            for (int i = 0; i < n; i++) {
                sum[i + 1] = sum[i] + ((long) i * p + q) % r + s;
            }
            if (n <= 3) {
                long ans = 0;
                for (int i = 1; i <= n; i++) {
                    if (sum[i] - sum[i - 1]  > ans) ans = sum[i] - sum[i - 1];
                }
                double res = (double) ans / sum[n];
                out.printf("%.15f\n",1 - res);
                continue;
            }
            long ans = sum[n];
            int l = 1;
            for (int i = 2; i <= n; i++) {
                while (l + 1< i && Math.max(sum[i] - sum[l], sum[l]) > Math.max(sum[i] - sum[l + 1], sum[l + 1])) {
                    ans = Math.min(ans, Math.max(sum[n] - sum[i], Math.max(sum[i] - sum[l], sum[l])));
                    l++;
                }
                ans = Math.min(ans, Math.max(sum[n] - sum[i], Math.max(sum[i] - sum[l], sum[l])));
            }
            double res = (double) ans / sum[n];
            out.printf("%.15f\n", 1 - res);
        }
    }
}

class InputReader {
    private BufferedReader reader;
    private StringTokenizer tokenizer;

    public InputReader(InputStream inputStream) {
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public String nextLine() {
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return line;
    }

    public String next() {
        while (tokenizer == null || !tokenizer.hasMoreTokens())
            tokenizer = new StringTokenizer(nextLine());
        return tokenizer.nextToken();
    }

    public int nextInt() {
        return Integer.parseInt(next());
    }
}


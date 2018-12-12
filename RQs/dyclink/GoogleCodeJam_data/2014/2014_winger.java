import java.io.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class GCJ3A implements Runnable {

	private PrintWriter out;
	
	final String file = "A-large";
	Random rnd = new Random(42);

	static class InputData {

        int n;
        int[] a;
		
		InputData(FastReader in) throws IOException {
            n = in.nextInt();
            long p = in.nextLong();
            long q = in.nextLong();
            long r = in.nextLong();
            long s = in.nextLong();
            a = new int[n];
            for (int i = 0; i < n; ++i) {
                a[i] = (int)((i * p + q) % r + s);
                if (a[i] <= 0) {
                    throw new AssertionError();
                }
            }
		}
		
		void solve(PrintWriter out) {
            long total = 0;
            for (int i = 0; i < n; ++i) {
                total += a[i];
            }
            long ans = 0, prefix = 0, prefix1 = 0, prefix2 = total;
            for (int i = 0, j = 0; i < n; ++i) {
                while (j < i || j < n && Math.min(prefix1, prefix2) <= Math.min(prefix1 + a[j], prefix2 - a[j])) {
                    prefix1 += a[j];
                    prefix2 -= a[j];
                    j++;
                }
                ans = Math.max(ans, total - Math.max(prefix, Math.max(prefix1, prefix2)));
                prefix += a[i];
                prefix1 -= a[i];
            }
            if (n <= 100) {
                long sum0 = 0;
                for (int i = 0; i < n; ++i) {

                }
            }
            out.println(1.0 * ans / total);
		}
	}
	
	static class Solver implements Callable<String> {

		InputData data;
		
		Solver(InputData data) {
			this.data = data;
		}

		@Override
		public String call() throws Exception {
			StringWriter out = new StringWriter();
			data.solve(new PrintWriter(out));
			return out.toString();
		}
		
	}

	public void run() {
		try {
			FastReader in = new FastReader(new BufferedReader(new FileReader(file + ".in")));
			out = new PrintWriter(file + ".out");
			
			ScheduledThreadPoolExecutor service = new ScheduledThreadPoolExecutor(7);
			
			int tests = in.nextInt();
			Future<String>[] ts = new Future[tests];
			for (int test = 0; test < tests; ++test) {
				ts[test] = service.submit(new Solver(new InputData(in)));
			}
			for (int test = 0; test < tests; ++test) {
				while (!ts[test].isDone()) {
					Thread.sleep(500);
				}
				System.err.println("Test " + test);
				out.print("Case #" + (test + 1) + ": ");
				out.print(ts[test].get());
			}
			service.shutdown();
			
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static class FastReader {
		public FastReader(BufferedReader in) {
			this.in = in;
			eat("");
		}
		
		private StringTokenizer st;
		private BufferedReader in;
		
		void eat(String s) {
			st = new StringTokenizer(s);
		}
		
		String next() throws IOException {
			while (!st.hasMoreTokens()) {
				String line = in.readLine();
				if (line == null) {
					return null;
				}
				eat(line);
			}
			return st.nextToken();
		}
		
		int nextInt() throws IOException {
			return Integer.parseInt(next());
		}
		
		long nextLong() throws IOException {
			return Long.parseLong(next());
		}
		
		double nextDouble() throws IOException {
			return Double.parseDouble(next());
		}
	}
	
	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		new GCJ3A().run();
	}
	
}

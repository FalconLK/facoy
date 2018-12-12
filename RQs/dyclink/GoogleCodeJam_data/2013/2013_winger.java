import java.io.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class GCJA implements Runnable {

	private PrintWriter out;
	
	final String file = "A-large";
	Random rnd = new Random(42);

    static final long INF = 1000000000000000000L;

	static class InputData {

        long b;
        long[] x;
        int n = 37;
		
		InputData(FastReader in) throws IOException {
            b = in.nextLong();
            int n = in.nextInt();
            x = new long[37];
            for (int i = 0; i < n; ++i) {
                x[i] = in.nextLong();
            }
		}
		
		void solve(PrintWriter out) {
            Arrays.sort(x);
            double ans = 0.;
            long last = 0, sum = 0;
            for (int i = 0; i < n; ++i) {
                sum += (x[i] - last) * i;
                last = x[i];
                if (sum > b) {
                    break;
                }
                if (i + 1 == n || x[i + 1] != x[i]) {
                    long next = i + 1 == n ? INF : x[i + 1];
                    int count = i + 1;
                    for (int c = 0; c < count && c + sum <= b; ++c) {
                        long bet = Math.min((b - c - sum) / count, next - x[i] - 1);
                        long bets = 0;
                        for (int j = 0; j < count - c; ++j) {
                            bets += last + bet - x[j];
                        }
                        double val = bets * 36.0 / (count - c) - bet * count - c - sum;
//                        System.err.println(count + " " + sum + " " + c + " " + bet + " " + bets + " " + val);
                        ans = Math.max(ans, val);
                    }
                }
            }
            out.println(ans);
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
            System.out.println(tests);
			Future<String>[] ts = new Future[tests];
			for (int test = 0; test < tests; ++test) {
				ts[test] = service.submit(new Solver(new InputData(in)));
			}
			for (int test = 0; test < tests; ++test) {
				while (!ts[test].isDone()) {
					Thread.sleep(500);
				}
				System.err.println("Test " + test + ": " + ts[test].get());
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
		new GCJA().run();
	}
	
}

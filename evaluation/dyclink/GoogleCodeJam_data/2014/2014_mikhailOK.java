import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class A {

	static class Input {
		int n;
		int[] a;

		Input(InputReader in) throws IOException {
			n = in.nextInt();
			int p = in.nextInt();
			int q = in.nextInt();
			int r = in.nextInt();
			int s = in.nextInt();
			a = new int[n];
			for (int i = 0; i < n; i++) {
				a[i] = (int) (((long) i * (long) p + q) % r + s);
			}
		}

		private boolean can(int[] a, long eat) {
			int n = a.length;
			int i = 0;
			long s1 = 0;
			while (i < n && s1 + a[i] <= eat) {
				s1 += a[i++];
			}
			long s2 = 0;
			while (i < n && s2 + a[i] <= eat) {
				s2 += a[i++];
			}
			long s3 = 0;
			while (i < n && s3 + a[i] <= eat) {
				s3 += a[i++];
			}
			return i == n;
		}

		Output solve() {
			long sum = 0;
			for (int val : a) {
				sum += val;
			}
			long left = -1, right = sum + 1;
			while (right - left > 1) {
				long mid = (left + right) >>> 1;
				if (can(a, mid)) {
					right = mid;
				} else {
					left = mid;
				}
			}

			double prob = 1.0 - (double) right / (double) sum;
			return new Output(prob);
		}
	}

	static class Output {
		double value;

		public Output(double value) {
			this.value = value;
		}

		void printOutput(PrintWriter out) {
			out.println(value);
		}
	}

	public static void main(String[] args) throws IOException,
			InterruptedException, ExecutionException {
		InputReader in = new InputReader(new FileInputStream("a.in"));
		PrintWriter out = new PrintWriter("a.out");
		ExecutorService executor = Executors.newFixedThreadPool(4);
		int testCases = in.nextInt();
		final Input[] inputs = new Input[testCases];
		for (int i = 0; i < testCases; i++) {
			inputs[i] = new Input(in);
		}

		@SuppressWarnings("unchecked")
		Future<Output> outputs[] = new Future[testCases];

		for (int i = 0; i < testCases; i++) {
			final int testCase = i;
			outputs[i] = executor.submit(new Callable<Output>() {
				@Override
				public Output call() throws Exception {
					return inputs[testCase].solve();
				}
			});
		}

		for (int i = 0; i < testCases; i++) {
			out.print("Case #" + (i + 1) + ": ");
			outputs[i].get().printOutput(out);
		}

		out.close();
		executor.shutdown();
	}

	static class InputReader {

		InputReader(InputStream input) {
			br = new BufferedReader(new InputStreamReader(input));
		}

		BufferedReader br;
		StringTokenizer st;

		String nextToken() throws IOException {
			while (st == null || !st.hasMoreTokens()) {
				String line = br.readLine();
				if (line == null) {
					return null;
				}
				st = new StringTokenizer(line);
			}
			return st.nextToken();
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
}
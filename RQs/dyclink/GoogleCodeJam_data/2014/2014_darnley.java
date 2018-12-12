import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class A {
	int n;
	long p, q, r, s;
	
	String solve() {
		long[] a = new long[n];
		long[] c = new long[n + 1];
		long sum = 0;
		for (int i = 0; i < n; i++) {
			a[i] = (i * p + q) % r + s;
			sum += a[i];
			c[i + 1] = sum;
		}
		long low = 0;
		long high = sum;
		while (low + 1 < high) {
			long m = (low + high) / 2;
			int i = 0;
			long x = 0;
			while (i < n && x + a[i] <= m) {
				x += a[i];
				i++;
			}
			int j = n - 1;
			long y = 0;
			while (j >= 0 && y + a[j] <= m) {
				y += a[j];
				j--;
			}
			if (sum - x - y <= m) {
				high = m;
			} else {
				low = m;
			}
		}
		return "" + (sum - high) * 1.0 / sum;
	}
	
	public A(Scanner in) {
		n = in.nextInt();
		p = in.nextInt();
		q = in.nextInt();
		r = in.nextInt();
		s = in.nextInt();
	}
	
	private static String fileName = A.class.getSimpleName().replaceFirst("_.*", "").toLowerCase();
	private static String inputFileName = fileName + ".in";
	private static String outputFileName = fileName + ".out";
	
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(4);
		Locale.setDefault(Locale.US);
		Scanner in = new Scanner(new File(inputFileName));
		PrintWriter out = new PrintWriter(outputFileName);
		int tests = in.nextInt();
		in.nextLine();
		@SuppressWarnings("unchecked")
		Future<String>[] outputs = new Future[tests];
		for (int t = 0; t < tests; t++) {
			final A testCase = new A(in);
			outputs[t] = executor.submit(new Callable<String>() {
				@Override
				public String call() {
					return testCase.solve();
				}
			});
		}
		for (int t = 0; t < tests; t++) {
			out.println("Case #" + (t + 1) + ": " + outputs[t].get());
		}
		in.close();
		out.close();
		executor.shutdown();
	}
}

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class A {

	public static long getStartIsMax(long[] a, long[] b) {
		long result = 0;
		for (int i = 0; i < a.length - 2; i++) {
			long meSum = a[i];
			int l = i + 1;
			int h = a.length - 2;
			while (h > l) {
				int mid = l + h >> 1;
				if (a[mid] - meSum <= meSum)
					l = mid + 1;
				else
					h = mid;
			}
			if (a[h] - meSum > meSum)
				h--;
			if (h > i && b[h + 1] <= meSum)
				result = Math.max(result, b[h + 1] + a[h] - meSum);
		}
		// System.out.println("START " + result);
		return result;
	}

	public static long getMiddleIsMax(long[] a, long[] b) {
		long result = 0;
		for (int t = 1; t < a.length - 1; t++) {
			int l = 0;
			int h = t - 1;
			while (h > l) {
				int mid = l + h >> 1;
				if (a[t] - a[mid] >= a[mid] && a[t] - a[mid] >= b[t + 1])
					l = mid + 1;
				else
					h = mid;
			}
			if (!(a[t] - a[h] >= a[h] && a[t] - a[h] >= b[t + 1]))
				h--;
			if (h >= 0 && a[t] - a[h] >= a[h] && a[t] - a[h] >= b[t + 1]) {
				result = Math.max(result, a[h] + b[t + 1]);
			}
		}
		// System.out.println("MID " + result);
		return result;
	}

	public static void main(String[] args) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader("A.in"));
		PrintWriter out = new PrintWriter("A.out");
		StringTokenizer strtok;
		strtok = new StringTokenizer(in.readLine());
		int tc = Integer.parseInt(strtok.nextToken());
		for (int cc = 1; cc <= tc; cc++) {
			strtok = new StringTokenizer(in.readLine());
			int n = Integer.parseInt(strtok.nextToken());
			long p = Integer.parseInt(strtok.nextToken());
			long q = Integer.parseInt(strtok.nextToken());
			long r = Integer.parseInt(strtok.nextToken());
			long s = Integer.parseInt(strtok.nextToken());
			long[] weights = new long[n];
			for (int i = 0; i < n; i++)
				weights[i] = (i * p + q) % r + s;
			long[] forward = new long[n];
			long[] backward = new long[n];
			for (int i = 0; i < forward.length; i++) {
				forward[i] = weights[i];
				if (i != 0)
					forward[i] += forward[i - 1];
			}
			for (int i = n - 1; i >= 0; i--) {
				backward[i] = weights[i];
				if (i != n - 1)
					backward[i] += backward[i + 1];
			}
			// System.out.println(Arrays.toString(weights));
			long result = 0;
			for (int i = 1; i < n; i++) {
				result = Math
						.max(result, Math.min(forward[i - 1], backward[i]));
			}
			// System.out.println("INIT " + result);
			result = Math.max(result, getStartIsMax(forward, backward));
			result = Math.max(result, getMiddleIsMax(forward, backward));
			for (int i = 0; i < n / 2; i++) {
				long t = weights[i];
				weights[i] = weights[n - 1 - i];
				weights[n - 1 - i] = t;
			}
			for (int i = 0; i < forward.length; i++) {
				forward[i] = weights[i];
				if (i != 0)
					forward[i] += forward[i - 1];
			}
			for (int i = n - 1; i >= 0; i--) {
				backward[i] = weights[i];
				if (i != n - 1)
					backward[i] += backward[i + 1];
			}
			result = Math.max(result, getStartIsMax(forward, backward));
			System.out.printf("Case #%d: %.13f\n", cc, result * 1.00
					/ forward[n - 1]);
			out.printf("Case #%d: %.13f\n", cc, result * 1.00 / forward[n - 1]);
		}
		out.close();
	}
}

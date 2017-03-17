import java.io.*;
import java.util.*;

public class A {
	private static String fileName = A.class.getSimpleName().replaceFirst("_.*", "").toLowerCase();
	private static String inputFileName = fileName + ".in";
	private static String outputFileName = fileName + ".out";
	private static Scanner in;
	private static PrintWriter out;

	private void solve() {
		long money = in.nextLong();
		int bets = in.nextInt();
		int m = 37;
		long[] a = new long[m];
		for (int i = 0; i < bets; i++) {
			a[i] = in.nextLong();
		}
		Arrays.sort(a);
		double ans = 0;
		for (int i = 1; i <= m; i++) {
			long now = a[i - 1];
			long eq = 0;
			for (int j = 0; j < i; j++) {
				eq += now - a[j];
			}
			int same = 0;
			for (int j = i; j < m; j++) {
				if (a[j] == now) {
					same++;
				}
			}
			if (eq + same > money) {
				break;
			}
			long left = 0;
			long right = money + 1;
			while (left + 1 < right) {
				long up = (left + right) / 2;
				long need = eq + i * up;
				for (int j = i; j < m; j++) {
					need += Math.max((now + up + 1) - a[j], 0);
					if (need > money) {
						break;
					}
				}
				if (need > money) {
					right = up;
				} else {
					left = up;
				}
			}
			long up = left;
			double prof = - eq - up * i;
			for (int j = i; j < m; j++) {
				prof -= Math.max((now + up + 1) - a[j], 0);
			}
			for (int j = 0; j < i; j++) {
				prof += (now + up - a[j]) * 36.0 / i;
			}
			ans = Math.max(ans, prof);
		}
		System.out.println(ans);
		out.println(ans);
	}

	public static void main(String[] args) throws IOException {
		Locale.setDefault(Locale.US);
		if (args.length >= 2) {
			inputFileName = args[0];
			outputFileName = args[1];
		}
		in = new Scanner(new File(inputFileName));
		out = new PrintWriter(outputFileName);
		int tests = in.nextInt();
		in.nextLine();
		for (int t = 1; t <= tests; t++) {
			out.print("Case #" + t + ": ");
			new A().solve();
			System.out.println("Case #" + t + ": solved");
		}
		in.close();
		out.close();
	}
}

package google.codejam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

public class Tour {
	
	static long max(long... values) {
		long max = values[0];
		for (int i = 1; i < values.length; i++)
			if (max < values[i])
				max = values[i];
		return max;
	}
	
	public static void main(String[] args) {
		try {
			Scanner in = new Scanner(new BufferedReader(new FileReader(new File("A-large.in")), 256 << 10));
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("output.txt")), 256 << 10);

			int testsNumber = in.nextInt();
			for (int testId = 1; testId <= testsNumber; testId++) {
				int n = in.nextInt();
				int p = in.nextInt(), q = in.nextInt(), r = in.nextInt(), s = in.nextInt();
				long sum = 0;
				long[] a = new long[n];
				for (int i = 0; i < n; i++) {
					a[i] = (((long)i * p + q) % r + s);
					sum += a[i];
				}
				long min = sum;
				long si = 0, sj = 0;
				for (int i = 0, j = n-1; i <= j; ) {
					if (si + a[i] < sj + a[j]) {
						si += a[i++];
					}
					else {
						sj += a[j--];
					}
					min = Math.min(min, max(sum-si-sj, si, sj));
				}
				out.append("Case #" + testId + ": " + 1.0 * (sum-min) / sum);
				out.append("\n");
			}
			in.close();
			out.close();
		}
		catch (Exception e) {
			System.err.println("Error:" + e.getMessage());
		}
	}
}

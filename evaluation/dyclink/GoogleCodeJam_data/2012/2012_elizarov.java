package round3;

import java.io.*;
import java.util.Scanner;

/**
 * @author Roman Elizarov
 */
public class A {
	public static void main(String[] args) throws IOException {
		new A().go();
	}

	Scanner in;
	PrintWriter out;

	private void go() throws IOException  {
		in = new Scanner(new File("src\\round3\\a.in"));
		out = new PrintWriter(new File("src\\round3\\a.out"));
		int t = in.nextInt();
		for (int tn = 1; tn <= t; tn++) {
			out.println("Case #" + tn + ": " + solveCase());
		}
		in.close();
		out.close();
	}

	int n;
	int[] l;
	int[] p;
	int[] a;

	private String solveCase() {
		n = in.nextInt();
		l = new int[n];
		for (int i = 0; i < n; i++)
			l[i] = in.nextInt();
		p = new int[n];
		for (int i = 0; i < n; i++)
			p[i] = in.nextInt();
		a = new int[n];
		for (int i = 0; i < n; i++)
			a[i] = i;
		boolean changes;
		do {
			changes = false;
			long l1 = 0;
			for (int i = 0; i < n - 1; i++) {
				int l2 = l[a[i]];
				int l3 = l[a[i + 1]];
				int p2 = p[a[i]];
				int p3 = p[a[i + 1]];
				long eCur = (l1 + l2) * p2 + (l1 + l2 + l3) * (100 - p2) * p3;
				long eSwap = (l1 + l3) * p3 + (l1 + l2 + l3) * (100 - p3) * p2;
				if (eSwap < eCur || eSwap == eCur && a[i + 1] < a[i]) {
					int t = a[i];
					a[i] = a[i + 1];
					a[i + 1] = t;
					changes = true;
				}
				l1 += l[a[i]];
			}
		} while (changes);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			if (i > 0)
				sb.append(' ');
			sb.append(a[i]);
		}
		return sb.toString();
	}
}

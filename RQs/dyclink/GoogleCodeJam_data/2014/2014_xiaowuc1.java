import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.math.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
public class A {
	static BufferedReader br;
	static StringTokenizer st;
	static PrintWriter pw;
	
	static long[] num;
	
	public static void main(String[] args) throws Exception {
		br = new BufferedReader(new FileReader("a.in"));
		pw = new PrintWriter(new BufferedWriter(new FileWriter("a.out")));
		final int MAX_CASES = readInt();
		for(int casenum = 1; casenum <= MAX_CASES; casenum++) {
			pw.printf("Case #%d: ", casenum);
			int n = readInt();
			int p = readInt();
			int q = readInt();
			int r = readInt();
			int s = readInt();
			num = new long[n+1];
			for(int i = 1; i <= n; i++) {
				long curr = i-1;
				curr *= p;
				curr += q;
				curr %= r;
				curr += s;
				num[i] = num[i-1] + curr;
			}
			double ret = 0;
			for(int i = 0; i < n; i++) {
				int min = i;
				int max = n-1;
				while(max-min > 3) {
					int left = (2*min+max)/3;
					int right = (2*max+min)/3;
					long leftCount = count(i, left);
					long rightCount = count(i, right);
					if(leftCount < rightCount) {
						max = right;
					}
					else {
						min = left;
					}
				}
				for(int j = min; j <= max; j++) {
					ret = Math.max(ret, 1-count(i,j)*1.0/num[n]);
				}
			}
			pw.println(ret);
		}
		pw.close();
	}

	public static long count(int l, int r) {
		long mid = num[r+1] - num[l];
		long left = num[l];
		long right = num[num.length-1] - num[r+1];
		return Math.max(mid, Math.max(left, right));
	}
	
	public static int readInt() {
		return Integer.parseInt(nextToken());
	}

	public static long readLong() {
		return Long.parseLong(nextToken());
	}

	public static double readDouble() {
		return Double.parseDouble(nextToken());
	}

	public static String nextToken() {
		while(st == null || !st.hasMoreTokens())	{
			try {
				if(!br.ready())	{
					pw.close();
					System.exit(0);
				}
				st = new StringTokenizer(br.readLine());
			}
			catch(IOException e) {
				System.err.println(e);
				System.exit(1);
			}
		}
		return st.nextToken();
	}

	public static String readLine()	{
		st = null;
		try {
			return br.readLine();
		}
		catch(IOException e) {
			System.err.println(e);
			System.exit(1);
			return null;
		}
	}

}

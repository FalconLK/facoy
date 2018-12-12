import java.io.*;
import java.util.*;

public class taskA {

	PrintWriter out;
	BufferedReader br;
	StringTokenizer st;

	String nextToken() throws IOException {
		while ((st == null) || (!st.hasMoreTokens()))
			st = new StringTokenizer(br.readLine());
		return st.nextToken();
	}

	public int nextInt() throws IOException {
		return Integer.parseInt(nextToken());
	}

	public long nextLong() throws IOException {
		return Long.parseLong(nextToken());
	}

	public double nextDouble() throws IOException {
		return Double.parseDouble(nextToken());
	}

	class Job implements Comparable<Job> {
		int t, p, num;

		public Job(int t, int num) {
			super();
			this.t = t;
			this.num = num;
		}

		@Override
		public int compareTo(Job arg0) {
			return - p + arg0.p;
		}
		
		
	}

	public void solve() throws IOException {
		int n = nextInt();
		Job[] a = new Job[n];
		for (int i = 0; i < n; i++) {
			a[i] = new Job(nextInt(), i);
		}
		for (int i = 0; i < n; i++) {
			a[i].p = nextInt();
		}
		Arrays.sort(a);
		for (int i = 0;i < n;i++) {
			out.print(a[i].num + " ");
		}
		out.println();
	}

	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			out = new PrintWriter(System.out);

			br = new BufferedReader(new FileReader("taskA.in"));
			out = new PrintWriter("taskA.out");
			int n = nextInt();
			for (int i = 0; i < n; i++) {

				out.print("Case #" + (i + 1) + ": ");
				solve();
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new taskA().run();
	}
}

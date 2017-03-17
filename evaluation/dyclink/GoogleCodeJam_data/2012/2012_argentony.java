import java.io.*;
import java.util.*;

public class A {
	public static void main(String[] args) {
		new A().run();
	}

	BufferedReader br;
	StringTokenizer st;
	PrintWriter out;
	boolean eof;

	public void run() {
		for (File inf : (new File(".")).listFiles()) {
			if (inf.getName().startsWith("A-") && inf.getName().endsWith(".in")) {
				System.out.println("Processing " + inf.getName() + ":");
				try {
					br = new BufferedReader(new FileReader(inf));
					File ouf = new File(inf.getName().replaceAll("\\.in",
							".out"));
					out = new PrintWriter(ouf);
					st = null;
					eof = false;
					solve();
				} catch (Throwable e) {
					e.printStackTrace();
					System.exit(-1);
				} finally {
					out.close();
				}
			}
		}
	}

	String nextToken() {
		while (st == null || !st.hasMoreElements()) {
			try {
				st = new StringTokenizer(br.readLine());
			} catch (Exception e) {
				eof = true;
				return "0";
			}
		}
		return st.nextToken();
	}

	int nextInt() {
		return Integer.parseInt(nextToken());
	}

	double nextDouble() {
		return Double.parseDouble(nextToken());
	}

	void solve() throws IOException {
		int testCount = nextInt();
		for (int testNumber = 1; testNumber <= testCount; ++testNumber) {
			System.out.println(testNumber + " / " + testCount);
			out.print("Case #" + testNumber + ": ");
			int n = nextInt();
			Level[] g = new Level[n];
			for (int i = 0; i < n; ++i) {
				g[i] = new Level(-1, nextInt(), i);
			}
			for (int i = 0; i < n; ++i) {
				g[i].p = nextInt();
			}
			
			Arrays.sort(g);
			
			for (Level l : g) {
				out.print(" " + l.n);
			}
			
			out.println();
			
			out.flush();
		}
	}
	
	class Level implements Comparable<Level> {
		
		int p, l, n;
		
		public Level(int p, int l, int n) {
			this.p = p;
			this.l = l;
			this.n = n;
		}

		@Override
		public int compareTo(Level o) {
//			int res = Integer.compare(o.l * (1-p), l * (1-o.p));
			int res = -Integer.compare(o.l * (p), l * (o.p)); 
			return res != 0 ? res : Integer.compare(n, o.n);
		}
	}

}

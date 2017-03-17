import java.io.*;

import java.awt.geom.Point2D;
import java.text.*;
import java.math.*;
import java.util.*;

public class Main implements Runnable {

	final String problem = "A";
//	final String filename = problem + "-sample";

//	 final String filename= problem+"-small-attempt0";
	// final String filename= problem+"-small-attempt1";
	 final String filename= problem+"-large";

	public void solve() throws Exception {
		int N = iread();
		int[] L = new int[N];
		for (int i = 0; i < N; i++)
			L[i] = iread();
		int[] p = new int[N];
		for (int i = 0; i < N; i++)
			p[i] = iread();
		Obj[] test = new Obj[N];
		for (int i = 0; i < N; i++)
			test[i] = new Obj(L[i], p[i], i);
		Arrays.sort(test);
		for (int i = 0; i < N; i++) {
			out.write(test[i].num + "");
			if (i < N - 1)
				out.write(" ");
		}
	}

	class Obj implements Comparable<Obj> {
		int L;
		int p;
		int num;

		public Obj(int l, int p, int num) {
			super();
			L = l;
			this.p = p;
			this.num = num;
		}

		@Override
		public int compareTo(Obj o) {
			if (o.L * p != L * o.p)
				return -o.L * p + L * o.p;
			return num - o.num;
		}
	}

	public void solve_gcj() throws Exception {
		int tests = iread();
		for (int test = 1; test <= tests; test++) {
			out.write("Case #" + test + ": ");
			solve();
			out.write("\n");
		}
	}

	public void run() {
		try {
			// in = new BufferedReader(new InputStreamReader(System.in));
			// out = new BufferedWriter(new OutputStreamWriter(System.out));
			in = new BufferedReader(new FileReader(filename + ".in"));
			out = new BufferedWriter(new FileWriter(filename + ".out"));
			solve_gcj();
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public int iread() throws Exception {
		return Integer.parseInt(readword());
	}

	public double dread() throws Exception {
		return Double.parseDouble(readword());
	}

	public long lread() throws Exception {
		return Long.parseLong(readword());
	}

	BufferedReader in;

	BufferedWriter out;

	public String readword() throws IOException {
		StringBuilder b = new StringBuilder();
		int c;
		c = in.read();
		while (c >= 0 && c <= ' ')
			c = in.read();
		if (c < 0)
			return "";
		while (c > ' ') {
			b.append((char) c);
			c = in.read();
		}
		return b.toString();
	}

	public static void main(String[] args) {
		try {
			Locale.setDefault(Locale.US);
		} catch (Exception e) {

		}
		// new Thread(new Main()).start();
		new Thread(null, new Main(), "1", 1 << 25).start();
	}
}
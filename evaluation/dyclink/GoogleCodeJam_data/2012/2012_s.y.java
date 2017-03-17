package R3;
import java.io.*;
import java.math.*;
import java.util.*;

import R3.Main.IOFast;


public class A {
	private final String dir = "D:\\contest\\gcj\\2012\\" + getClass().getPackage().toString().split(" ")[1] + "\\" + getClass().getSimpleName();
	private final String inputFileName = "A-large.in";
//	private final String inputFileName = "C-large.in";
	private final String outputFileName = inputFileName + "_res.txt";
	
	void run() throws IOException {
		final int n = IOFast.nextInt();
		int[][] xs = new int[n][3];
		for(int i = 0; i < n; i++) {
			xs[i][2] = i;
			xs[i][0] = IOFast.nextInt();
		}
		for(int i = 0; i < n; i++) {
			xs[i][1] = IOFast.nextInt();
		}
		for(int j = 0; j < n; j++) {
			for(int i = 0; i + 1 < n; i++)  {
				if(xs[i][1] * xs[i+1][0] < xs[i+1][1] * xs[i][0]) {
					int[] x = xs[i];
					xs[i] = xs[i+1];
					xs[i+1] = x;
				}
			}
		}
		for(int i = 0; i < n; i++) {
			IOFast.out.print(xs[i][2] + (i==n-1?"\r\n":" "));
		}
	}
	
	///////////////////// TEMPLATE ////////////////////////
	static void start() throws IOException {
		new A().run1();
	}

	void run1() throws IOException {
		IOFast.setReader(new FileReader(dir + "\\" + inputFileName));
		IOFast.setWriter(new FileWriter(dir + "\\" + outputFileName));
		int T = IOFast.nextInt();
		while(T-- != 0) {
			printCase();
			run();
		}
	}
	
	private static int CASE;
	private static void printCase() {
		IOFast.out.print("Case #" + ++CASE + ": ");
		System.err.println("Done: Case #" + CASE);
	}
	
	public static void main(String[] args) throws IOException {
		start();
		IOFast.out.flush();
		IOFast.in.close();
		IOFast.out.close();
	}

	static
	public class IOFast {
		private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		private static PrintWriter out = new PrintWriter(System.out);

		//	private static final int BUFFER_SIZE = 50 * 200000;
		private static final StringBuilder buf = new StringBuilder();
		private static boolean[] isDigit = new boolean[256];
		private static boolean[] isSpace = new boolean[256];

		static {
			for(int i = 0; i < 10; i++) {
				isDigit['0' + i] = true;
			}
			isDigit['-'] = true;
			isSpace[' '] = isSpace['\r'] = isSpace['\n'] = isSpace['\t'] = true;
		}

		static boolean endInput;
		
		public static void setReader(Reader r) { in = new BufferedReader(r); }
		public static void setWriter(Writer w) { out = new PrintWriter(w); }

		private static int nextInt() throws IOException {
			boolean plus = false;
			int ret = 0;
			while(true) {
				final int c = in.read();

				if(c == -1) {
					endInput = true;
					return Integer.MIN_VALUE;
				}

				if(isDigit[c]) {
					if(c != '-') {
						plus = true;
						ret = c - '0';
					}
					break;
				}
			}

			while(true) {
				final int c = in.read();
				if(c == -1 || !isDigit[c]) {
					break;
				}
				ret = ret * 10 + c - '0';
			}

			return plus ? ret : -ret;
		}

		private static long nextLong() throws IOException {
			boolean plus = false;
			long ret = 0;
			while(true) {
				final int c = in.read();

				if(c == -1) {
					endInput = true;
					return Integer.MIN_VALUE;
				}

				if(isDigit[c]) {
					if(c != '-') {
						plus = true;
						ret = c - '0';
					}
					break;
				}
			}

			while(true) {
				final int c = in.read();
				if(c == -1 || !isDigit[c]) {
					break;
				}
				ret = ret * 10 + c - '0';
			}

			return plus ? ret : -ret;
		}



		private static String next() throws IOException {
			buf.setLength(0);

			while(true) {
				final int c = in.read();

				if(c == -1) {
					endInput = true;
					return "-1";
				}

				if(!isSpace[c]) {
					buf.append((char)c);
					break;
				}
			}

			while(true) {
				final int c = in.read();

				if(c == -1 || isSpace[c]) {
					break;
				}
				buf.append((char)c);
			}

			return buf.toString();
		}

		private static double nextDouble() throws IOException {
			return Double.parseDouble(next());
		}

	}
}

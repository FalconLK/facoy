import java.util.*;
import java.util.regex.*;
import java.text.*;
import static java.lang.Math.*;
import java.io.*;
import java.math.*;

public final class PerfectGame implements Runnable {

	private final void solveTest(final int testNum) throws Exception {

		final int n = in.nextInt();

		final int[] l = new int[n];
		final int[] d = new int[n];
		for (int i = 0; i < n; ++i) {
			l[i] = in.nextInt();
		}
		for (int i = 0; i < n; ++i) {
			d[i] = in.nextInt();
		}

		final int ind[] = new int[n];
		for (int i = 0; i < n; ++i) {
			ind[i] = i;
		}

		for (int i = 0; i < n; ++i) {
			for (int j = i + 1; j < n; ++j) {
				if ((l[ind[i]] * d[ind[i]] < l[ind[j]] * d[ind[j]])
						|| (l[ind[i]] * d[ind[i]] == l[ind[j]] * d[ind[j]] && (d[ind[i]] == 0 && ind[i] > ind[j] ||  (d[ind[i]] != 0 && (l[ind[i]] > l[ind[j]] || (l[ind[i]] == l[ind[j]] && ind[i] > ind[j])))     ))) {
					int t = ind[i];
					ind[i] = ind[j];
					ind[j] = t;
				}
			}
		}

		final int result = 0;
		out.print(String.format("Case #%d:", testNum));
		for (int i = 0; i < n; ++i) {
			out.print(" " + ind[i]);
		}
		out.println();
		// final double result = 0;
		// out.println(String.format("Case #%d: %.15f", testNum, result));
	}

	public final void run() {
		final int testCnt = in.nextInt();
		for (int testNum = 1; testNum <= testCnt; ++testNum) {
			try {
				solveTest(testNum);
			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(-1);
			}
		}
		out.flush();
		if (!USE_STD_INPUT_OUTPUT) {
			out.close();
		}
	}

	private static final boolean USE_STD_INPUT_OUTPUT = false;

	private static InputStream inputStream = null;
	private static OutputStream outputStream = null;

	private final static String FILE_IN = PerfectGame.class.getSimpleName()
			+ ".in";
	private final static String FILE_OUT = PerfectGame.class.getSimpleName()
			+ ".out";

	static {
		if (USE_STD_INPUT_OUTPUT) {
			inputStream = System.in;
			outputStream = System.out;
		} else {
			try {
				inputStream = new FileInputStream(new File(FILE_IN));
				outputStream = new FileOutputStream(new File(FILE_OUT));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

	private static final Scanner in = new Scanner(inputStream);
	private static final PrintWriter out = new PrintWriter(outputStream);

	public static final void main(final String[] args) throws Exception {
		final long startTime = System.currentTimeMillis();
		final Thread th = new Thread(null, new PerfectGame(), "", 32 << 20);
		th.setPriority(Thread.MAX_PRIORITY);
		th.start();
		th.join();
		System.out.println(String.format("%.3f",
				0.001 * (System.currentTimeMillis() - startTime)));
	}
}

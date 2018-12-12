import java.io.*;
import java.util.*;

public class A {
	private static String fileName = A.class.getSimpleName().replaceFirst("_.*", "").toLowerCase();
	private static String inputFileName = fileName + ".in";
	private static String outputFileName = fileName + ".out";
	private static Scanner in;
	private static PrintWriter out;

	class Level implements Comparable<Level> {
		int id;
		int time;
		double prob;
		double v;
		
		public Level(int id, int time, double prob) {
			this.id = id;
			this.time = time;
			this.prob = prob;
			this.v = - prob / time;
		}

		@Override
		public int compareTo(Level t) {
			int c = Double.compare(v, t.v);
			if (c != 0) {
				return c;
			}
			return id - t.id;
		}
	}
	
	private void solve() {
		int n = in.nextInt();
		int[] time = new int[n];
		double[] prob = new double[n];
		for (int i = 0; i < n; i++) {
			time[i] = in.nextInt();
		}
		for (int i = 0; i < n; i++) {
			prob[i] = in.nextInt() / 100.0;
		}
		Level[] levels = new Level[n];
		for (int i = 0; i < n; i++) {
			levels[i] = new Level(i, time[i], prob[i]);
		}
		Arrays.sort(levels);
		for (Level level : levels) {
			out.print(" " + level.id);
		}
		out.println();
	}

	public static void main(String[] args) throws IOException {
		Locale.setDefault(Locale.US);
		if (args.length >= 2) {
			inputFileName = args[0];
			outputFileName = args[1];
		}
		in = new Scanner(new FileReader(inputFileName));
		out = new PrintWriter(outputFileName);
		int tests = in.nextInt();
		in.nextLine();
		for (int t = 1; t <= tests; t++) {
			out.print("Case #" + t + ":");
			new A().solve();
			System.out.println("Case #" + t + ": solved");
		}
		in.close();
		out.close();
	}
}

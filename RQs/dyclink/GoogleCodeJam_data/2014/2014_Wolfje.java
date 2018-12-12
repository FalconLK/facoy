import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;
import static java.lang.Math.*;

public class ProblemA {
	String PROBLEM_ID = "problemA";

	enum TestType {
		EXAMPLE, SMALL, LARGE
	}

//	 TestType TYPE = TestType.EXAMPLE;
//	 TestType TYPE = TestType.SMALL;
	TestType TYPE = TestType.LARGE;

	public String getFileName() {
		String result = PROBLEM_ID + "_";
		switch (TYPE) {
		case EXAMPLE:
			result += "example";
			break;
		case SMALL:
			result += "small";
			break;
		case LARGE:
			result += "large";
			break;
		}
		return result;
	}

	public String getInFileName() {
		return getFileName() + ".in";
	}

	public String getOutFileName() {
		return getFileName() + ".out";
	}

	public static void main(String[] args) throws Exception {
		new ProblemA();
	}

	public ProblemA() throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(getInFileName()));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				getOutFileName())));
		Scanner scan = new Scanner(in);
		int tests = scan.nextInt();
		for (int test = 0; test < tests; test++) {
			int n = scan.nextInt();
			long p = scan.nextLong();
			long q = scan.nextLong();
			long r = scan.nextLong();
			long s = scan.nextLong();
			long[] x = new long[n];
			for ( int i = 0; i < n; i++) {
				x[i] = (i * p + q) % r + s;
			}
			long[] sum = new long[n];
			sum[0] = x[0];
			for ( int i = 1; i < n; i++) sum[i] = sum[i-1] + x[i];
			long total = sum[n-1];
			
			long lb = 0;
			long ub = total;
			while ( lb + 1 < ub ) {
				long mid = (lb+ub)/2;
				int pieces = 0;
				long size = 0;
				for ( int i = 0; i < n; i++) {
					if ( size + x[i] <= mid) {
						size += x[i];
					} else {
						pieces++;
						size = x[i];
						if ( size > mid ) pieces += 10;
					}
				}
				pieces++;
				if ( pieces <= 3) ub = mid;
				else lb = mid;
			}
			
			
//			System.out.println(Arrays.toString(x));
			double prob = (total-ub)*1.0/total;
			String resultStr = String.format("Case #%d: %.12f", test + 1, prob);
			// add answer here

			System.out.println(resultStr);
			out.println(resultStr);
		}
		out.close();
		System.out.println("*** in file =  " + getInFileName());
		System.out.println("*** out file = " + getOutFileName());
	}
}

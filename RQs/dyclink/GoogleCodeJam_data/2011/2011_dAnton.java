import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main implements Runnable {
	public static void main(String[] args) {
		Thread thread = new Thread(new Main());
		thread.start();
	}

	public void run() {
		try {
			run1();
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	int nextInt(StreamTokenizer st) throws IOException {
		st.nextToken();
		return (int) (st.nval);
	}

	long nextLong(StreamTokenizer st) throws IOException {
		st.nextToken();
		return (long) (st.nval);
	}

	BigInteger nextBI(StreamTokenizer st) throws IOException {
		st.nextToken();
		return new BigInteger(st.sval);
	}

	String nextString(StreamTokenizer st) throws IOException {
		st.nextToken();
		return st.sval;
	}

	double nextDouble(StreamTokenizer st) throws IOException {
		st.nextToken();
		return st.nval;
	}

	Map<Long, double[]> res = new HashMap<Long, double[]>();

	public void run1() throws IOException {
		InputStream inputStream = new FileInputStream("input.txt");
		// InputStream inputStream = System.in;
		Scanner sc = new Scanner(new InputStreamReader(inputStream));
		// StreamTokenizer st = new StreamTokenizer(inputStream);
		// BufferedReader br = new BufferedReader(new
		// InputStreamReader(inputStream));
		PrintWriter pw = new PrintWriter(new FileWriter("output.txt"));
		int tN = sc.nextInt();
		case_cycle: for (int tn = 1; tn <= tN; tn++) {
			double w = sc.nextInt();
			int[] n = new int[]{sc.nextInt(), sc.nextInt()};
			int g = sc.nextInt();
			double[][][] p = new double[2][][]; 
			for(int t = 0; t < 2; t++) {
				p[t] = new double[n[t]][2];
				for(int i = 0; i < n[t]; i++) {
					for(int j= 0; j < 2; j++) {
						p[t][i][j] = sc.nextInt();
					}
				}
			}
			double area = get(p, w);
			pw.format("Case #%d:\n", tn);
			for(int i = 1; i < g; i++) {
				pw.println(find(p, w, area * i / g));
			}
		}
		pw.close();
	}
	
	double get(double[][][] p, double w) {
		return get(p[1], w) - get(p[0], w);
	}
	
	double get(double[][] p, double w) {
		int n = p.length;
		double res = 0;
		for(int i = 1; i < n; i++) {
			if(p[i][0] < w) {
				res += (p[i][0] - p[i - 1][0]) * (p[i][1] + p[i - 1][1]) / 2;
			} else {
				double y = p[i - 1][1] + (p[i][1] - p[i - 1][1]) * (w - p[i - 1][0]) / (p[i][0] - p[i - 1][0]);
				res += (w - p[i - 1][0]) * (y + p[i - 1][1]) / 2;
				break;
			}
		}
		return res;
	}
	
	double find(double[][][] p, double w, double area) {
		double l = 0;
		double r = w;
		for(int t = 0; t < 200; t++) {
			double m = (l + r) / 2;
			if(get(p, m) < area) {
				l = m;
			} else
				r = m;
		}
		return (l + r) / 2;
	}
}

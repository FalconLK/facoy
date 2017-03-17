import java.io.*;
import java.util.*;
import java.math.*;

public class Main {
//	static Scanner in; static int next() throws Exception {return in.nextInt();};
	static StreamTokenizer in; static int next() throws Exception {in.nextToken(); return (int) in.nval;};
//	static BufferedReader in;
	static PrintWriter out;
	static String NAME = "a";
	
	public static void main(String[] args) throws Exception {
//		in = new Scanner(new File(NAME + ".in"));
		in = new StreamTokenizer(new BufferedReader(new FileReader(new File(NAME + ".in"))));
//		in = new BufferedReader(new FileReader(new File(NAME + ".in")));
		out = new PrintWriter(new File(NAME + ".out"));
			
		int tests = next();
		
		for (int test = 1; test <= tests; test++) {
		
			int w = next();
			int l = next();
			int u = next();
			int g = next();
			
			int[] xl = new int[l];
			int[] yl = new int[l];
			int[] xu = new int[u];
			int[] yu = new int[u];
			
			for (int i = 0; i < l; i++) {
				xl[i] = next();
				yl[i] = next();
			}
			for (int i = 0; i < u; i++) {
				xu[i] = next();
				yu[i] = next();
			}
			
			int n = u + l - 2;
			int[] x = new int[n];
			for (int i = 0; i < u; i++) x[i] = xu[i];
			for (int i = 1; i < l - 1; i++) x[i - 1 + u] = xl[i];
			Arrays.sort(x);
			double[] y1 = new double[n];
			int ind = 0;
			for (int i = 0; i < n; i++) {
				while (x[i] > xl[ind + 1]) ind++;
				y1[i] = yl[ind] + (double)(yl[ind + 1] - yl[ind]) * (x[i] - xl[ind])/(xl[ind + 1] - xl[ind]);
			}
			double[] y2 = new double[n];
			ind = 0;
			for (int i = 0; i < n; i++) {
				while (x[i] > xu[ind + 1]) ind++;
				y2[i] = yu[ind] + (double)(yu[ind + 1] - yu[ind]) * (x[i] - xu[ind])/(xu[ind + 1] - xu[ind]);
			}
			
/*			for (int i = 0; i < n; i++) out.print(x[i] + " ");
			out.println();
			for (int i = 0; i < n; i++) out.print(y1[i] + " ");
			out.println();
			for (int i = 0; i < n; i++) out.print(y2[i] + " ");
			out.println();*/
			
			double[] yy = new double[n];
			for (int i = 0; i < n; i++) yy[i] = y2[i] - y1[i];
			
			double ss = 0;
			for (int i = 0; i < n - 1; i++) {
				ss += (x[i + 1] - x[i]) * (yy[i] + yy[i + 1])/2;
			}
			
			ind = 0;
			double s = 0;
			
			out.println("Case #" + test + ": ");
			
			for (int i = 1; i < g; i++) {
				while (s + (x[ind + 1] - x[ind])*(yy[ind] + yy[ind + 1])/2 < i*ss/g) {
					s += (x[ind + 1] - x[ind])*(yy[ind] + yy[ind + 1])/2;
					ind++;
				}
				double L = x[ind];
				double R = x[ind + 1];
				for (int j = 0; j < 100; j++) {
					double tos = i*ss/g - s;
					double M = (R + L)/2;
					if ((M - x[ind]) * (yy[ind] + (M - x[ind])/(x[ind + 1] - x[ind])*(yy[ind + 1] - yy[ind])/2) > tos) R = M;
					else L = M;
				}
				out.println(L);
			}
		
		}
		
		out.close();
	}
	
}
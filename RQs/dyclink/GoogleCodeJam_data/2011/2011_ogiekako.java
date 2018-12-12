package _11R3;
import java.io.*;
import java.math.*;
import java.util.*;
import static java.lang.Math.*;
import static java.math.BigInteger.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;

public class ProgramA {
	static final boolean _PRACTICE = !true;
	static final boolean _SAMPLE = !true;
	static final boolean _SMALL = !true;
	static final String _PROBLEM = "A";

	void debug(Object... os) {
		System.err.println(deepToString(os));
	}

	void run() {
		Scanner sc = new Scanner(System.in);
		int oo = sc.nextInt();
		for (int o = 1; o <= oo; o++) {
			System.err.println(o);
			System.out.printf("Case #%d: ", o);
			System.out.println();
			solve(sc);
		}
	}

	private void solve(Scanner sc){
		W=sc.nextDouble();
		L=sc.nextInt();
		U=sc.nextInt();
		int G=sc.nextInt();
		ux=new double[U];
		uy=new double[U];
		lx=new double[L];
		ly=new double[L];
		for(int i=0;i<L;i++) {
			lx[i]=sc.nextDouble();ly[i]=sc.nextDouble()+1010;
		}
		for(int i=0;i<U;i++) {
			ux[i]=sc.nextDouble();uy[i]=sc.nextDouble()+1010;
		}
		double S = calc(W);
		for(int i=1;i<G;i++) {
			double left=0,right=W;
			for(int j=0;j<200;j++) {
				double mid = (left+right)/2;
				double ns = calc(mid);
				if(S/G*i < ns) {
					right = mid;
				}else {
					left = mid;
				}
			}
			System.out.println(right);
		}
	}
	private double calc(double right){
		return func(ux,uy,right) - func(lx,ly,right);
	}
	private double func(double[] xs,double[] ys,double right){
		int n=xs.length;
		double res=0;
		for(int i=0;i<n-1;i++) {
			if(right<xs[i])continue;
			if(right<xs[i+1]) {
				double a = (ys[i+1]-ys[i])/(xs[i+1]-xs[i]);
				double b = ys[i] - a*xs[i];
				double y = a*right + b;
				res += (ys[i]+y)*(right-xs[i])/2;
			}else {
				res += (ys[i]+ys[i+1])*(xs[i+1]-xs[i])/2;
			}
		}
		return res;
	}
	double W;
	int L,U;
	double[] ux,uy;
	double[] lx,ly;

	public static void main(String... args) throws IOException {
		if (!_SAMPLE) {
			if (_SMALL) {
				int i = 0;
				while (new File(_SMALLNAME(i) + ".in").exists())
					i++;
				i--;
				boolean test = false;
				if (new File(_SMALLNAME(i) + ".out").exists()) {
					System.err.println("overwrite?(y/n)");
					char c = (char) System.in.read();
					test = c != 'y';
				}
				if (test) {
					System.setIn(new FileInputStream(_SMALLNAME(i) + ".in"));
					System.setOut(new PrintStream(_PROBLEM + "-small-test.out"));
					new ProgramA().run();
					FileReader f1 = new FileReader(_PROBLEM + "-small-test.out");
					FileReader f2 = new FileReader(_SMALLNAME(i) + ".out");
					BufferedReader br1 = new BufferedReader(f1);
					BufferedReader br2 = new BufferedReader(f2);
					for (int j = 1;; j++) {
						String s1 = br1.readLine();
						String s2 = br2.readLine();
						if (s1 == null && s2 == null) {
							System.err.println("OK!");
							break;
						}
						if (s1 == null || s2 == null || !s1.equals(s2)) {
							System.err.println("failed at line " + j);
							System.err.println("expected " + s2);
							System.err.println("but " + s1);
							break;
						}
					}
				} else {
					System.setIn(new FileInputStream(_SMALLNAME(i) + ".in"));
					System.setOut(new PrintStream(_SMALLNAME(i) + ".out"));
					new ProgramA().run();
				}
			} else {
				System.setIn(new FileInputStream(_LARGENAME() + ".in"));
				System.setOut(new PrintStream(_LARGENAME() + ".out"));
				new ProgramA().run();
			}
		} else
			new ProgramA().run();
	}

	private static String _LARGENAME() {
		return _PROBLEM + "-large" + (_PRACTICE ? "-practice" : "");
	}

	private static String _SMALLNAME(int a) {
		return _PROBLEM + "-small" + (_PRACTICE ? a == 0 ? "-practice" : "" : "-attempt" + a);
	}
}
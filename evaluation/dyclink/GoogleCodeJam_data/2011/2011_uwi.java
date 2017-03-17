package gcj2011.r3;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class A {
	static String BASEPATH = "x:\\gcj\\2011r3\\";
	static boolean LARGE = true;
	static String INPATH = BASEPATH + A.class.getSimpleName().charAt(0) + (LARGE ? "-large.in" : "-small-attempt0.in");
	static String OUTPATH = INPATH.substring(0, INPATH.length()-3) + new SimpleDateFormat("-HHmmss").format(new Date()) + ".out";
	
	static String INPUT = "";
	
	static double[] lx, ly, ux, uy, ls, us;
	static int L, U;
	
	public void call()
	{
		int W = ni();
		L = ni();
		U = ni();
		int G = ni();
		
		lx = new double[L];
		ly = new double[L];
		ux = new double[U];
		uy = new double[U];
		for(int i = 0;i < L;i++){
			lx[i] = ni();
			ly[i] = ni();
		}
		for(int i = 0;i < U;i++){
			ux[i] = ni();
			uy[i] = ni();
		}
		ls = new double[L];
		us = new double[U];
		for(int i = 1;i <= L-1;i++){
			ls[i] = ls[i-1] + (ly[i] + ly[i-1]) / 2 * (lx[i] - lx[i-1]);
		}
		for(int i = 1;i <= U-1;i++){
			us[i] = us[i-1] + (uy[i] + uy[i-1]) / 2 * (ux[i] - ux[i-1]);
		}
		
		double all = us[U-1]-ls[L-1];
		out.println();
		
		for(int i = 0;i < G-1;i++){
			double low = 0;
			double high = W;
			double T = all / G * (i+1);
			while(high - low > 1E-9){
				double mid = (high+low)/2;
				if(S(mid) < T){
					low = mid;
				}else{
					high = mid;
				}
			}
			out.printf("%1.8f\n", low);
		}
	}
	
	static double S(double x)
	{
		double ret = 0;
		{
			int indl = Arrays.binarySearch(lx, x);
			if(indl < 0)indl = -indl-2;
			ret -= ls[indl];
			double yl = ly[indl]+(ly[indl+1]-ly[indl])*(x-lx[indl])/(lx[indl+1]-lx[indl]);
			ret -= (x - lx[indl]) * (ly[indl] + yl) / 2;
		}
		
		{
			int indu = Arrays.binarySearch(ux, x);
			if(indu < 0)indu = -indu-2;
			ret += us[indu];
			double yu = uy[indu]+(uy[indu+1]-uy[indu])*(x-ux[indu])/(ux[indu+1]-ux[indu]);
			ret += (x - ux[indu]) * (uy[indu] + yu) / 2;
		}
		
		return ret;
	}
	
	Scanner in;
	PrintWriter out;
	
	public A(Scanner in, PrintWriter out)
	{
		this.in = in;
		this.out = out;
	}
	
	int ni() { return Integer.parseInt(in.next()); }
	double nd() { return Double.parseDouble(in.next()); }
	void tr(Object... o) { if(INPUT.length() != 0)System.out.println(Arrays.deepToString(o)); }
	
	public static void main(String[] args) throws Exception
	{
		long start = System.currentTimeMillis();
		
		Scanner in = INPUT.isEmpty() ? new Scanner(new File(INPATH)) : new Scanner(INPUT);
		PrintWriter out = INPUT.isEmpty() ? new PrintWriter(new File(OUTPATH)) : new PrintWriter(System.out);
		int n = in.nextInt();
		in.nextLine();
		
		for(int i = 0;i < n;i++){
			out.printf("Case #%d: ", i+1);
			new A(in, out).call();
			out.flush();
			if(INPUT.isEmpty())System.err.println("case " + (i + 1) + " solved.\t");
		}
		
		long end = System.currentTimeMillis();
		System.out.println((end - start) + "ms");
	}
}

import java.io.*;
import java.util.*;

public class A {
	static int W,L,U,G;
	static int[] lowx, lowy, highx, highy;
	public static void main(String args[]) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("input.txt"));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("output.txt")));
		int inputs = Integer.parseInt(br.readLine());
		for(int m=0; m<inputs; m++) {
			String s = br.readLine();
			StringTokenizer st = new StringTokenizer(s);
			 W = Integer.parseInt(st.nextToken());
			 L = Integer.parseInt(st.nextToken());
			 U = Integer.parseInt(st.nextToken());
			 G = Integer.parseInt(st.nextToken());
			
			lowx = new int[L];
			lowy = new int[L];
			highx = new int[U];
			highy = new int[U];
			for(int i=0; i<L; i++) {
				s = br.readLine();
				st = new StringTokenizer(s);
				lowx[i] = Integer.parseInt(st.nextToken());
				lowy[i] = Integer.parseInt(st.nextToken());
			}
			for(int i=0; i<U; i++) {
				s = br.readLine();
				st = new StringTokenizer(s);
				highx[i] = Integer.parseInt(st.nextToken());
				highy[i] = Integer.parseInt(st.nextToken());
			}
			out.println("Case #"+(m+1)+":");
			double totalArea = area(0,W);
			double targetArea = totalArea/G;
			double curans = 0;
			for(int g=0; g<G-1; g++) {
				double low = curans;
				double high = W;
				while(low+0.0000000001<high) {
					double guess = (low+high)/2;
					double a = area(curans, guess);
					if(a<targetArea) {
						low = guess;
					} else {
						high = guess;
					}
				}
				curans = (low+high)/2;
				out.println(curans);
			}
		
			
		}
		out.close();	
	}
	
	static double area(double left, double right) {
		double ans = 2000*(right-left);
		for(int l=0; l<L-1; l++) {
			if(lowx[l+1]<left || lowx[l]>right) continue;
			double ax = Math.max(lowx[l], left);
			double bx = Math.min(lowx[l+1], right);
			double ay = lowy[l];
			double by = lowy[l+1];
			if(ax>lowx[l]) {
				ay = lowy[l] + (ax-lowx[l])/(lowx[l+1]-lowx[l])*(lowy[l+1]-lowy[l]);
			}
			if(bx<lowx[l+1]) {
				by = lowy[l+1] + (bx-lowx[l+1])/(lowx[l]-lowx[l+1])*(lowy[l]-lowy[l+1]);
			}
			ans-=(bx-ax)*((ay+by)/2+1000);
		}
		for(int l=0; l<U-1; l++) {
			if(highx[l+1]<left || highx[l]>right) continue;
			double ax = Math.max(highx[l], left);
			double bx = Math.min(highx[l+1], right);
			double ay = highy[l];
			double by = highy[l+1];
			if(ax>highx[l]) {
				ay = highy[l] + (ax-highx[l])/(highx[l+1]-highx[l])*(highy[l+1]-highy[l]);
			}
			if(bx<highx[l+1]) {
				by = highy[l+1] + (bx-highx[l+1])/(highx[l]-highx[l+1])*(highy[l]-highy[l+1]);
			}
			ans-=(bx-ax)*(1000-(ay+by)/2);
		}
		return ans;
	}
}

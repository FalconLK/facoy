import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import java.math.*;
import java.awt.geom.*;

public class A2 {
	static class Pair implements Comparable<Pair> {
		Integer a;
		Integer b;
		
		double yl, yh;
		int idx = -1;
		boolean up = false;
		public Pair(int a1, int b1) {
			a = a1;
			b = b1;
		}
		@Override
		public int compareTo(Pair p) {
			if (a.compareTo(p.a) != 0) {
				return a.compareTo(p.a);
			}
			return b.compareTo(p.b);
		}
		
		public String toString() {
			return "(" + a + ", " + b + ")";
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		Scanner sc = new Scanner(new File("A.in"));
		PrintWriter pw = new PrintWriter("A.out");
		int T = sc.nextInt();
		for (int test=1; test<=T; test++) {
			
			System.out.println(test);
			Vector<Pair> vl = new Vector<Pair>(); 
			Vector<Pair> vu = new Vector<Pair>();
			Vector<Pair> v = new Vector<Pair>();
			
			pw.println("Case #"+test+": ");
			
			int w = sc.nextInt();
			int l = sc.nextInt();
			int u = sc.nextInt();
			int g = sc.nextInt();
			
			double sl = 0;
			double su = 0;
			
			for (int i=0; i<l; i++) {
				int x = sc.nextInt();
				int y = sc.nextInt();
				
				Pair p = new Pair(x, y);
				p.up = false;
				p.idx = i;
				
				vl.add(p);
				
				if (i>0) {
					int xv = vl.get(i-1).a;
					int yv = vl.get(i-1).b;
					
					sl += (x - xv) * ((double)y + yv)/2; 
				}
			}
			
			for (int j=0; j<u; j++) {
				int x = sc.nextInt();
				int y = sc.nextInt();
				
				Pair p = new Pair(x, y);
				p.up = true;
				p.idx = j;
				vu.add(p);
				
				if (j>0) {
					int xv = vu.get(j-1).a;
					int yv = vu.get(j-1).b;
					
					su += (x - xv) * ((double)y + yv)/2; 
				}

			}
			
			v.addAll(vl);
			v.addAll(vu);
			Collections.sort(v);
			
			int ll=0;
			for (int i=0; i<u; i++) {
				Pair p = vu.get(i);
				int x = p.a;
				int y = p.b;
				p.yh = y;
				while (x > vl.get(ll+1).a) {
					ll++;
				}
				int x00 = vl.get(ll).a;
				int x01 = vl.get(ll+1).a;
				int y00 = vl.get(ll).b;
				int y01 = vl.get(ll+1).b;
				p.yl = ((double)(x-x00)) / (x01-x00) * (y01-y00) + y00;
				
			}

			int lu=0;
			for (int i=0; i<l; i++) {
				Pair p = vl.get(i);
				int x = p.a;
				int y = p.b;
				p.yl = y;
				while (x > vu.get(lu+1).a) {
					lu++;
				}
				int x00 = vu.get(lu).a;
				int x01 = vu.get(lu+1).a;
				int y00 = vu.get(lu).b;
				int y01 = vu.get(lu+1).b;
				p.yh = ((double)(x-x00)) / (x01-x00) * (y01-y00) + y00;
			}
			

			double s = su - sl;
			double s0 = s / g;
			
			for_i:
			for (int i=1; i<g; i++) {
				// find x with i*s0
				double st = i*s0;
				double scrt = 0;
				
				for (int k=0; k<l+u; k++) {
					Pair p1 = v.get(k);
					Pair p2 = v.get(k+1);
					
					int x0 = p1.a;
					int x1 = p2.a;
					double dy0 = p1.yh - p1.yl;
					double dy1 = p2.yh - p2.yl;
					
					double crt = (dy0+dy1) * (x1-x0) / 2; 
					if (crt + scrt > st) {
						double area = st - scrt;
						
						double xmin = x0;
						double xmax = x1;
						while (xmax - xmin > 0.00000001) {
							double xh = (xmin+xmax)/2;
							double yh = dy0 + (dy1-dy0) * ((double)(xh-x0))/(x1-x0);
							double sh = (yh+dy0) * (xh-x0)/2;
							if (sh > area) {
								xmax = xh;
							}
							else {
								xmin = xh;
							}
						}
						pw.println(xmin);
						continue for_i;
					}
					else {
						scrt += crt;
					}
				}

			}
		}
		pw.close();
	}
}

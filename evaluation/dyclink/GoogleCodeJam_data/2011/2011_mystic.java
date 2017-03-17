import java.util.*;
import java.math.*;
import java.io.*;

public class Solution {
	class Point {
		int x, y;
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	double area(Point[] p, double bnd) {
		double res = 0.0;
		
		for (int i=0; i + 1 < p.length; i++) {
			if (p[i].x >= bnd)
				continue;
			
			double x1 = p[i].x;
			double y1 = p[i].y;
			double x2 = p[i+1].x;
			double y2 = p[i+1].y;
			
			if (x2 > bnd) {
				double cf = (bnd - p[i].x) / (double)(p[i+1].x - p[i].x);
				x2 = bnd;
				y2 = p[i].y + (p[i+1].y - p[i].y) * cf;
			}
			
			res += (x2 - x1) * (y1 + y2) / 2.0;
		}
		
		return res;
	}
	
	public void go() throws Exception {
		Scanner sc = new Scanner(new FileReader("input.txt"));
		PrintWriter pw = new PrintWriter(new FileWriter("output.txt"));
		int caseCnt = sc.nextInt();
		for (int caseNum=1; caseNum <= caseCnt; caseNum++) {
			System.out.println("Processing case " + caseNum);
			pw.println("Case #" + caseNum + ":");
			
			int W = sc.nextInt();
			int L = sc.nextInt();
			int U = sc.nextInt();
			int G = sc.nextInt();
			
			Point[] lower = new Point[L];
			for (int i=0; i < L; i++)
				lower[i] = new Point(sc.nextInt(), sc.nextInt() + 1000);
			
			Point[] upper = new Point[U];
			for (int i=0; i < U; i++)
				upper[i] = new Point(sc.nextInt(), sc.nextInt() + 1000);
			
			double total = area(upper, W) - area(lower, W);
			
			for (int t=1; t < G; t++) {
				double need = total * t / (double)G;
				
				double l = 0.0, r = W;
				for (int it=0; it < 100; it++) {
					double mid = (l + r) / 2.0;
					double have = area(upper, mid) - area(lower, mid);
					if (have < need)
						l = mid;
					else
						r = mid;
				}
				
				pw.println((l + r) / 2.0);
			}
		}
		pw.flush();
		pw.close();
		sc.close();
	}
	
	public static void main(String[] args) throws Exception {
		(new Solution()).go();
	}
}
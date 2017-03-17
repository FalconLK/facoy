package codejam2011;

import java.awt.Point;
import java.io.*;
import java.util.*;

public class IrregularCakes {
    static Scanner in;
    static PrintWriter out;

    public static void main(String[] args) throws IOException {
        in = new Scanner(new File("inout\\A-small.in"));
        out = new PrintWriter(new FileWriter("inout\\result.out"));
        input();
        out.close();
        in.close();
    }

    static int caseNo = 0;

    static Point[] lower;
    static Point[] upper;
    static void input() throws IOException {
        int T = in.nextInt();
        for (int t=0; t<T; t++) {
            int W = in.nextInt();
            int L = in.nextInt();
            int U = in.nextInt();
            int G = in.nextInt();
            lower = new Point[L];
            for (int l=0; l<L; l++) {
                int x = in.nextInt();
                int y = in.nextInt();
                lower[l] = new Point(x,y);
            }
            upper = new Point[U];
            for (int u=0; u<U; u++) {
                int x = in.nextInt();
                int y = in.nextInt();
                upper[u] = new Point(x,y);
            }
            double total = 0;
            for (int i=0; i<L-1; i++) {
                total -= (lower[i+1].getY()+lower[i].getY())*(lower[i+1].getX()-lower[i].getX())/2;
            }
            for (int i=0; i<U-1; i++) {
                total += (upper[i+1].getY()+upper[i].getY())*(upper[i+1].getX()-upper[i].getX())/2;
            }
            out.println("Case #"+(++caseNo)+": ");
            for (int g=1; g<G; g++) {
                double part = g*total/G;
                double lb = 0;
                double ub = W;
                while (ub-lb > 0.000000001) {
                    double next = (ub+lb)/2;
                    double area = area(next);
                    if (area>part) {
                        ub = next;
                    } else {
                        lb = next;
                    }
                }
                out.println(lb);
            }
        }
    }

    static double area(double X) {
        double area = 0;
        for (int i=0; i<lower.length-1; i++) {
            Point l2 = lower[i+1];
            Point l1 = lower[i];
            double x1 = l1.x;
            double y1 = l1.y;
            double x2;
            double y2;
            if (X<=l1.x) {
                break;
            } else if (X<l2.x) {
                x2 = X;
                y2 = y1+(X-x1)*(l2.y-y1)/(double)(l2.x-x1);
            } else {
                x2 = l2.x;
                y2 = l2.y;
            }
            area -= (x2-x1)*(y2+y1)/2;
        }
        for (int i=0; i<upper.length-1; i++) {
            Point l2 = upper[i+1];
            Point l1 = upper[i];
            double x1 = l1.x;
            double y1 = l1.y;
            double x2;
            double y2;
            if (X<=l1.x) {
                break;
            } else if (X<l2.x) {
                x2 = X;
                y2 = y1+(X-x1)*(l2.y-y1)/(double)(l2.x-x1);
            } else {
                x2 = l2.x;
                y2 = l2.y;
            }
            area += (x2-x1)*(y2+y1)/2;
        }
        return area;
    }

}

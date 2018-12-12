package round3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class A {
    private static final double EPS = 0.00000000001;

    public void run(Scanner input, PrintWriter output) {
        int w = input.nextInt();
        int l = input.nextInt();
        int u = input.nextInt();
        int g = input.nextInt();

        int[] lx = new int[l];
        int[] ly = new int[l];

        for (int i = 0; i < l; i++) {
            lx[i] = input.nextInt();
            ly[i] = input.nextInt();
        }

        int[] ux = new int[u];
        int[] uy = new int[u];

        for (int i = 0; i < u; i++) {
            ux[i] = input.nextInt();
            uy[i] = input.nextInt();
        }

        output.println();
        double cx = 0;

        double s = calc(0, w, lx, ly, ux, uy) / g;

        for (int i = 0; i < g - 1; i++) {
            double _l = cx;
            double _r = w;

            while (Math.abs(_r - _l) > EPS) {
                double m = (_l + _r) / 2;
                if (calc(cx, m, lx, ly, ux, uy) < s) {
                    _l = m;
                } else {
                    _r = m;
                }
            }

            output.println(_l);
            cx = _l;
        }
    }

    private double trapS(double xl, double xr, double yl1, double yl2, double yr1, double yr2) {
        return (yl2 - yl1 + yr2 - yr1) / 2 * (xr - xl);
    }

    private double yAtX(double x, int[] xs, int[] ys) {
        // find between
        for (int i = 0; i < xs.length - 1; i++) {
            if (x >= xs[i] && x <= xs[i + 1]) {
                double k = 1.0 * (ys[i + 1] - ys[i]) / (xs[i + 1] - xs[i]);
                return ys[i] + (x - xs[i]) * k;
            }
        }
        throw new RuntimeException();
    }

    private double calc(double fromX, double toX, int[] lx, int[] ly, int[] ux, int[] uy) {
        double cx = fromX;

        double s = 0;

        while (cx < toX) {
            // find next x
            double nx = toX;

            for (int i = 0; i < lx.length; i++) {
                if (lx[i] > cx) {
                    nx = Math.min(nx, lx[i]);
                    break;
                }
            }

            for (int i = 0; i < ux.length; i++) {
                if (ux[i] > cx) {
                    nx = Math.min(nx, ux[i]);
                    break;
                }
            }

            s += trapS(cx, nx, yAtX(cx, lx, ly), yAtX(cx, ux, uy), yAtX(nx, lx, ly), yAtX(nx, ux, uy));

            cx = nx;
        }

        return s;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner input = new Scanner(new File("input.txt"));
        PrintWriter output = new PrintWriter(new File("output.txt"));

        A sol = new A();

        int t = input.nextInt();
        for (int i = 0; i < t; i++) {
            output.print("Case #" + (i + 1) + ": ");
            sol.run(input, output);
        }

        input.close();
        output.close();
    }
}

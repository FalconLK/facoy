import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class A {

    private int W;
    private int L;
    private int U;
    private int G;
    private int[] xl;
    private int[] yl;
    private int[] xu;
    private int[] yu;

    private String solve(Scanner in) {
        W = in.nextInt();
        L = in.nextInt();
        U = in.nextInt();
        G = in.nextInt();
        xl = new int[L];
        yl = new int[L];
        for (int i = 0; i < L; i++) {
            xl[i] = in.nextInt();
            yl[i] = in.nextInt();
        }
        xu = new int[U];
        yu = new int[U];
        for (int i = 0; i < U; i++) {
            xu[i] = in.nextInt();
            yu[i] = in.nextInt();
        }
        double ss = count(W);
        StringBuilder res = new StringBuilder();
        for (int i = 1; i < G; i++) {
            double s = ss * i / G;
            double ll = 0;
            double rr = W;
            while (rr > ll + 1e-9) {
                double mm = (ll + rr) / 2;
                double ms = count(mm);
                if (ms < s)
                    ll = mm;
                else
                    rr = mm;
            }
            res.append("\n");
            res.append(ll);
        }
        return res.toString();
    }

    private double count(double x) {
        int i = 0;
        int j = 0;
        double xx = 0;
        double res = 0;
        while (xx < x) {
            double xxx = Math.min(x, Math.min(xl[i + 1], xu[j + 1]));

            double h1 = calc(xu, yu, j, xx) - calc(xl, yl, i, xx);
            double h2 = calc(xu, yu, j, xxx) - calc(xl, yl, i, xxx);

            res += (h1 + h2) * (xxx - xx) / 2;

            if (xl[i + 1] == xxx) i++;
            if (xu[j + 1] == xxx) j++;
            xx = xxx;
        }
        return res;
    }

    private double calc(int[] x, int[] y, int i, double xx) {
        double x1 = x[i];
        double x2 = x[i + 1];
        double y1 = y[i];
        double y2 = y[i + 1];
        return y1 + (y2 - y1) * (xx - x1) / (x2 - x1);
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File("input.txt"));
        PrintWriter out = new PrintWriter("output.txt");
        int T = in.nextInt();
        for (int i = 0; i < T; i++) {
            String s = "Case #" + (i + 1) + ": " + new A().solve(in);
            out.println(s);
            System.out.println(s);
        }
        out.close();
    }

}
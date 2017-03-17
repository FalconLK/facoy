import java.util.*;
import java.io.*;

class A_as {
    Scanner in;
    PrintWriter out;

    public void solve() throws IOException {
        int testNo = in.nextInt();
        for (int test = 1; test <= testNo; test++) {
            int w = in.nextInt();
            int l = in.nextInt();
            int u = in.nextInt();
            int g = in.nextInt();

            double s = 0;

            int[] xl = new int[l];
            int[] yl = new int[l];
            for (int i = 0; i < l; i++) {
                xl[i] = in.nextInt();
                yl[i] = in.nextInt();
                if (i > 0) {
                    s -= (xl[i] - xl[i - 1]) * (yl[i] + yl[i - 1]) / 2.0;
                }
            }
            int[] xu = new int[u];
            int[] yu = new int[u];
            for (int i = 0; i < u; i++) {
                xu[i] = in.nextInt();
                yu[i] = in.nextInt();
                if (i > 0) {
                    s += (xu[i] - xu[i - 1]) * (yu[i] + yu[i - 1]) / 2.0;
                }
            }

            s /= g;

            out.println("Case #" + test + ":");
            for (int p = 1; p < g; p++) {
                double needs = s * p;
                double L = 0;
                double R = w;
                for (int iter = 0; iter < 100; iter++) {
                    double m = (L + R) / 2;

                    double sm = 0;
                    for (int i = 1; i < l; i++) {
                        double xp = xl[i];
                        double yp = yl[i];
                        if (xl[i] >= m) {
                            xp = m;
                            yp = yl[i - 1] + (yl[i] - yl[i - 1]) * (xp - xl[i - 1]) / (0.0 + xl[i] - xl[i - 1]);
                        }
                        sm -= (xp - xl[i - 1]) * (yp + yl[i - 1]) / 2.0;
                        if (xl[i] >= m) {
                            break;
                        }
                    }
                    for (int i = 1; i < u; i++) {
                        double xp = xu[i];
                        double yp = yu[i];
                        if (xu[i] >= m) {
                            xp = m;
                            yp = yu[i - 1] + (yu[i] - yu[i - 1]) * (xp - xu[i - 1]) / (0.0 + xu[i] - xu[i - 1]);
                        }
                        sm += (xp - xu[i - 1]) * (yp + yu[i - 1]) / 2.0;
                        if (xu[i] >= m) {
                            break;
                        }
                    }
                    if (sm > needs) {
                        R = m;
                    } else {
                        L = m;
                    }
                }
                out.printf("%.9f\n", L);
            }
        }
    }

    public void run() {
        try {
            in = new Scanner(new File("A-small-attempt0.in"));
            out = new PrintWriter(new File("A-small-attempt0.out"));

            solve();

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] arg) {
        Locale.setDefault(Locale.US);
        new A_as().run();
    }
}
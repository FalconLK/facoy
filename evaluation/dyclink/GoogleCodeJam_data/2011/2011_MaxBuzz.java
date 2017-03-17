import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class A {
    BufferedReader in;
    StringTokenizer st;
    PrintWriter out;

    static class Cake {
        private final int[] ux;
        private final int[] uy;
        private final int[] lx;
        private final int[] ly;
        private final double[] pu;
        private final double[] pl;

        public Cake(int[] lx, int[] ly, int[] ux, int[] uy) {
            this.lx = lx;
            this.ly = ly;
            this.ux = ux;
            this.uy = uy;

            pl = new double[lx.length];
            pu = new double[ux.length];
            for (int i = 1; i < pl.length; ++i) {
                pl[i] = pl[i - 1] + (ly[i - 1] + ly[i]) / 2.0 * (lx[i] - lx[i - 1]);
            }
            for (int i = 1; i < pu.length; ++i) {
                pu[i] = pu[i - 1] + (uy[i] + uy[i - 1]) / 2.0 * (ux[i] - ux[i - 1]);
            }
        }

        public double area(double slice) {
            double ans = 0;
            //lower
            {
                int l = 0, r = lx.length - 1;
                while (r - l > 1) {
                    int m = (l + r) >>> 1;
                    if (lx[m] <= slice) {
                        l = m;
                    } else {
                        r = m;
                    }
                }
                double y = (ly[r] - ly[l]) * (slice - lx[l]) / (lx[r] - lx[l]) + ly[l];
                ans -= pl[l];
                ans -= (ly[l] + y) / 2.0 * (slice - lx[l]);
            }
            //lower
            {
                int l = 0, r = ux.length - 1;
                while (r - l > 1) {
                    int m = (l + r) >>> 1;
                    if (ux[m] <= slice) {
                        l = m;
                    } else {
                        r = m;
                    }
                }
                double y = (uy[r] - uy[l]) * (slice - ux[l]) / (ux[r] - ux[l]) + uy[l];
                ans += pu[l];
                ans += (uy[l] + y) / 2.0 * (slice - ux[l]);
            }
            return ans;
        }
    }

    public void solveOne(String prefix) throws IOException {
        int w = nextInt();
        int l = nextInt();
        int u = nextInt();
        int g = nextInt();
        int[] lx = new int[l];
        int[] ly = new int[l];
        int[] ux = new int[u];
        int[] uy = new int[u];
        for (int i = 0; i < l; ++i) {
            lx[i] = nextInt();
            ly[i] = nextInt();
        }
        for (int i = 0; i < u; ++i) {
            ux[i] = nextInt();
            uy[i] = nextInt();
        }
        Cake cake = new Cake(lx, ly, ux, uy);
        double sum = cake.area(w);
        out.println(prefix);
        for (int i = 1; i < g; ++i) {
            double rate = sum / g * i;
            double left = 0, right = w;
            for (int it = 0; it < 30; ++it) {
                double mid = (left + right) / 2;
                if (cake.area(mid) > rate) {
                    right = mid;
                } else {
                    left = mid;
                }
            }
            out.println((left + right) / 2);
        }
    }

    public void run() throws IOException {
        in = new BufferedReader(new FileReader(A.class.getSimpleName() + ".in"));
        out = new PrintWriter(A.class.getSimpleName() + ".out");

        int nTests = nextInt();

        for (int i = 1; i <= nTests; ++i) {
            solveOne(String.format("Case #%d: ", i));
        }

        out.close();
        in.close();
    }

    String next() throws IOException {
        while (st == null || !st.hasMoreTokens()) {
            String line = in.readLine();
            if (line == null) {
                return null;
            }
            st = new StringTokenizer(line);
        }
        return st.nextToken();
    }

    int nextInt() throws IOException {
        return Integer.parseInt(next());
    }

    long nextLong() throws IOException {
        return Long.parseLong(next());
    }

    double nextDouble() throws IOException {
        return Double.parseDouble(next());
    }

    public static void main(String[] args) throws IOException {
        new A().run();
    }
}

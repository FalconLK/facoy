import java.io.*;
import java.util.*;
import java.math.*;

/**
 * User: Igor Kirov
 * Date: 11.06.11
 */
public class A implements Runnable {
    int w, l, u, g;
    Point[] a;
    Point[] b;

    class Point {
        double x, y;
    }

    private Point getStart(double x, Point p1, Point p2) {
        if (x < p1.x) {
            return p1;
        } else if (x > p2.x) {
            return null;
        } else {
            double x1 = p1.x;
            double y1 = p1.y;
            double x2 = p2.x;
            double y2 = p2.y;
            double y = (x * (y2 - y1) - x1 * y2 + y1 * x2) / (x2 - x1);
            Point ret = new Point();
            ret.x = x;
            ret.y = y;
            return ret;
        }
    }

    private Point getEnd(double x, Point p1, Point p2) {
        if (x > p2.x) {
            return p2;
        } else if (x < p1.x) {
            return null;
        } else {
            double x1 = p1.x;
            double y1 = p1.y;
            double x2 = p2.x;
            double y2 = p2.y;
            double y = (x * (y2 - y1) - x1 * y2 + y1 * x2) / (x2 - x1);
            Point ret = new Point();
            ret.x = x;
            ret.y = y;
            return ret;
        }
    }

    private double calcS(double left, double right) {
        double s1 = 0;
        for (int i = 0; i < l - 1; i++) {
            Point p1 = getStart(left, a[i], a[i + 1]);
            Point p2 = getEnd(right, a[i], a[i + 1]);
            if (p1 == null || p2 == null)
            {
                continue;
            }
            s1 += 0.5 * (p2.x * p1.y + p2.x * p2.y - p1.x * p1.y - p1.x * p2.y);
        }

        double s2 = 0;
        for (int i = 0; i<u-1; i++)
        {
            Point p1 = getStart(left, b[i], b[i + 1]);
            Point p2 = getEnd(right, b[i], b[i + 1]);
            if (p1 == null || p2 == null)
            {
                continue;
            }
            s2 += 0.5 * (p2.x * p1.y + p2.x * p2.y - p1.x * p1.y - p1.x * p2.y);
        }

        return s2 - s1;
    }

    private void solve() throws IOException {
        w = nextInt();
        l = nextInt();
        u = nextInt();
        g = nextInt();

        a = new Point[l];
        b = new Point[u];

        for (int i = 0; i < l; i++) {
            a[i] = new Point();
            a[i].x = nextInt();
            a[i].y = nextInt();
        }

        for (int i = 0; i < u; i++) {
            b[i] = new Point();
            b[i].x = nextInt();
            b[i].y = nextInt();
        }

        double square = calcS(0, w);
        double part = square / g;

        double left = 0;
        double right = w;
        double curl = 0;

        writer.println();
        for (int i = 0; i<g-1; i++)
        {
            double mid = 0;
            while (right - left > 1e-12)
            {
                mid = (right + left) / 2;
                double s = calcS(curl, mid);
                if (Math.abs(s-part) < 1e-12)
                {
                    break;
                }
                if (s > part)
                {
                    right = mid;
                }
                else
                {
                    left = mid;
                }
            }
            curl = mid;
            right = w;
            writer.printf("%.6f\n", mid);
        }
    }

    public static void main(String[] args) throws IOException {
        new Thread(null, new A(), "", 64 * 1024 * 1024).start();
    }

    StringTokenizer tokenizer;
    BufferedReader reader;
    PrintWriter writer;

    public void run() {
        try {
            try {
                Locale.setDefault(Locale.US);
            } catch (Exception ignored) {
            }

            reader = new BufferedReader(new FileReader("A.in"));
            writer = new PrintWriter(new FileWriter("A.out"));
            tokenizer = null;

            int tests = nextInt();
            for (int i = 0; i < tests; i++) {
                writer.printf("Case #%d: ", i + 1);
                solve();
                writer.flush();
            }

            reader.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private int nextInt() throws IOException {
        return Integer.parseInt(nextToken());
    }

    private long nextLong() throws IOException {
        return Long.parseLong(nextToken());
    }

    private BigInteger nextBigInteger() throws IOException {
        return new BigInteger(nextToken());
    }

    private double nextDouble() throws IOException {
        return Double.parseDouble(nextToken());
    }

    private String nextToken() throws IOException {
        while (tokenizer == null || !tokenizer.hasMoreTokens()) {
            tokenizer = new StringTokenizer(reader.readLine());
        }
        return tokenizer.nextToken();
    }
}
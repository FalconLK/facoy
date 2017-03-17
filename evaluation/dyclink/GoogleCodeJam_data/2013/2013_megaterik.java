
import java.awt.Point;
import java.io.*;
import java.math.BigInteger;
import java.util.*;
import static java.lang.Math.*;

public class Cheaters implements Runnable {

    BufferedReader in;
    PrintWriter out;
    StringTokenizer tok = new StringTokenizer("");

    public static void main(String[] args) {
        new Thread(null, new Cheaters(), "", 256 * (1L << 20)).start();
    }

    public void run() {
        try {
            long t1 = System.currentTimeMillis();
            out = new PrintWriter(System.out);
            in = new BufferedReader(new FileReader("src/A-large.in"));
            out = new PrintWriter("src/A-large.out");
            solve();
            in.close();
            out.close();
            long t2 = System.currentTimeMillis();
            System.err.println("Time = " + (t2 - t1));
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    String readString() throws IOException {
        while (!tok.hasMoreTokens()) {
            tok = new StringTokenizer(in.readLine());
        }
        return tok.nextToken();
    }

    int readInt() throws IOException {
        return Integer.parseInt(readString());
    }

    long readLong() throws IOException {
        return Long.parseLong(readString());
    }

    double readDouble() throws IOException {
        return Double.parseDouble(readString());
    }

    // solution
    void solve() throws IOException {
        int n = readInt();
        for (int i = 1; i <= n; i++) {
            solveTestCase(i);
        }
    }
    static final long INF = 1000000000000000L;

    void solveTestCase(int testcase) throws IOException {
        long money = readLong();
        int n = readInt();
        long[] a = new long[37];
        for (int i = 0; i < n; i++) {
            a[i] = readLong();
        }
        long l = 0;
        long r = INF;
        while (l < r - 1) {
            long m = (l + r) / 2;
            if (isPossible(a, m, money)) {
                l = m;
            } else {
                r = m;
            }
        }
        double max = 0;
        for (long i = l; i >= 1 && i > l - 100; i--) {
            max = Math.max(max, getProfit(money, i, a));
        }
        // out.println("possible " + l);
        out.print("Case #" + testcase + ": ");
        out.printf("%f\n", max);
        //out.println(max);
        out.flush();
    }

    private double getProfit(long money, long l, long[] a) {
        double startResult = -money;
        ArrayList<Long> profit = new ArrayList<>();
        for (long x : a) {
            if (x < l) {
                money -= (l - x);
                profit.add(l - x);
            } else if (x == l) {
                profit.add(0L);
            }
        }
        //  out.println("get profit " + l + " " + money);
        Collections.sort(profit);
        double bestResult = 0;
        while (profit.size() > 0 && money >= 0) {
            double result = startResult + money;//remaining
            for (int i = 0; i < profit.size(); i++) {
                result += 36.0 * (profit.get(i)) * (1.0 / profit.size());
            }
            profit.remove(0);
            money--;
            bestResult = Math.max(bestResult, result);
        }

        return bestResult;
    }

    private boolean isPossible(long[] a, long m, long money) {
        for (long value : a) {
            if (value < m) {
                money -= (m - value);
            }
        }
        return money >= 0;
    }
}
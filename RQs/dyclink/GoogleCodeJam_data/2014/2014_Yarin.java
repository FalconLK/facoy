package round3;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;

class Kattio extends PrintWriter {
    public Kattio(InputStream i) {
        super(new BufferedOutputStream(System.out));
        r = new BufferedReader(new InputStreamReader(i));
    }

    public Kattio(InputStream i, OutputStream o) {
        super(new BufferedOutputStream(o));
        r = new BufferedReader(new InputStreamReader(i));
    }

    public boolean hasMoreTokens() {
        return peekToken() != null;
    }

    public int getInt() {
        return Integer.parseInt(nextToken());
    }

    public double getDouble() {
        return Double.parseDouble(nextToken());
    }

    public long getLong() {
        return Long.parseLong(nextToken());
    }

    public String getWord() {
        return nextToken();
    }


    private BufferedReader r;
    private String line;
    private StringTokenizer st;
    private String token;

    private String peekToken() {
        if (token == null)
            try {
                while (st == null || !st.hasMoreTokens()) {
                    line = r.readLine();
                    if (line == null) return null;
                    st = new StringTokenizer(line);
                }
                token = st.nextToken();
            } catch (IOException e) {
            }
        return token;
    }

    private String nextToken() {
        String ans = peekToken();
        token = null;
        return ans;
    }
}


public class A {
    public static void main(String[] args) throws FileNotFoundException {
        Kattio io;

//        io = new Kattio(System.in, System.out);
//        io = new Kattio(new FileInputStream("round3/A-sample.in"), System.out);
//        io = new Kattio(new FileInputStream("round3/A-small-0.in"), new FileOutputStream("round3/A-small-0.out"));
        io = new Kattio(new FileInputStream("round3/A-large-0.in"), new FileOutputStream("round3/A-large-0.out"));

        int cases = io.getInt();
        for (int i = 1; i <= cases; i++) {
            io.print("Case #" + i + ": ");
            new A().solve(io);
        }
        io.close();
    }

    long a[], sum[];
    double best;

    private void check(int left, int right) {
        if (left > right) return;
        if (left < 0) return;
        long ival1 = sum[left];
        long ival2 = sum[right+1] - ival1;
        long ival3 = sum[sum.length - 1] - sum[right + 1];
        if (ival1+ival2+ival3!=sum[sum.length-1]) throw new RuntimeException();

        long sig = Math.max(ival1, Math.max(ival2, ival3));
        double arnar = sum[sum.length - 1] - sig;
        best = Math.max(best, arnar / (arnar + sig));
    }

    private void solve(Kattio io) {
        int N = io.getInt(), p = io.getInt(), q = io.getInt(), r = io.getInt(), s = io.getInt();
        a = new long[N];
        sum = new long[N + 1];
        for (int i = 0; i < N; i++) {
            a[i] = (((i * (long) p) + q) % r + s);
            sum[i+1] = sum[i] + a[i];
        }
        best = 0.0;
        for (int i = 0; i < N; i++) {
            long right = sum[N] - sum[i+1];

            int lo = 0, hi = N;
            while (lo < hi) {
                int j = (lo+hi)/2;
                if (sum[j] < right) lo = j + 1; else hi = j;
            }
            check(lo-2, i);
            check(lo-1, i);
            check(lo, i);
            check(lo+1, i);
            check(lo+2, i);
        }
        io.println(best);
    }
}

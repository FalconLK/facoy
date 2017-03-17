import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Locale;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.InputStream;

/**
 * Built using CHelper plug-in
 * Actual solution is at the top
 * @author Niyaz Nigmatullin
 */
public class Main {
	public static void main(String[] args) {
		InputStream inputStream;
		try {
			inputStream = new FileInputStream("taska2.in");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream("taska2.out");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		FastScanner in = new FastScanner(inputStream);
		FastPrinter out = new FastPrinter(outputStream);
		TaskA2 solver = new TaskA2();
		int testCount = Integer.parseInt(in.next());
		for (int i = 1; i <= testCount; i++)
			solver.solve(i, in, out);
		out.close();
	}
}

class TaskA2 {
    public void solve(int testNumber, FastScanner in, FastPrinter out) {
        out.print("Case #" + testNumber + ": ");
        System.err.println("[" + testNumber + "]");
        long b = in.nextLong();
        int n = in.nextInt();
        double ans = 0;
        long[] a = new long[37];
        for (int i = 0; i < n; i++) a[i] = in.nextLong();
        Arrays.sort(a);
        for (int i = a.length - 1; i >= 0; i--) a[i] -= a[0];
        for (int win = 1; win <= 35; win++) {
            if (!ok(a, win, a[win - 1], b)) {
                continue;
            }
            long l = a[win - 1];
            long r = b + 1;
            while (l < r - 1) {
                long mid = (l + r) >>> 1;
                if (ok(a, win, mid, b)) {
                    l = mid;
                } else {
                    r = mid;
                }
            }
            long cur = 0;
            for (int i = 0; i < win; i++) {
                cur += l - a[i];
            }
            double sum = cur;
            for (int i = win; i < 35; i++) {
                if (a[i] <= l) {
                    cur += l - a[i] + 1;
                }
            }
//            System.out.println(sum + " " + win + " " + cur + " " + l + " " + (36 * sum / win - cur));
            ans = Math.max(ans, 36 * sum / win - cur);
        }
        out.printf(Locale.US, "%.17f\n", ans);
    }

    boolean ok(long[] a, int k, long x, long b) {
        long cur = 0;
        for (int i = 0; i < k; i++) {
            if (a[i] > x) throw new AssertionError();
            cur += x - a[i];
            if (cur > b) return false;
        }
        for (int i = k; i < 35; i++) {
            if (a[i] <= x) {
                cur += x - a[i] + 1;
                if (cur > b) return false;
            }
        }
        if (a[35] <= x || a[36] <= x) return false;
        return cur <= b;
    }
}

class FastScanner extends BufferedReader {

    public FastScanner(InputStream is) {
        super(new InputStreamReader(is));
    }

    public int read() {
        try {
            int ret = super.read();
//            if (isEOF && ret < 0) {
//                throw new InputMismatchException();
//            }
//            isEOF = ret == -1;
            return ret;
        } catch (IOException e) {
            throw new InputMismatchException();
        }
    }

    public String next() {
        StringBuilder sb = new StringBuilder();
        int c = read();
        while (isWhiteSpace(c)) {
            c = read();
        }
        if (c < 0) {
            return null;
        }
        while (c >= 0 && !isWhiteSpace(c)) {
            sb.appendCodePoint(c);
            c = read();
        }
        return sb.toString();
    }

    static boolean isWhiteSpace(int c) {
        return c >= 0 && c <= 32;
    }

    public int nextInt() {
        int c = read();
        while (isWhiteSpace(c)) {
            c = read();
        }
        int sgn = 1;
        if (c == '-') {
            sgn = -1;
            c = read();
        }
        int ret = 0;
        while (c >= 0 && !isWhiteSpace(c)) {
            if (c < '0' || c > '9') {
                throw new NumberFormatException("digit expected " + (char) c
                        + " found");
            }
            ret = ret * 10 + c - '0';
            c = read();
        }
        return ret * sgn;
    }

    public long nextLong() {
        return Long.parseLong(next());
    }

    public String readLine() {
        try {
            return super.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    }

class FastPrinter extends PrintWriter {

    public FastPrinter(OutputStream out) {
        super(out);
    }

    public FastPrinter(Writer out) {
        super(out);
    }


}


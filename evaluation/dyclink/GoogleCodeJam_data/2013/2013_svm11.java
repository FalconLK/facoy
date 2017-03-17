import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.util.InputMismatchException;
import java.io.InputStream;

/**
 * Built using CHelper plug-in
 * Actual solution is at the top
 * @author Vaibhav Mittal
 */
public class Main {
	public static void main(String[] args) {
		InputStream inputStream;
		try {
			inputStream = new FileInputStream("A-small-attempt2.in");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream("output.txt");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		InputReader in = new InputReader(inputStream);
		PrintWriter out = new PrintWriter(outputStream);
		Cheaters solver = new Cheaters();
		int testCount = Integer.parseInt(in.next());
		for (int i = 1; i <= testCount; i++)
			solver.solve(i, in, out);
		out.close();
	}
}

class Cheaters {
    public void solve(int testNumber, InputReader in, PrintWriter out) {
        int b = in.readInt();
        int n = in.readInt();
        int[] x = new int[37];
        for (int i = 0; i < n; ++i)
            x[i] = in.readInt();
        Arrays.sort(x);
        int[] xx = x.clone();
        double res = 0;
        for (int i = 1; i <= b; ++i) {
            ++x[0];
            Arrays.sort(x);
            res = Math.max(res, profit(x, xx, i));
        }
        out.printf("Case #%d: %.12f\n", testNumber, res);
    }

    public double profit(int[] x, int[] xx, int p) {
        int cnt = 0;
        for (int tmp : x) if (tmp == x[0]) ++cnt;
        double res = 0;
        for (int i = 0; i < x.length; ++i)
            if (x[i] == x[0])
                res += Math.max((x[i] - xx[i]) * 36.0 / cnt, 0.0);
        return Math.max(res - p, 0);
    }
}

class InputReader {
    private InputStream stream;
    private byte[] buf = new byte[1024];
    private int curChar;
    private int numChars;
    private SpaceCharFilter filter;

    public InputReader(InputStream stream) {
        this.stream = stream;
    }

    private int read() {
        if (numChars == -1)
            throw new InputMismatchException();
        if (curChar >= numChars) {
            curChar = 0;
            try {
                numChars = stream.read(buf);
            } catch (IOException e) {
                throw new InputMismatchException();
            }
            if (numChars <= 0)
                return -1;
        }
        return buf[curChar++];
    }

    public int readInt() {
        int c = read();
        while (isSpaceChar(c))
            c = read();
        int sgn = 1;
        if (c == '-') {
            sgn = -1;
            c = read();
        }
        int res = 0;
        do {
            if (c < '0' || c > '9')
                throw new InputMismatchException();
            res *= 10;
            res += c - '0';
            c = read();
        } while (!isSpaceChar(c));
        return res * sgn;
    }

    public String readString() {
        int c = read();
        while (isSpaceChar(c))
            c = read();
        StringBuilder res = new StringBuilder();
        do {
            res.appendCodePoint(c);
            c = read();
        } while (!isSpaceChar(c));
        return res.toString();
    }

    public boolean isSpaceChar(int c) {
        if (filter != null)
            return filter.isSpaceChar(c);
        return isWhitespace(c);
    }

    public static boolean isWhitespace(int c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == -1;
    }

    public String next() {
        return readString();
    }

    public interface SpaceCharFilter {
        public boolean isSpaceChar(int ch);
    }
}


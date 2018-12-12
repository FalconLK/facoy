import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.util.Locale;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.File;
import java.io.Writer;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.math.BigInteger;
import java.io.InputStream;

/**
 * Built using CHelper plug-in
 * Actual solution is at the top
 * @author ogiekako
 */
public class Main {
	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		InputStream inputStream;
		try {
			final String regex = "A-(small|large).*[.]in";
			File directory = new File(".");
			File[] candidates = directory.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.matches(regex);
				}
			});
			File toRun = null;
			for (File candidate : candidates) {
				if (toRun == null || candidate.lastModified() > toRun.lastModified())
					toRun = candidate;
			}
			inputStream = new FileInputStream(toRun);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream("a.out");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		MyScanner in = new MyScanner(inputStream);
		MyPrintWriter out = new MyPrintWriter(outputStream);
		TaskA solver = new TaskA();
		int testCount = Integer.parseInt(in.next());
		for (int i = 1; i <= testCount; i++)
			solver.solve(i, in, out);
		out.close();
	}
}

class TaskA {
    public void solve(int testNumber, MyScanner in, MyPrintWriter out) {
        double res = 0;
        long B = in.nextLong();
        int N = in.nextInt();
        long[] is = new long[37];
        for (int i = 0; i < N; i++) is[i] = in.nextLong();
        N = 37;
        Arrays.sort(is);
        long A = 0;
//        for(long i:is)B += i;
        long K = 0;

        TreeSet<Long> cands = new TreeSet<Long>();
        for(int i=0;i<N;i++)for(int d=-1;d<=1;d++)cands.add(is[i] + d);

        for (int n = 1; n <= N; n++) {
            if (n > 0) {
                A += is[n - 1];
                K = is[n - 1];
            }

            long left=K, right= (long) (1.05 * 1e12);
            do{
                long m1 = (left * 2 + right) / 3;
                long m2 = (left + right * 2) / 3;
                double e1 = calc(n,m1,A,N,B,is);
                double e2 = calc(n,m2,A,N,B,is);
                if(e1 < e2)left = m1;
                else right = m2;
            }while(right - left > 3);

            for(long k=left;k<=right;k++){
                res = Math.max(res, calc(n,k,A,N,B,is));
            }
        }
        out.printFormat("Case #%d: %.09f\n", testNumber, res);
    }

    double calc(int n,long k,long A,int N,long B,long[] is)   {
        long X = k * n - A;
        long Y = 0;
        for (int i = n; i < N; i++) Y += Math.max(0, k + 1 - is[i]);
        if(X+Y > B)return -1;
        return (double) 36 / n * X - (X + Y);
    }


    }

class MyScanner {
    private final InputStream in;

    public MyScanner(InputStream in) {
        this.in = in;
    }

    int bufLen;
    int bufPtr;
    byte[] buf = new byte[1024];

    public int read() {
        if (bufLen == -1)
            throw new InputMismatchException();
        if (bufPtr >= bufLen) {
            bufPtr = 0;
            try {
                bufLen = in.read(buf);
            } catch (IOException e) {
                throw new InputMismatchException();
            }
            if (bufLen <= 0)
                return -1;
        }
        return buf[bufPtr++];
    }

    private char[] strBuf = new char[65536];

    public String next() {
        int strLen = 0;
        int c;
        do {
            c = read();
            if (c == -1) throw new NoSuchElementException();
        } while (Character.isWhitespace(c));
        do {
            if (strLen + 1 >= strBuf.length) {
                char[] tmp = new char[strBuf.length * 2];
                System.arraycopy(strBuf, 0, tmp, 0, strBuf.length);
                strBuf = tmp;
            }
            strBuf[strLen++] = (char) c;
            c = read();
        } while (c != -1 && !Character.isWhitespace(c));
        return new String(strBuf, 0, strLen);
    }

    public int nextInt() {
        int c = read();
        if (c == -1) throw new NoSuchElementException();
        while (c != '-' && (c < '0' || '9' < c)) {
            c = read();
            if (c == -1) throw new NoSuchElementException();
        }
        if (c == '-') return -nextInt();
        int res = 0;
        do {
            res *= 10;
            res += c - '0';
            c = read();
        } while ('0' <= c && c <= '9');
        return res;
    }

    public long nextLong() {
        try {
            int c = read();
            if (c == -1) return c;
            while (c != '-' && (c < '0' || '9' < c)) {
                c = read();
                if (c == -1) return c;
            }
            if (c == '-') return -nextLong();
            long res = 0;
            do {
                res *= 10;
                res += c - '0';
                c = read();
            } while ('0' <= c && c <= '9');
            return res;
        } catch (Exception e) {
            return -1;
        }
    }


    }

class MyPrintWriter {
    PrintWriter out;

    public MyPrintWriter(OutputStream outputStream) {
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)));
    }

    public MyPrintWriter(Writer writer) {
        out = new PrintWriter(writer);
    }

    public void close() {
        out.close();
    }

    public void printFormat(String format, Object... args) {
        out.printf(format, args);
    }

}


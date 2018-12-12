import java.io.Closeable;
import java.io.BufferedReader;
import java.util.Comparator;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.io.InputStream;

/**
 * Built using CHelper plug-in
 * Actual solution is at the top
 * @author Jacob Jiang
 */
public class Main {
	public static void main(String[] args) {
		InputStream inputStream;
		try {
			inputStream = new FileInputStream("A-large.in");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream("A-large.out");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		QuickScanner in = new QuickScanner(inputStream);
		ExtendedPrintWriter out = new ExtendedPrintWriter(outputStream);
		TaskA solver = new TaskA();
		int testCount = Integer.parseInt(in.next());
		for (int i = 1; i <= testCount; i++)
			solver.solve(i, in, out);
		out.close();
	}
}

class TaskA {
	public void solve(final int testNumber, QuickScanner in, ExtendedPrintWriter out) {
        int n = in.nextInt();
        final int[] L = in.nextIntArray(n);
        final int[] P = in.nextIntArray(n);
        Integer[] answer = new Integer[n];
        for (int i = 0; i < n; i++) {
            answer[i] = i;
        }
        Arrays.sort(answer, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(L[o1] * P[o2], L[o2] * P[o1]);
            }
        });
        out.print("Case #" + testNumber + ": ");
        out.printLine(answer);
	}
}

class QuickScanner implements Iterator<String>, Closeable {
    BufferedReader reader;
    StringTokenizer tokenizer;
    boolean endOfFile = false;

    public QuickScanner(InputStream inputStream){
        reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            tokenizer = new StringTokenizer(reader.readLine());
        } catch (Exception e) {
            endOfFile = true;
        }
    }

    public boolean hasNext() {
        if (!tokenizer.hasMoreTokens()) {
            try {
                checkNext();
            } catch (NoSuchElementException ignored) {
            }

        }
        return !endOfFile;
    }

    private void checkNext() {
        if (endOfFile) {
            throw new NoSuchElementException();
        }
        try {
            while (!tokenizer.hasMoreTokens()) {
                tokenizer = new StringTokenizer(reader.readLine());
            }
        } catch (Exception e) {
            endOfFile = true;
            throw new NoSuchElementException();
        }
    }

    public String next() {
        checkNext();
        return tokenizer.nextToken();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public int nextInt() {
        return Integer.parseInt(next());
    }

    public int[] nextIntArray(int count) {
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = nextInt();
        }
        return result;
    }

    public void close() {
        try {
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

class ExtendedPrintWriter extends PrintWriter {


    public ExtendedPrintWriter(Writer out) {
        super(out);
    }

    public ExtendedPrintWriter(Writer out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public ExtendedPrintWriter(OutputStream out) {
        super(out);
    }

    public ExtendedPrintWriter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public void printItems(Object... items) {
        for (int i = 0; i < items.length; i++) {
            if (i != 0) {
                print(' ');
            }
            print(items[i]);
        }
    }

    public void printLine(Object... items) {
        printItems(items);
        println();
    }

    }


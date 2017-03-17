import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.concurrent.*;

public class A {
    private static String FILENAME = null;
    static {
        //FILENAME = "A-sample";
        //FILENAME = "A-small";
        FILENAME = "A-large";
    }

    public static void main(String[] args) {
        new A().run();
    }

    private PrintStream out;
    private final BufferedReader reader;
    private StringTokenizer tokenizer = new StringTokenizer("");

    public A() {
        try {
            if (FILENAME == null) {
                out = System.out;
                reader = new BufferedReader(new InputStreamReader(System.in));
            } else {
                out = new PrintStream(new FileOutputStream("source/" + FILENAME + ".out"));
                reader = new BufferedReader(new FileReader("source/" + FILENAME + ".in"));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try {
            runCases();
        } finally {
            out.close();
        }
    }

    public void debug(String s, Object... args) {
        System.err.printf("DEBUG: " + s + "\n", args);
    }

    private void runCases() {
        int numProcs = Runtime.getRuntime().availableProcessors();
        debug("num processors: %d", numProcs);
        ExecutorService service = Executors.newFixedThreadPool(numProcs);
        try {
            int cases = getInt();
            ArrayList<Future<String>> list = new ArrayList<Future<String>>();
            for (int c = 1; c <= cases; c++) {
                Solver solver = new Solver(c);
                list.add(service.submit(solver));
            }
            for (int c = 1; c <= cases; c++) {
                Future<String> future = list.get(c - 1);
                try {
                    String s = "Case #" + c + ": " + future.get();
                    out.println(s);
                    if (out != System.out) {
                        System.out.println(s);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.getCause().printStackTrace();
                }
            }
        } finally {
            service.shutdown();
            debug("done with all!");
        }
    }

    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getToken() {
        while (true) {
            if (tokenizer.hasMoreTokens()) {
                return tokenizer.nextToken();
            }
            String s = readLine();
            if (s == null) {
                return null;
            }
            tokenizer = new StringTokenizer(s, " \t\n\r");
        }
    }

    public double getDouble() {
        return Double.parseDouble(getToken());
    }

    public int getInt() {
        return Integer.parseInt(getToken());
    }

    public long getLong() {
        return Long.parseLong(getToken());
    }

    public BigInteger getBigInt() {
        return new BigInteger(getToken());
    }

    public BigDecimal getBigDec() {
        return new BigDecimal(getToken());
    }

    public class Solver implements Callable<String> {

        private final int N;
        private int caseNumber;
        private int[] L;
        private int[] P;

        // Do all input reading here!!
        public Solver(int caseNumber) {
            this.caseNumber = caseNumber;
            N = getInt();
            L = new int[N];
            P = new int[N];
            for (int i = 0; i < N; i++) {
                L[i] = getInt();
            }
            for (int i = 0; i < N; i++) {
                P[i] = getInt();
            }
        }

        // Do no reading here! This is run async!
        // Solve the actual problem here
        public String call() throws Exception {
            debug("solving case %d", caseNumber);
            Obj[] o = new Obj[N];
            for (int i = 0; i < N; i++) {
                o[i] = new Obj(L[i], P[i], i);
            }
            Arrays.sort(o, new Comparator<Obj>() {
                public int compare(Obj obj, Obj obj1) {
                    int v1 = obj1.P * obj.L;
                    int v2 = obj.P * obj1.L;
                    int v = (v1 - v2);
                    if (v != 0) {
                        return v;
                    }
                    return obj.i - obj1.i;
                }
            });
            String s= "";
            for (Obj o2 : o) {
                if (s != "") {
                    s += " ";
                }
                s += o2.i;
            }
            return s;
        }

        private class Obj {
            private final int L;
            private final int P;
            private final int i;

            private Obj(int l, int p, int i) {
                L = l;
                P = p;
                this.i = i;
            }
        }
    }
}

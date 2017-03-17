import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * @author Ivan Romanov
 */
public class A {

    private static final String PROBLEM = "A-large";

    private static class Level implements Comparable<Level> {
        private final int p;
        private final int l;
        private final int idx;
        private final double time;

        public Level(int p, int l, int idx) {
            this.p = p;
            this.l = l;
            this.idx = idx;
            this.time = p == 0 ? Double.POSITIVE_INFINITY : l * 100.0 / p;
        }

        public int compareTo(Level that) {
            return Double.compare(this.time, that.time);
        }
    }

    private String solve() throws IOException {
        int n = nextInt();
        int[] p = new int[n];
        int[] l = new int[n];
        for (int i = 0; i < n; i++) {
            l[i] = nextInt();
        }
        for (int i = 0; i < n; i++) {
            p[i] = nextInt();
        }
        Level[] a = new Level[n];
        for (int i = 0; i < n; i++) {
            a[i] = new Level(p[i], l[i], i);
        }
        Arrays.sort(a);
        StringBuilder sb = new StringBuilder();
        for (Level level : a) {
            sb.append(" ").append(level.idx);
        }
        return sb.toString();
    }

    private BufferedReader reader;

    private StringTokenizer tt = new StringTokenizer("");

    private String nextToken() throws IOException {
        while (!tt.hasMoreTokens()) {
            tt = new StringTokenizer(reader.readLine());
        }
        return tt.nextToken();
    }

    private int nextInt() throws IOException {
        return Integer.parseInt(nextToken());
    }

    private void run() throws IOException {
        reader = new BufferedReader(new FileReader(PROBLEM + ".in"));
        PrintWriter writer = new PrintWriter(new File(PROBLEM + ".out"));
        try {
            int n = nextInt();
            for (int tc = 1; tc <= n; tc++) {
                writer.print("Case #" + tc + ":");
                writer.println(solve());
            }
        } finally {
            reader.close();
            writer.close();
        }
    }

    public static void main(String[] args) {
        try {
            new A().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
import java.io.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;

public class ProblemA {

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.UK);
        new ProblemA();
    }

    private BufferedReader in = null;
    private PrintWriter out = null;
    private StringTokenizer tok = new StringTokenizer("");
    private String fileName = "A-small-attempt3";

    public ProblemA() throws Exception {
        init();
        solve();
        in.close();
        out.close();
    }

    private void solve() throws Exception {
        int T = nextInt();
        for (int ii = 1; ii <= T; ++ii) {
            out.print("Case #" + ii + ": ");
            long b = nextLong();
            int n = nextInt();
            long[] a = new long[37];
            for (int i = 0; i < n; ++i) {
                a[i] = nextInt();
            }
            Arrays.sort(a);
            double ans = 0;
            for (long floor = 1; floor < a[36]; ++floor) {
                double res = 0;
                int cnt = 0;
                long bcur = 0;
                for (int i = 0; i < 37; ++i) {
                    if (a[i] > floor) {
                        break;
                    } else {
                        cnt++;
                    }
                }
                if (cnt == 0) {
                    continue;
                }
                for (int i = 0; i < cnt; ++i) {
                    bcur += floor - a[i];
                    res += (floor - a[i]) * 36.0;
                }
                res /= (double)cnt;
                if (bcur > b) {
                    break;
                }
                ans = Math.max(ans, res - bcur);
                for (int i = cnt - 1; i > 0 && bcur < b; --i) {
                    bcur++;
                    res = (res * (i + 1) - (floor - a[i]) * 36) / (double) i;
                    if (Double.isNaN(res)) {
                        break;
                    }
                    ans = Math.max(ans, res - bcur);
                }
            }
//            long[] value = new long[37];
//            long[] valueSum = new long[37];
//            long[] count = new long[37];
//            long[] countSum = new long[37];
//            count[0] = 1;
//            countSum[0] = 1;
//            value[0] = a[0];
//            valueSum[0] = a[0];
//            int c = 0;
//            for (int i = 1; i < 37; ++i) {
//                if (a[i] == a[i - 1]) {
//                    count[c]++;
//                    countSum[c]++;
//                    valueSum[c] += a[i];
//                } else {
//                    c++;
//                    countSum[c] = countSum[c - 1];
//                    count[c]++;
//                    countSum[c]++;
//                    value[c] = a[i];
//                    valueSum[c] = valueSum[c - 1];
//                    valueSum[c] += a[i];
//                }
//            }
//            c++;
//            double ans = 0;
//            for (int i = 1; i < c; ++i) {
//                long need = countSum[i - 1] * (value[i] - value[i - 1] - 1);
//                b -= i > 1 ? countSum[i - 2] : 0;
//                if (b < 0) {
//                    break;
//                }
//                boolean flag = b < need;
//                long floor = !flag ? value[i] - 1 : value[i - 1] + b / countSum[i - 1];
//                b -= (floor - value[i - 1]) * countSum[i - 1];
//                long put = 0;
//                double cur = 0;
//                for (int j = 0; j < countSum[i - 1]; ++j) {
//                    put += floor - a[j];
//                    cur += (double) (floor - a[j]) * 36.0 / (double) countSum[i - 1];
//                }
//                cur -= put;
//                ans = Math.max(ans, cur);
//                if (flag) {
//                    break;
//                }
//            }
            out.printf("%.9f\n", ans);
        }
    }


    private void init() throws FileNotFoundException {
        if (fileName.equals("#Console")) {
            in = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(System.out);
        } else if (fileName.equals("#IOtxt")) {
            in = new BufferedReader(new FileReader("input.txt"));
            out = new PrintWriter("output.txt");
        } else {
            in = new BufferedReader(new FileReader(fileName + ".in"));
            out = new PrintWriter(fileName + ".out");
        }
    }


    private int nextInt() throws IOException {
        return Integer.parseInt(nextToken());
    }

    private long nextLong() throws IOException {
        return Long.parseLong(nextToken());
    }


    private String nextToken() throws IOException {
        while (!tok.hasMoreTokens()) {
            String s = in.readLine();
            if (s == null) {
                return null;
            }
            tok = new StringTokenizer(s);
        }
        return tok.nextToken();
    }

}
            
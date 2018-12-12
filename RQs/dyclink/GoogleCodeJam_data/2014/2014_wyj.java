package contest;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class MagicalTour {
    final static String PROBLEM_NAME = "tour";
    final static String WORK_DIR = "D:\\GCJ\\" + PROBLEM_NAME + "\\";

    long[] A, S;

    void solve(Scanner sc, PrintWriter pw) {
        int N = sc.nextInt();
        long p = sc.nextLong(), q = sc.nextLong(), r = sc.nextLong(), s = sc.nextLong();

        A = new long[N+1];
        for (int i=0; i<N; i++) {
            A[i+1] = (p*i+q) % r + s;
        }

        S = new long[N+1];
        S[0] = 0;
        for (int i=1; i<=N; i++) {
            S[i] = S[i-1] + A[i];
        }

        long ans = Long.MAX_VALUE;

        long sumLeft = 0;

        for (int a = 1; a <= N; a++) {
            int L = a-1, R = N;
            while (R-L > 1) {
                int mid = (L+R)/2;
                long sum1 = S[mid] - S[a-1];
                long sum2 = S[N] - S[mid];
                if (sum1 < sum2) L = mid; else R = mid;
            }
            for (int b = L-3; b <= L+3; b++) {
                if (b < a || b > N) continue;
                long sumMid = S[b] - S[a-1];
                long sumRight = S[N] - S[b];
                long max = Math.max(sumLeft, Math.max(sumMid, sumRight));
                ans = Math.min(ans, max);
            }
            sumLeft += A[a];
        }

        pw.println((S[N] - ans) / (double)S[N]);
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(new FileReader(WORK_DIR + "input.txt"));
        PrintWriter pw = new PrintWriter(new FileWriter(WORK_DIR + "output.txt"));
        int caseCnt = sc.nextInt();
        for (int caseNum=0; caseNum<caseCnt; caseNum++) {
            System.out.println("Processing test case " + (caseNum + 1));
            pw.print("Case #" + (caseNum+1) + ": ");
            new MagicalTour().solve(sc, pw);
        }
        pw.flush();
        pw.close();
        sc.close();
    }
}

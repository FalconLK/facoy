package contest;

import java.util.*;
import java.io.*;
import java.math.*;

public class Cheaters {
    final static String PROBLEM_NAME = "cheat";
    final static String WORK_DIR = "D:\\Gcj\\" + PROBLEM_NAME + "\\";

    long B;
    long[] X;

    boolean can(long min) {
        long need = 0;
        for (int i=0; i < X.length; i++)
            if (X[i] <= min) need += min - X[i];
        return (need <= B);
    }

    double profit(long min) {
        long spent = 0;
        int cnt = 0;
        for (int i=0; i < X.length; i++)
            if (X[i] <= min) {
                spent += min - X[i];
                cnt++;
            }
        return 36 * spent / (double)cnt - spent;
    }

    void solve(Scanner sc, PrintWriter pw) {
        B = sc.nextLong();
        int N = sc.nextInt();
        X = new long[37];
        for (int i=0; i<N; i++) X[i] = sc.nextLong();
        Arrays.sort(X);

        double ans = 0.0;
        for (int win = 1; win <= 35; win++) {
            long min = X[win-1];
            long need = 0;
            for (int i=0; i < win; i++)
                need += min - X[i];
            for (int i=win; i < 37; i++)
                if (min + 1 > X[i])
                    need += min + 1 - X[i];
            if (need > B) continue;

            long L = min, R = 1000000000000000L;
            while (R - L > 1) {
                long mid = (L + R) / 2;
                need = 0;
                for (int i=0; i < win; i++)
                    need += mid - X[i];
                for (int i=win; i<37; i++)
                    if (mid + 1 > X[i])
                        need += mid + 1 - X[i];
                if (need <= B) L = mid; else R = mid;
            }

            long loseMoney = 0;
            long winMoney = 0;
            for (int i=0; i < win; i++) {
                loseMoney += L - X[i];
                winMoney += 36 * (L - X[i]);
            }
            for (int i=win; i < 37; i++)
                if (L + 1 > X[i])
                    loseMoney += L + 1 - X[i];

            ans = Math.max(ans, winMoney / (double)win - loseMoney);
        }

        pw.println(ans);
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(new FileReader(WORK_DIR + "input.txt"));
        PrintWriter pw = new PrintWriter(new FileWriter(WORK_DIR + "output.txt"));
        int caseCnt = sc.nextInt();
        for (int caseNum=0; caseNum<caseCnt; caseNum++) {
            System.out.println("Processing test case " + (caseNum + 1));
            pw.print("Case #" + (caseNum+1) + ": ");
            new Cheaters().solve(sc, pw);
        }
        pw.flush();
        pw.close();
        sc.close();
    }
}

package gcj;

import java.util.*;
import java.io.*;

public class Perfect {
    final static String PROBLEM_NAME = "perfect";
    final static String WORK_DIR = "D:\\Gcj\\" + PROBLEM_NAME + "\\";

    static void preprocess() {
    }

    class Data implements Comparable<Data> {
        int L, P, id;
        public Data(int L, int P, int id) {
            this.L = L;
            this.P = P;
            this.id = id;
        }
        public int compareTo(Data other) {
            int A = this.L * other.P;
            int B = this.P * other.L;
            if (A<B) return -1;
            if (A>B) return 1;
            return this.id - other.id;
        }
    }

    int N;
    int[] L;
    int[] P;
    boolean[] used;
    int[] ans;

    int[] ord;
    double best;

    void go(int pos) {
        if (pos==N) {
            double sum = 0.0;
            for (int i=0; i<N; i++) {
                double mul = 1.0;
                for (int j=i; j<N; j++)
                    mul *= 1.0-P[ord[j]]/100.0;
                sum += L[ord[i]]/mul;
            }
            if (sum<best*(1-1e-10)) {
                best = sum;
                ans = ord.clone();
            }
            return;
        }
        for (ord[pos]=0; ord[pos]<N; ord[pos]++) if (!used[ord[pos]]) {
            used[ord[pos]]=true;
            go(pos+1);
            used[ord[pos]]=false;
        }
    }

    void solveBrute(Scanner sc, PrintWriter pw) {
        int N = sc.nextInt();
        L = new int[N];
        P = new int[N];
        for (int i=0; i<N; i++) L[i] = sc.nextInt();
        for (int i=0; i<N; i++) P[i] = sc.nextInt();
        used = new boolean[N];
        ans = new int[N];
        ord = new int[N];
        best = 1e100;
        this.N = N;
        go(0);
        for (int i=0; i<N; i++) pw.print(" "+ans[i]);
        pw.println();
    }

    void solve(Scanner sc, PrintWriter pw) {
        int N = sc.nextInt();
        Data[] d = new Data[N];
        int[] L = new int[N];
        int[] P = new int[N];
        for (int i=0; i<N; i++) L[i] = sc.nextInt();
        for (int i=0; i<N; i++) P[i] = sc.nextInt();
        for (int i=0; i<N; i++) {
            d[i] = new Data(L[i], P[i], i);
        }
        Arrays.sort(d);
        for (int i=0; i<N; i++)
            pw.print(" "+d[i].id);
        pw.println();
    }

    public static void main(String[] args) throws Exception {
        preprocess();

        Scanner sc = new Scanner(new FileReader(WORK_DIR + "input.txt"));
        PrintWriter pw = new PrintWriter(new FileWriter(WORK_DIR + "output.txt"));
        int caseCnt = sc.nextInt();
        for (int caseNum=0; caseNum<caseCnt; caseNum++) {
            System.out.println("Processing test case " + (caseNum + 1));
            pw.print("Case #" + (caseNum+1) + ":");
            new Perfect().solve(sc, pw);
        }
        pw.flush();
        pw.close();
        sc.close();
    }
}

package com.google.codejam2014.round3;

import java.io.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;

public class MagicalMarvelousTour {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    PrintWriter writer = new PrintWriter(System.out);
    StringTokenizer stringTokenizer;

    MagicalMarvelousTour() throws IOException {
        reader = new BufferedReader(new FileReader("input.txt"));
        writer = new PrintWriter(new FileWriter("output.txt"));
    }

    String next() throws IOException {
        while (stringTokenizer == null || !stringTokenizer.hasMoreTokens()) {
            stringTokenizer = new StringTokenizer(reader.readLine());
        }
        return stringTokenizer.nextToken();
    }

    int nextInt() throws IOException {
        return Integer.parseInt(next());
    }

    long nextLong() throws IOException {
        return Long.parseLong(next());
    }

    double nextDouble() throws IOException {
        return Double.parseDouble(next());
    }

    void solveTestCase(int testNumber) throws IOException {
        writer.printf("Case #%d: ", testNumber);
        int n = nextInt();
        int p = nextInt();
        int q = nextInt();
        int r = nextInt();
        int s = nextInt();
        int[] a = new int[n];
        for(int i = 0; i < n; i++) {
            a[i] = (i * p + q) % r + s;
        }
        int[] ps = new int[n + 1];
        for(int i = 1; i <= n; i++) {
            ps[i] = ps[i - 1] + a[i - 1];
        }
        double ans = 0;
        for(int i = 0; i < n; i++) {
            for(int j = i + 1; j <= n; j++) {
                int s1 = ps[i];
                int s2 = ps[j] - ps[i];
                int s3 = ps[n] - ps[j];
                int max = Math.max(s1, Math.max(s2, s3));
                ans = Math.max(ans, 1.0 * (ps[n] - max) / ps[n]);
            }
        }
        writer.println(String.format(Locale.US, "%.13f", ans));
    }

    void solve() throws IOException {
        int testsNumber = nextInt();
        for(int i = 1; i <= testsNumber; i++) {
            solveTestCase(i);
            writer.flush();
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        new MagicalMarvelousTour().solve();
    }
}

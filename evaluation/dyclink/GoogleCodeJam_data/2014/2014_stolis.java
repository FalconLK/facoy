package round3;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class MagicalMarvelousTour3 {
    
    public static void main(String[] args) throws Exception {
        File inputFile = new File("A-large.in");
        Scanner in = new Scanner(inputFile);
        File outputFile = new File("output.txt");
        PrintWriter out = new PrintWriter(outputFile);

        int T = in.nextInt();
        for (int t=0; t<T; t++) {
            int N = in.nextInt();
            long p = in.nextInt();
            long q = in.nextInt();
            long r = in.nextInt();
            long s = in.nextInt();
            long[] A = new long[N];
            for (int n=0; n<N; n++) {
                A[n] = (n*p+q)%r+s;
            }
            long[] best = new long[N];
            int beforeHalf = 0;
            long beforeHalfSum = 0;
            long sum = 0;
            for (int n=0; n<N; n++) {
                sum += A[n];
                while (beforeHalf < n && beforeHalfSum+A[beforeHalf] < sum/2) {
                    beforeHalfSum += A[beforeHalf];
                    beforeHalf++;
                }
                long max = sum;
                if (beforeHalf > 0) {
                    max = Math.max(beforeHalfSum, sum-beforeHalfSum);
                }
                if (beforeHalf <= n-1) {
                    long value = Math.max(beforeHalfSum+A[beforeHalf], sum-beforeHalfSum-A[beforeHalf]);
                    max = Math.min(max, value);
                }
                best[n] = max;
            }
            long min = best[N-1];
            long subSum = 0;
            for (int n=N-1; n>=1; n--) {
                subSum += A[n];
                min = Math.min(min, Math.max(subSum, best[n-1]));
            }
            
            double answer = (sum-min)/(double)sum;
            out.println("Case #"+(t+1)+": "+answer);
        }

        out.close();
    }
    
}

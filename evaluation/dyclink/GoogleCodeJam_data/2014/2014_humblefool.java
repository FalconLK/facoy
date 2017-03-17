import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;


public class MagicalTour {
    static Scanner input;
    static PrintWriter out;
    public static void main(String[] args) throws Exception {
//        input = new Scanner(System.in);
        input = new Scanner(new FileReader("A-large.in"));
        
//        out = new PrintWriter(System.out);
        out = new PrintWriter(new FileWriter("A-large.out"));
        
        int testCases = input.nextInt();
        for(int testCase = 1; testCase <= testCases; testCase++) {
            out.println("Case #" + testCase + ": " + solveCase(testCase));
        }
        out.close();
    }
    
    static double solveCase(int testCase) {
        int n = input.nextInt();
        int p = input.nextInt(), q = input.nextInt(), r = input.nextInt(), s = input.nextInt();
        int[] values = new int[n];
        long sum = 0;
        for(int i=0;i<n;i++) {
            values[i] = (int)(((long)i * p + q) % r + s);
            sum += values[i];
        }
        
        long res = Integer.MIN_VALUE, leftSum = 0, middle = values[0], rightSum = sum - middle;
        for(int a = 0, b = 0; a < n; a++) {
            while(true) {
                if(b + 1 == n)
                    break;
                if(b < a || middle + values[b+1] <= rightSum - values[b+1]) {
                    middle += values[b+1];
                    rightSum -= values[b+1];
                    b++;
                }
                else
                    break;
            }
            res = Math.max(res, sum - Math.max(leftSum, Math.max(rightSum, middle)));
            if (b + 1 < n) {
                res = Math.max(res, sum - Math.max(leftSum, Math.max(rightSum - values[b+1], middle + values[b+1])));
            }
            leftSum += values[a];
            middle -= values[a];
        }
        return (double) res / sum;
    }
}

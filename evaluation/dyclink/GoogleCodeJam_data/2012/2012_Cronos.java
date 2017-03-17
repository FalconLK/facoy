package codejam.y2012.r3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author Ilya Lantuh
 */
public class Task1 {

    static Scanner input;
    static BufferedWriter output;

    public static void main(String[] args) throws Exception {
        input = new Scanner(new File("D:/Programming/Projects/CodeJam/input.txt"));
        output = new BufferedWriter(new FileWriter("D:/Programming/Projects/CodeJam/output.txt"));
        int T = input.nextInt();
        for (int i = 1; i <= T; i++) {
            String result = getResult();
            System.out.println("Case #" + i + ": " + result);
            output.write("Case #" + i + ": " + result);
            output.newLine();
        }
        output.close();
    }

    public static String getResult() {
        int N = input.nextInt();
        int[] L = new int[N];
        for (int i = 0; i < N; i++) {
            L[i] = input.nextInt();
        }
        int[] P = new int[N];
        for (int i = 0; i < N; i++) {
            P[i] = input.nextInt();
        }
        
        boolean[] u = new boolean[N];
        List<Integer> result = new ArrayList<Integer>(N);
        
        while (result.size() < N) {
            int max = -1;
            for (int i = 0; i < N; i++) {
                if (u[i]) continue;
                if (max == -1) max = i;
                else if (P[i] > P[max]) max = i;
            }
            u[max] = true;
            result.add(max);
        }

        StringBuilder sb = new StringBuilder();

        for (Integer index : result) {
            sb.append(index + " ");
        }
        
        return sb.toString();
    }

}

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class Cheaters {
    static Scanner sc;
    static PrintWriter out;

    public static void main(String[] args) throws IOException {
//        sc = new Scanner(System.in);
//        out = new PrintWriter(System.out);

        sc = new Scanner(new FileReader("A-large.in"));
        out = new PrintWriter(new FileWriter("A-large.out"));

        int testCases = sc.nextInt();
        for (int testCase = 1; testCase <= testCases; testCase++) {
            out.println(String.format("Case #%s: %s", testCase,
                    solveCase()));
        }
        out.close();
    }

    static double solveCase() {
        long budget = sc.nextLong();
        int bets = sc.nextInt();

        long[] amount = new long[37];
        for (int i = 0; i < bets; i++) {
            amount[i] = sc.nextLong();
        }
        Arrays.sort(amount);

        double best = 0.0;
        for (int joined = 1; joined <= 37; joined++) {
            long lower = 0, upper = (long) 1e14;
            while (lower + 1 < upper) {
                long middle = (lower + upper) / 2;
                if (possible(amount, joined, middle, budget))
                    lower = middle;
                else
                    upper = middle;
            }

            if (possible(amount, joined, lower, budget)) {
                double expected = 0, bet = 0;
                for (int i = 0; i < joined; i++) {
                    bet += Math.max(0, lower - amount[i]);
                    expected += 36.0 * Math.max(0, lower - amount[i]) / joined;
                }
                for(int i = joined; i < 37;i++) {
                    bet += Math.max(0, lower + 1 - amount[i]);
                }
//                System.err.println(String.format(
//                        "Joined: %s, lowest bet: %s, profit: %s", joined,
//                        lower, expected - bet));

                best = Math.max(best, expected - bet);
            }
        }

        return best;
    }

    static boolean possible(long[] amount, int joined, long lowestBet,
            long budget) {
        long required = 0;
        for (int i = 0; i < 37; i++) {
            required += Math.max(0, i < joined ? (lowestBet - amount[i])
                    : (lowestBet + 1 - amount[i]));
        }
        return required <= budget;

    }
}

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

/**
 * @author nik
 */
public class A {
  public static void main(String[] args) throws IOException {
    Scanner in = new Scanner(new File("A-small-attempt0 (1).in"));
    PrintWriter out = new PrintWriter("a.out");
    int T = in.nextInt();
    for (int t = 1; t <= T; t++) {
      int n = in.nextInt();
      Lev[] ls = new Lev[n];
      for (int i = 0; i < n; i++) {
        ls[i] = new Lev();
        ls[i].i = i;
        ls[i].l = in.nextInt();
      }
      for (int i = 0; i < n; i++) {
        ls[i].p = in.nextInt();
      }
      Arrays.sort(ls);
      StringBuilder buf = new StringBuilder();
      for (Lev l : ls) {
        buf.append(" ").append(l.i);
      }
      out.println("Case #"+t+":" + buf);
    }
    out.close();
    in.close();
  }

  private static class Lev implements Comparable<Lev> {
    public int l, p, i;

    public int compareTo(Lev o) {
      double q1 = Math.log((100.-p)*0.01)/l;
      double q2 = Math.log((100.-o.p)*0.01)/o.l;
      return Double.compare(q1, q2);
    }
  }
}

import java.io.*;
import java.util.*;
import java.math.*;

public class A implements Runnable {
	
	class Lvl implements Comparable<Lvl> {
		int num, per, len;
		
		public Lvl(int num, int per, int len) {
			this.len = len;
			this.num = num;
			this.per = per;
		}
		
		public int compareTo(Lvl a) {
			if (this.per == a.per) {
				if (this.len == a.len) {
					return this.num - a.num;
				}
				return -this.len + a.len;
			}
			else {
				return -this.per+a.per;
			}
		}
	}
	
	public void run() {
		int tn = nextInt();
		for (int tst = 0; tst < tn; ++tst) {
			int n = nextInt();
			Lvl[] arr = new Lvl[n];
			int[] len = new int[n];
			int[] per = new int[n];
			
			for (int i = 0; i < n; ++i) {
				len[i] = nextInt();
			}
			for (int i = 0; i < n; ++i) {
				per[i] = nextInt();
			}
			
			for (int i = 0; i < n; ++i) {
				arr[i] = new Lvl(i, per[i], len[i]);
			}
			Arrays.sort(arr);
			
			out.print("Case #"+(tst+1)+":");
			for (int i = 0; i < n; ++i) {
				out.print(" " + arr[i].num);
			}
			out.println();
		}
		out.flush();
	}
	
	private static BufferedReader br = null;
	private static StringTokenizer stk = null;
	private static PrintWriter out = null;
	
	public static void main(String[] args) throws IOException {
		br = new BufferedReader(new FileReader(new File("D:\\A.txt")));
		out = new PrintWriter("D:\\AA.txt");
		(new Thread(new A())).start();
	}
	
	public void loadLine() {
		try {
			stk = new StringTokenizer(br.readLine());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String nextLine() {
		try {
			return (br.readLine());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int nextInt() {
		while (stk==null || !stk.hasMoreTokens()) loadLine();
		return Integer.parseInt(stk.nextToken());
	}
	
	public long nextLong() {
		while (stk==null || !stk.hasMoreTokens()) loadLine();
		return Long.parseLong(stk.nextToken());
	}
	
	public double nextDouble() {
		while (stk==null || !stk.hasMoreTokens()) loadLine();
		return Double.parseDouble(stk.nextToken());
	}
	
	public String nextWord() {
		while (stk==null || !stk.hasMoreTokens()) loadLine();
		return (stk.nextToken());
	}
}


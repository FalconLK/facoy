

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;

public class R3_A_easy {

	static boolean test = true;

	private void solve() throws Throwable {
		int t = iread();
		for (int i = 0; i < t; i++) {
			solveIt(i+1);
		}
	}
	
	class Level {
		int id, time, percentage;

		public Level(int id, int time, int percentage) {
			super();
			this.id = id;
			this.time = time;
			this.percentage = percentage;			
		}
	}
	
	private void solveIt(int casenr) throws Throwable {
		int n = iread();
		
		Level[] lvls = new Level[n];
		
		for (int i = 0; i < n; i++) {
			iread();
		}
		for (int i = 0; i < n; i++) {
			lvls[i] = new Level(i, 1,100-iread());
		}
		List<Level> list = new ArrayList<R3_A_easy.Level>();
		for (int i = 0; i < n; i++) {
			list.add(lvls[i]);
		}
		Collections.sort(list, new Comparator<Level>() {

			@Override
			public int compare(Level o1, Level o2) {
				if(o1.percentage != o2.percentage){
					return o1.percentage - o2.percentage;
				}
				
				return o1.id - o2.id;
			}
		});
		
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < n; i++) {
			b.append(list.get(i).id);
			if(i < n-1){
				b.append(" ");
			}         
		}
		
		String answerString = "Case #" + casenr + ": " + b.toString();
		out.write(answerString + "\n");
		System.out.println(answerString);
	}
	
	public int iread() throws Exception {
		return Integer.parseInt(wread());
	}

	public double dread() throws Exception {
		return Double.parseDouble(wread());
	}

	public long lread() throws Exception {
		return Long.parseLong(wread());
	}

	public String wread() throws IOException {
		StringBuilder b = new StringBuilder();
		int c;
		c = in.read();
		while (c >= 0 && c <= ' ')
			c = in.read();
		if (c < 0)
			return "";
		while (c > ' ') {
			b.append((char) c);
			c = in.read();
		}
		return b.toString();
	}

	public static void main(String[] args) throws Throwable {
		new R3_A_easy().solve();
		out.close();
	}

	public R3_A_easy() throws Throwable {
		if (test) {
			in = new BufferedReader(new FileReader(new File(testDataFile)));
			out = new BufferedWriter(new FileWriter(getClass().getCanonicalName() + "-out.txt"));
		} else {
			new BufferedReader(inp);
		}
	}

	static InputStreamReader inp = new InputStreamReader(System.in);
	static BufferedReader in = new BufferedReader(inp);
	static BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
//	static BufferedWriter out;// = new BufferedWriter(new FileWriter("out.txt"));

	
//	static String testDataFile = "g-example.in";
//	static String testDataFile = "A-small-attempt0.in";
	static String testDataFile = "A-small-attempt2.in";
}

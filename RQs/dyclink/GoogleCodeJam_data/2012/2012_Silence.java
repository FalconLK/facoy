import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main implements Runnable {

	public static void main(String[] args) throws IOException {
		new Thread(new Main()).start();
	}
	
	public void run() {
		try {
			run1();
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
	
	public void run1() throws IOException {
		Scanner sc = new Scanner(new FileReader("input.txt"));
		PrintWriter pw = new PrintWriter(new FileWriter("output.txt"));
		int tN = sc.nextInt();
		sc.nextLine();
		for (int tn = 0; tn < tN; tn++) {
			int n = sc.nextInt();
			int[] p = new int[n];
			int[] len = new int[n];
			for(int i = 0; i < n; i++) {
				p[i] = sc.nextInt();
			}
			for(int i = 0; i < n; i++) {
				len[i] = sc.nextInt();
			}
			boolean[] oppa = new boolean[n];
			pw.print("Case #" + (tn + 1) + ":");
			c : for(int i = 0; i < n; i++) {
				int be = -1;
				for(int j = 0; j < n; j++) {
					if(!oppa[j]) {
						if(be == -1 || len[j] * p[be] > len[be] * p[j]) {
							be = j;
						}
					}
				}
				oppa[be] = true;
				pw.print(" " + be);
			}
			pw.println();
		}
		pw.close();
	}
}

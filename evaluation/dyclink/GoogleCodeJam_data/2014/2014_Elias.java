import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;


public class Main {
	public static void main(String[] args) throws IOException {
		new Main();
	}
	
	public Main() throws IOException {
		//Scanner in = new Scanner(new File("A-test.txt"));
		Scanner in = new Scanner(new File("A-large.in"));
		BufferedWriter out = new BufferedWriter(new FileWriter("A-large.txt"));
		
		int amountOfTasks = in.nextInt();
		for (int task = 1; task <= amountOfTasks; task++) {
			long N = in.nextLong();
			long p = in.nextLong();
			long q = in.nextLong();
			long r = in.nextLong();
			long s = in.nextLong();
			
			long[] array = new long[(int) N + 2];
			array[0] = 0;
			array[(int) N + 1] = 0;
			for (long i = 1; i <= N; i++) {
				long a = (i-1)*p + q;
				array[(int) i] = (a%r) + s;
			}
			
			long[] partialLeft = new long[(int) N + 2];
			partialLeft[0] = 0;
			for (int i = 1; i < (int)(N) + 2; i++) {
				partialLeft[i] = partialLeft[i-1] + array[i];
			}
			
			long[] partialRight = new long[(int) N + 2];
			partialRight[(int)(N)+1] = 0;
			for (int i = (int)(N); i >= 0; i--) {
				partialRight[i] = partialRight[i+1] + array[i];
			}
			
			long total = partialLeft[(int)(N) + 1];
			long min = total;
			
			for (int b = (int) N + 1; b >= 0; b--) {
				long totalLeft = total - partialRight[b];
				//if (task == 2) System.out.println(totalLeft);
				int a = binarySearch(partialLeft, totalLeft/2);
				//if (task == 2) System.out.println(a);
				if (a < 0) a = 0;
				long x = maximum(partialRight[b], partialLeft[a], total - (partialRight[b] + partialLeft[a]));
				//if (task == 2) System.out.println(a + " " + b + " " + x);
				min = Math.min(min, x);
				x = maximum(partialRight[b], partialLeft[a+1], total - (partialRight[b] + partialLeft[a+1]));
				//if (task == 2) System.out.println(a + " " + b + " " + x);
				min = Math.min(min, x);
			}
			
			long profit = total - min;
			//if (task == 2) System.out.println(min);
			double solution = (double)(profit) / (double)(total);
			
			out.write("Case #" + task + ": " + solution);
			out.newLine();
		}
		
		out.close();
		in.close();
	}
	
	public int binarySearch(long[] array, long key) {
		int left = 0;
		int right = array.length-1;
		while(true) {
			if (right - left == 1) {
				if (array[right] <= key) return right;
				else return left;
			} else if (right == left) {
				return left;
			}
			else {
				int mid = (right+left)/2;
				if (array[mid] < key) left = mid;
				else right = mid;
			}
		}
	}
	
	public long maximum(long a, long b, long c) {
		return Math.max(a, Math.max(b, c));
	}
}
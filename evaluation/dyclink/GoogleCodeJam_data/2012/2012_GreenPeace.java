import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;

public class Main {

	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt")));
				String line = br.readLine();
				BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));
				int tests = Integer.valueOf(line.trim());
				for (int i = 0; i < tests; i++) {
					int n = Integer.valueOf(br.readLine());
					int params[][] = new int[n][3];
					String[] temp = br.readLine().split(" ");
					for (int j = 0; j < n; j++) {
						params[j][0] = j;
						params[j][1] = Integer.valueOf(temp[j]);
					}
					temp = br.readLine().split(" ");
					for (int j = 0; j < n; j++) {
						params[j][2] = Integer.valueOf(temp[j]);
					}
					Arrays.sort(params, new Comparator<int[]>(){

						@Override
						public int compare(int[] o1, int[] o2) {
							if (o1[2] > o2[2]) {
								return -1;
							}
							if (o1[2] < o2[2]) {
								return 1;
							}
							return o1[1]  - o2[1];
						}
						
					});
					out.write("Case #"+ (i + 1) +":");
					for (int j = 0; j < params.length; j++) {
						out.write(String.format(" %s", params[j][0]));
						
					}
					out.write("\n");
				}
				out.close();
		} catch (FileNotFoundException e) {
			// ????
		} catch (IOException e) {
			// ????
		}
	}
}
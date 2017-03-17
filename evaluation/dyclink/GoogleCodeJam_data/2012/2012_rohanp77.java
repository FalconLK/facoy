import java.io.*;
import java.util.*;

public class A {
	
	public static BufferedReader in;
	public static StringTokenizer st;
	
	public static void main(String[] args) throws IOException{
		in = new BufferedReader(new FileReader("A.in"));
		PrintWriter out = new PrintWriter(new FileWriter("A.out"));
		
		int test = readInt();
		for (int t=1; t<=test; t++){
			out.print("Case #"+t+":");
			
			int num = readInt();
			Level[] arr = new Level[num];
			for (int i=0; i<num; i++)
				arr[i] = new Level(readInt(), i);
			for (int i=0; i<num; i++)
				arr[i].percent = readInt();
			
			double soFar = 0;
			boolean flag = true;
			while(flag){
				flag = false;
				for (int j=0; j<num-1; j++){
					if(arr[j].time+arr[j+1].time*(1-arr[j].percent/100.0) > arr[j].time*(1-arr[j+1].percent/100.0)+arr[j+1].time){
						Level temp = arr[j];
						arr[j] = arr[j+1];
						arr[j+1] = temp;
						flag = true;
					}
				}
			}
			
			for (int i=0; i<num; i++)
				out.print(" "+arr[i].index);
			out.println();
		}
		
		out.close();
		in.close();
	}
	
	public static String readLine() throws IOException{
		return in.readLine();
	}
	
	public static String readToken() throws IOException{
		while(st==null || !st.hasMoreTokens())
			st = new StringTokenizer(readLine());
		return st.nextToken();
	}
	
	public static int readInt() throws IOException{
		return Integer.parseInt(readToken());
	}
}

class Level{
	int index;
	int time;
	int percent;
	
	public Level(int t, int i){
		time = t;
		index = i;
		percent = -1;
	}
}
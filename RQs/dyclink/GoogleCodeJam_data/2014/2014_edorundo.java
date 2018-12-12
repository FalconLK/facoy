import java.util.*;
import java.io.*;
public class A
{
	public static long[] array;
	
	public static void main(String[] args) throws Exception
	{
		Scanner in = new Scanner(new File("a-large.in"));
		PrintWriter out = new PrintWriter(new FileWriter(new File("alarge.out")));
		
		int t = in.nextInt();
		for(int x = 0; x < t; x++)
		{
			int n = in.nextInt();
			long p = in.nextInt();
			long q = in.nextInt();
			long r = in.nextInt();
			long s = in.nextInt();
			
			array = new long[n + 1];
			for(int y = 0; y < n; y++)
			{
				array[y + 1] = array[y] + (y * p + q) % r + s;
			}
			
			int index = 0;
			long min = Long.MAX_VALUE;
			for(int z = 0; z < array.length; z++)
			{
				while(z < n - 1 && Math.abs(sum(z, index) - sum(index + 1, n - 1)) > Math.abs(sum(z, index + 1) - sum(index + 2, n - 1)))
				{
					index++;
				}
				
				min = Math.min(min, Math.max(sum(0, z - 1), Math.max(sum(z, index), sum(index + 1, n - 1))));
			}
			
			double result = (double)(sum(0, n - 1) - min) / (double)sum(0, n - 1);
			
			out.printf("Case #%d: %.10f%n", x + 1, result);
		}
		
		out.close();
	}
	
	public static long sum(int a, int b)
	{
		if(a > b)
		{
			return 0;
		}
		
		return array[b + 1] - array[a];
	}
}

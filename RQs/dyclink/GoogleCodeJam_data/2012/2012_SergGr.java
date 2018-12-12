import java.io.*;
import java.util.*;

public class TaskA
{
	static class Level implements Comparable<Level>
	{
		public int length;
		public int probDie;
		public final int index;

		public Level(int index)
		{
			this.index = index;
		}

		public int compareTo(Level o)
		{
			int cmp0 = new Integer(probDie).compareTo(o.probDie);
			if (cmp0 != 0)
				return -cmp0;
			return new Integer(index).compareTo(o.index);
		}
	}

	static String calculate(List<Level> levels)
	{
		Collections.sort(levels);
		StringBuilder sb = new StringBuilder();
		for (Level l : levels)
		{
			sb.append(l.index).append(" ");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static void main(String[] args) throws IOException
	{

//		final String baseFileName = "taskA";
		final String baseFileName = "A-small-attempt0";
//		final String baseFileName = "A-small-attempt1";
//		final String baseFileName = "A-small-attempt2";
//		final String baseFileName = "A-small-attempt3";
//		final String baseFileName = "A-large";
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(baseFileName + ".out")));
		BufferedReader reader = new BufferedReader(new FileReader(baseFileName + ".in"));


		String inLine;
		inLine = reader.readLine();
		final int T = Integer.parseInt(inLine);
		for (int caseIndex = 1; caseIndex <= T; caseIndex++)
		{
			inLine = reader.readLine();
			final int N = Integer.parseInt(inLine);
			List<Level> levels = new ArrayList<Level>();
			for (int i = 0; i < N; i++)
				levels.add(new Level(i));

			StringTokenizer st;
			inLine = reader.readLine();
			st = new StringTokenizer(inLine);
			for (int i = 0; i < N; i++)
				levels.get(i).length = Integer.parseInt(st.nextToken());

			inLine = reader.readLine();
			st = new StringTokenizer(inLine);
			for (int i = 0; i < N; i++)
				levels.get(i).probDie = Integer.parseInt(st.nextToken());

			String res = calculate(levels);

			writer.printf("Case #%d: %s\r\n", caseIndex,
						  res);
		}

		writer.close();
	}
}


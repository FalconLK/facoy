import java.io.*;
import java.util.*;


class GameLevel implements Comparable<GameLevel>
{
	int id;
	int failProb;
	int timeCost;

	public GameLevel(int id, int t, int f)
	{
		this.id = id;
		failProb = f;
		timeCost = t;
	}
	
	public int getId()
	{
		return id;
	}
	
	@Override
	public int compareTo(GameLevel another) {
		int La = timeCost;
		int Fa = failProb;
		int Lb = another.timeCost;
		int Fb = another.failProb;
		int Tab = 100 * La + Lb * (100 - Fa);
		int Tba = 100 * Lb + La * (100 - Fb);
//		System.out.println(La + " " + Fa + " " + Lb + " " + Fb);
//		System.out.println(Tab + " " + Tba);
		if (Tab != Tba)
			return Tab - Tba;
		else
			return id - another.id;
	}
	
}
public class ProbA {

	
	public static void main(String[] args) throws FileNotFoundException {
		Scanner cin = new Scanner(new File("A.in.txt"));
		PrintStream cout = new PrintStream("A.out.txt");
		int caseN = cin.nextInt();
		for (int caseI=1; caseI<=caseN; caseI++)
		{
			int n = cin.nextInt();
			int[] l = new int[n];
			int[] p = new int[n];
			for (int i=0; i<n; i++)
				l[i] = cin.nextInt();
			for (int i=0; i<n; i++)
				p[i] = cin.nextInt();
			GameLevel[] levels = new GameLevel[n];
			for (int i=0; i<n; i++)
				levels[i] = new GameLevel(i, l[i], p[i]);
			Arrays.sort(levels);
			cout.print("Case #" + caseI + ":");
			for (int i=0; i<n; i++)
				cout.print(" " + levels[i].getId());
			cout.println();
		}
	}

}

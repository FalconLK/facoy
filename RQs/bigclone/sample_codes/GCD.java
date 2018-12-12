
//Heuristic:
//((while\s*\(\s*[a-z,A-Z,0-9,_,$]+\s*!=\s*0\s*\).*[a-z,A-Z,0-9,_,$]+\s*=\s*[a-z,A-Z,0-9,_,$]+\s*%\s*[a-z,A-Z,0-9,_,$]+)|(while\s*\(\s*[a-z,A-Z,0-9,_,$]+\s*!=\s*[a-z,A-Z,0-9,_,$]+\s*\).*[a-z,A-Z,0-9,_,$]+\s*=\s*[a-z,A-Z,0-9,_,$]+\s*-\s*[a-z,A-Z,0-9,_,$]+)|(return\s+[a-z,A-Z,0-9,_,$]+\s*\(\s*[a-z,A-Z,0-9,_,$]+\s*,\s*[a-z,A-Z,0-9,_,$]+\s*%\s*[a-z,A-Z,0-9,_,$]+\)))

public class GCD {
	
	// while(b != 0) AND b = a % b
	// while\s*\(\s*[a-z,A-Z,0-9,_,$]+\s*!=\s*0\s*\)
	// [a-z,A-Z,0-9,_,$]+\s*=\s*[a-z,A-Z,0-9,_,$]+\s*%\s*[a-z,A-Z,0-9,_,$]+
	// while\s*\(\s*[a-z,A-Z,0-9,_,$]+\s*!=\s*0\s*\).*[a-z,A-Z,0-9,_,$]+\s*=\s*[a-z,A-Z,0-9,_,$]+\s*%\s*[a-z,A-Z,0-9,_,$]+
	public static int gcd1(int a, int b) {
		while (b != 0) {
			int t = b;
			b = a % b;
			a = t;
		}
		return a;
	}
	
	// while(a != b) AND a = a - b
	//while\s*\(\s*[a-z,A-Z,0-9,_,$]+\s*!=\s*[a-z,A-Z,0-9,_,$]+\s*\)
	//[a-z,A-Z,0-9,_,$]+\s*=\s*[a-z,A-Z,0-9,_,$]+\s*-\s*[a-z,A-Z,0-9,_,$]+
	//while\s*\(\s*[a-z,A-Z,0-9,_,$]+\s*!=\s*[a-z,A-Z,0-9,_,$]+\s*\).*[a-z,A-Z,0-9,_,$]+\s*=\s*[a-z,A-Z,0-9,_,$]+\s*-\s*[a-z,A-Z,0-9,_,$]+
	public static int gcd2(int a, int b) {
		while (a != b) {
			if (a > b)
				a = a - b;
			else
				b = b - a;
		}
		return a;
	}
	
	// return gcd3(b, a % b)
	//return\s+[a-z,A-Z,0-9,_,$]+\s*\(\s*[a-z,A-Z,0-9,_,$]+\s*,\s*[a-z,A-Z,0-9,_,$]+\s*%\s*[a-z,A-Z,0-9,_,$]+\)
	public static int gcd3(int a, int b) {
		if (b == 0) {
			return 1;
		} else {
			return gcd3(b, a % b);
		}
	}
	
}
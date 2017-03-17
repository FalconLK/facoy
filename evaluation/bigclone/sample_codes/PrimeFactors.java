import java.util.ArrayList;
import java.util.List;

public class PrimeFactors {
	
	public static List<Integer> primeFactors1(int number) {
		int n = number;
		List<Integer> factors = new ArrayList<Integer>();
		for (int i = 2; i <= n; i++) {
			while (n % i == 0) {
				factors.add(i);
				n /= i;
			}
		}
		return factors;
	}

	public static List<Integer> primeFactors2(int number) {
		int n = number;
		List<Integer> factors = new ArrayList<Integer>();
		for (int i = 2; i <= n; i++) {
			while (n % i == 0) {
				factors.add(i);
				n /= i;
			}
		}
		return factors;
	}
	
	//public static void main(String args[]) {
	//	int i = 998;
	//	System.out.println(primeFactors1(i));
	//	System.out.println(primeFactors2(i));
	//}
	
}

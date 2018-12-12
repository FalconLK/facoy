import java.util.Random;


public class ShuffleArrayInPlace {
	
	// Heuristic
	//	Need to swap:
	//		a[i] = a[j] AND a[j] = tmp
	//  Need a random index:
	//		random.nextInt(
	//	#1:
	//		j = i + random.nextInt(length-i);
	//	#2:
	//		j = random.nextInt(i+1);
	
	public static void shuffle1(int[] a) {
		//Standard Fisher-Yates/Knuth Shuffle
		int length = a.length;
		
		Random random = new Random();
		random.nextInt();
		
		for(int i = 0; i < length; i++) {
			//Chose index to swap with from i <= j < length
			int j = i + random.nextInt(length-i);
			
			//Swap
			int tmp = a[i];
			a[i] = a[j];
			a[j] = tmp;
		}
	}
	
	public static void shuffle2(int[] a) {
		//Alternate Fisher-Yates/Knuth Shuffle
		Random random = new Random();
		random.nextInt();
		
		for(int i = a.length-1; i >= 1; i--) {
			//Choose index to swap from 0 <= j <= i
			int j = random.nextInt(i+1);
			
			//Swap
			int tmp = a[i];
			a[i] = a[j];
			a[j] = tmp;
		}
	}
	
	public static <T> void shuffle3(T[] a) {
		//Standard Fisher-Yates/Knuth Shuffle for Object array
		int length = a.length;
				
		Random random = new Random();
		random.nextInt();
				
		for(int i = 0; i < length; i++) {
			//Chose index to swap with from i <= j < length
			int j = i + random.nextInt(length-i);
			
			//Swap
			T tmp = a[i];
			a[i] = a[j];
			a[j] = tmp;
		}
	}
	
	//public static void main(String args[]) {
	//	int arr[] = {1,2,3,4,5,6,7,8,9,10};
	//	shuffle1(arr);
	//	for(int i : arr) {
	//		System.out.print(i + " " );
	//	}
	//	System.out.println();
	//	shuffle2(arr);
	//	for(int i : arr) {
	//		System.out.print(i + " " );
	//	}
	//	System.out.println();
	//	Integer arro[] = {1,2,3,4,5,6,7,8,9,10};
	//	shuffle3(arro);
	//	for(int i : arro) {
	//		System.out.print(i + " " );
	//	}
	//}
	
}

public class BubbleSort {

	public static void BubbleSortInt1(int[] num) {
		boolean flag = true; // set flag to true to begin first pass
		int temp; // holding variable

		while (flag) {
			flag = false; // set flag to false awaiting a possible swap
			for (int j = 0; j < num.length - 1; j++) {
				if (num[j] > num[j + 1]) // change to > for ascending sort
				{
					temp = num[j]; // swap elements
					num[j] = num[j + 1];
					num[j + 1] = temp;
					flag = true; // shows a swap occurred
				}
			}
		}
	}
	
	public static void BubbleSortInt2(int[] num) {
		int last_exchange;
		int right_border = num.length - 1;
		do {
			last_exchange = 0;
			for (int j = 0; j < num.length - 1; j++) {
				if (num[j] > num[j + 1])
				{
					int temp = num[j];
					num[j] = num[j + 1];
					num[j + 1] = temp;
					last_exchange = j;
				}
			}
			right_border = last_exchange;
		} while (right_border > 0);
	}
	
	public static void BubbleSortFloat1(float[] num) {
		boolean flag = true; // set flag to true to begin first pass
		float temp; // holding variable

		while (flag) {
			flag = false; // set flag to false awaiting a possible swap
			for (int j = 0; j < num.length - 1; j++) {
				if (num[j] > num[j + 1]) // change to > for ascending sort
				{
					temp = num[j]; // swap elements
					num[j] = num[j + 1];
					num[j + 1] = temp;
					flag = true; // shows a swap occurred
				}
			}
		}
	}
	
	public static void BubbleSortFloat2(float[] num) {
		int last_exchange;
		int right_border = num.length - 1;
		do {
			last_exchange = 0;
			for (int j = 0; j < num.length - 1; j++) {
				if (num[j] > num[j + 1])
				{
					float temp = num[j];
					num[j] = num[j + 1];
					num[j + 1] = temp;
					last_exchange = j;
				}
			}
			right_border = last_exchange;
		} while (right_border > 0);
	}
	
	public static void BubbleSortDouble1(double[] num) {
		boolean flag = true; // set flag to true to begin first pass
		double temp; // holding variable

		while (flag) {
			flag = false; // set flag to false awaiting a possible swap
			for (int j = 0; j < num.length - 1; j++) {
				if (num[j] > num[j + 1]) // change to > for ascending sort
				{
					temp = num[j]; // swap elements
					num[j] = num[j + 1];
					num[j + 1] = temp;
					flag = true; // shows a swap occurred
				}
			}
		}
	}
	
	public static void BubbleSortDouble2(double[] num) {
		int last_exchange;
		int right_border = num.length - 1;
		do {
			last_exchange = 0;
			for (int j = 0; j < num.length - 1; j++) {
				if (num[j] > num[j + 1])
				{
					double temp = num[j];
					num[j] = num[j + 1];
					num[j + 1] = temp;
					last_exchange = j;
				}
			}
			right_border = last_exchange;
		} while (right_border > 0);
	}
	
	public static void BubbleSortLong1(long[] num) {
		boolean flag = true; // set flag to true to begin first pass
		long temp; // holding variable

		while (flag) {
			flag = false; // set flag to false awaiting a possible swap
			for (int j = 0; j < num.length - 1; j++) {
				if (num[j] > num[j + 1]) // change to > for ascending sort
				{
					temp = num[j]; // swap elements
					num[j] = num[j + 1];
					num[j + 1] = temp;
					flag = true; // shows a swap occurred
				}
			}
		}
	}
	
	public static void BubbleSortLong2(long[] num) {
		int last_exchange;
		int right_border = num.length - 1;
		do {
			last_exchange = 0;
			for (int j = 0; j < num.length - 1; j++) {
				if (num[j] > num[j + 1])
				{
					long temp = num[j];
					num[j] = num[j + 1];
					num[j + 1] = temp;
					last_exchange = j;
				}
			}
			right_border = last_exchange;
		} while (right_border > 0);
	}
	
	public static void BubbleSortShort1(short[] num) {
		boolean flag = true; // set flag to true to begin first pass
		short temp; // holding variable

		while (flag) {
			flag = false; // set flag to false awaiting a possible swap
			for (int j = 0; j < num.length - 1; j++) {
				if (num[j] > num[j + 1]) // change to > for ascending sort
				{
					temp = num[j]; // swap elements
					num[j] = num[j + 1];
					num[j + 1] = temp;
					flag = true; // shows a swap occurred
				}
			}
		}
	}
	
	public static void BubbleSortShort2(short[] num) {
		int last_exchange;
		int right_border = num.length - 1;
		do {
			last_exchange = 0;
			for (int j = 0; j < num.length - 1; j++) {
				if (num[j] > num[j + 1])
				{
					short temp = num[j];
					num[j] = num[j + 1];
					num[j + 1] = temp;
					last_exchange = j;
				}
			}
			right_border = last_exchange;
		} while (right_border > 0);
	}
	
	public static void BubbleSortByte1(byte[] num) {
		boolean flag = true; // set flag to true to begin first pass
		byte temp; // holding variable

		while (flag) {
			flag = false; // set flag to false awaiting a possible swap
			for (int j = 0; j < num.length - 1; j++) {
				if (num[j] > num[j + 1]) // change to > for ascending sort
				{
					temp = num[j]; // swap elements
					num[j] = num[j + 1];
					num[j + 1] = temp;
					flag = true; // shows a swap occurred
				}
			}
		}
	}
	
	public static void BubbleSortByte2(byte[] num) {
		int last_exchange;
		int right_border = num.length - 1;
		do {
			last_exchange = 0;
			for (int j = 0; j < num.length - 1; j++) {
				if (num[j] > num[j + 1])
				{
					byte temp = num[j];
					num[j] = num[j + 1];
					num[j + 1] = temp;
					last_exchange = j;
				}
			}
			right_border = last_exchange;
		} while (right_border > 0);
	}

	public static <T extends Comparable<T>> void BubbleSortComparable1(T[] num) {
		int j;
		boolean flag = true; // set flag to true to begin first pass
		T temp; // holding variable

		while (flag) {
			flag = false; // set flag to false awaiting a possible swap
			for (j = 0; j < num.length - 1; j++) {
				if (num[j].compareTo(num[j + 1]) > 0) // change to > for ascending sort
				{
					temp = num[j]; // swap elements
					num[j] = num[j + 1];
					num[j + 1] = temp;
					flag = true; // shows a swap occurred
				}
			}
		}
	}
	
	public static <T extends Comparable<T>> void BubbleSortComparable2(T[] num) {
		int last_exchange;
		int right_border = num.length - 1;
		do {
			last_exchange = 0;
			for (int j = 0; j < num.length - 1; j++) {
				if (num[j].compareTo(num[j + 1]) > 0)
				{
					T temp = num[j];
					num[j] = num[j + 1];
					num[j + 1] = temp;
					last_exchange = j;
				}
			}
			right_border = last_exchange;
		} while (right_border > 0);
	}

}
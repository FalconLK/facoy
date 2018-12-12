package database;

public class TransposeMatrix {
	public static int[][] transpose(int[][] m) {
		int[][] retval = new int[m[0].length][m.length];
		for(int i = 0; i < m.length; i++) {
			for(int j = 0; j < m[0].length; j++) {
				retval[j][i] = m[i][j];
			}
		}
		return retval;
	}
//	public static void print(int[][] m) {
//		for(int i = 0; i < m.length; i++) {
//			for(int j = 0; j < m[0].length; j++) {
//				System.out.println("m[" + (i+1) + "][" + (j+1) + "]=" + m[i][j]);
//			}
//		}
//	}
//	public static void main(String args[]) {
//		int[][] om = new int[2][3];
//		om[0][0] = 11;
//		om[0][1] = 12;
//		om[0][2] = 13;
//		om[1][0] = 21;
//		om[1][1] = 22;
//		om[1][2] = 23;
//		System.out.println(om.length);
//		System.out.println(om[0].length);
//		System.out.println("---");
//		TransposeMatrix.print(om);
//		System.out.println("---");
//		TransposeMatrix.print(TransposeMatrix.transpose(om));
//	}
}


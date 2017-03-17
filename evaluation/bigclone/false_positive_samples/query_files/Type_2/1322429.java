package pipe.dataLayer;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class PNMatrix {

    /**
	 * Array for internal storage of elements.
	 * 
	 * @serial internal array storage.
	 */
    private int[][] A;

    /**
	 * Row and column dimensions.
	 * 
	 * @serial row dimension.
	 * @serial column dimension.
	 */
    private int m, n;

    /** Used to determine whether the matrixes have been modified */
    public boolean matrixChanged;

    /**
	 * Construct an m-by-n matrix of zeros.
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 */
    public PNMatrix(int m, int n) {
        this();
        this.m = m;
        this.n = n;
        A = new int[m][n];
    }

    /**
	 * Construct an m-by-n matrix from another PNMatrix.
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 */
    public PNMatrix(PNMatrix B) {
        this();
        this.m = B.m;
        this.n = B.n;
        A = B.A.clone();
    }

    /**
	 * Construct a matrix from a 2-D array.
	 * 
	 * @param A
	 *            Two-dimensional array of integers.
	 * @exception IllegalArgumentException
	 *                All rows must have the same length
	 * @see #constructWithCopy
	 */
    public PNMatrix(int[][] A) {
        this();
        if (A != null) {
            m = A.length;
            if (A.length >= 1) {
                n = A[0].length;
                for (int i = 0; i < m; i++) {
                    if (A[i].length != n) {
                        throw new IllegalArgumentException("All rows must have the same length.");
                    }
                }
                this.A = A;
            }
        }
    }

    /**
	 * Construct a matrix from a one-dimensional packed array
	 * 
	 * @param vals
	 *            One-dimensional array of integers, packed by columns (ala
	 *            Fortran).
	 * @param m
	 *            Number of rows.
	 * @exception IllegalArgumentException
	 *                Array length must be a multiple of m.
	 */
    public PNMatrix(int vals[], int m) {
        this();
        this.m = m;
        n = (m != 0 ? vals.length / m : 0);
        if (m * n != vals.length) {
            throw new IllegalArgumentException("Array length must be a multiple of m.");
        }
        A = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = vals[i + j * m];
            }
        }
    }

    private PNMatrix() {
        matrixChanged = true;
    }

    /**
	 * Append a column matrix (vector) to the right of another matrix.
	 * 
	 * @param X
	 *            Column matrix (vector) to append.
	 * @return The matrix with the column vector appended to the right.
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
    public PNMatrix appendVector(PNMatrix X) {
        PNMatrix R = new PNMatrix(m, n + 1);
        R.setMatrix(0, m - 1, 0, n - 1, this);
        try {
            for (int i = 0; i < m; i++) {
                R.set(i, n, X.get(i, 0));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Row indices incompatible");
        }
        return R;
    }

    /**
	 * Check if a matrix has a row that satisfies the cardinality condition
	 * 1.1.b of the algorithm.
	 * 
	 * @return True if the matrix satisfies the condition and linear combination
	 *         of columns followed by column elimination is required.
	 */
    public int cardinalityCondition() {
        int cardRow = -1;
        int pPlusCard = 0, pMinusCard = 0, countpPlus = 0, countpMinus = 0;
        int[] pPlus, pMinus;
        int m = getRowDimension(), n = getColumnDimension();
        int pLength = n, mLength = n;
        for (int i = 0; i < m; i++) {
            countpPlus = 0;
            countpMinus = 0;
            pPlus = getPositiveIndices(i);
            pMinus = getNegativeIndices(i);
            for (int j = 0; j < pLength; j++) {
                if (pPlus[j] != 0) {
                    countpPlus++;
                }
            }
            for (int j = 0; j < mLength; j++) {
                if (pMinus[j] != 0) {
                    countpMinus++;
                }
            }
            if (countpPlus == 1 || countpMinus == 1) {
                return i;
            }
        }
        return cardRow;
    }

    /**
	 * Find the column index of the element in the pPlus or pMinus set, where
	 * pPlus or pMinus has cardinality == 1.
	 * 
	 * @return The column index, -1 if unsuccessful (this shouldn't happen under
	 *         normal operation).
	 */
    public int cardinalityOne() {
        int k = -1;
        int pPlusCard = 0, pMinusCard = 0, countpPlus = 0, countpMinus = 0;
        int[] pPlus, pMinus;
        int m = getRowDimension(), n = getColumnDimension();
        int pLength = n, mLength = n;
        for (int i = 0; i < m; i++) {
            countpPlus = 0;
            countpMinus = 0;
            pPlus = getPositiveIndices(i);
            pMinus = getNegativeIndices(i);
            for (int j = 0; j < pLength; j++) {
                if (pPlus[j] != 0) {
                    countpPlus++;
                }
            }
            for (int j = 0; j < mLength; j++) {
                if (pMinus[j] != 0) {
                    countpMinus++;
                }
            }
            if (countpPlus == 1) {
                return pPlus[0] - 1;
            }
            if (countpMinus == 1) {
                return pMinus[0] - 1;
            }
        }
        return k;
    }

    /**
	 * Check if a matrix satisfies condition 1.1 of the algorithm.
	 * 
	 * @return True if the matrix satisfies the condition and column elimination
	 *         is required.
	 */
    public boolean checkCase11() {
        boolean satisfies11 = false;
        boolean pPlusEmpty = true, pMinusEmpty = true;
        int[] pPlus, pMinus;
        int m = getRowDimension();
        for (int i = 0; i < m; i++) {
            pPlusEmpty = true;
            pMinusEmpty = true;
            pPlus = getPositiveIndices(i);
            pMinus = getNegativeIndices(i);
            int pLength = pPlus.length, mLength = pMinus.length;
            for (int j = 0; j < pLength; j++) {
                if (pPlus[j] != 0) {
                    pPlusEmpty = false;
                }
            }
            for (int j = 0; j < mLength; j++) {
                if (pMinus[j] != 0) {
                    pMinusEmpty = false;
                }
            }
            if ((pPlusEmpty || pMinusEmpty) && !isZeroRow(i)) {
                return true;
            }
            for (int j = 0; j < pLength; j++) {
                pPlus[j] = 0;
            }
            for (int j = 0; j < mLength; j++) {
                pMinus[j] = 0;
            }
        }
        return satisfies11;
    }

    /**
	 * Find the comlumn indices to be changed by linear combination.
	 * 
	 * @return An array of integers, these are the indices increased by 1 each.
	 */
    public int[] colsToUpdate() {
        int js[] = null;
        int pPlusCard = 0, pMinusCard = 0, countpPlus = 0, countpMinus = 0;
        int[] pPlus, pMinus;
        int m = getRowDimension();
        int n = getColumnDimension();
        int pLength = n, mLength = n;
        for (int i = 0; i < m; i++) {
            countpPlus = 0;
            countpMinus = 0;
            pPlus = getPositiveIndices(i);
            pMinus = getNegativeIndices(i);
            for (int j = 0; j < pLength; j++) {
                if (pPlus[j] != 0) {
                    countpPlus++;
                }
            }
            for (int j = 0; j < mLength; j++) {
                if (pMinus[j] != 0) {
                    countpMinus++;
                }
            }
            if (countpPlus == 1) {
                return pMinus;
            } else if (countpMinus == 1) {
                return pPlus;
            }
        }
        return js;
    }

    /**
	 * Construct a matrix from a copy of a 2-D array.
	 * 
	 * @param A
	 *            Two-dimensional array of integers.
	 * @return The copied matrix.
	 * @exception IllegalArgumentException
	 *                All rows must have the same length
	 */
    public static PNMatrix constructWithCopy(int[][] A) {
        int m = A.length;
        int n = A[0].length;
        PNMatrix X = new PNMatrix(m, n);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++) {
            if (A[i].length != n) {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j];
            }
        }
        return X;
    }

    /**
	 * Make a deep copy of a matrix
	 * 
	 * @return The matrix copy.
	 */
    public PNMatrix copy() {
        PNMatrix X = new PNMatrix(m, n);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j];
            }
        }
        return X;
    }

    /**
	 * Clone the IntMatrix object.
	 * 
	 * @return The clone of the current matrix.
	 */
    public Object clone() {
        return this.copy();
    }

    /**
	 * Eliminate a column from the matrix, column index is toDelete
	 * 
	 * @param toDelete
	 *            The column number to delete.
	 * @return The matrix with the required row deleted.
	 */
    public PNMatrix eliminateCol(int toDelete) {
        int m = getRowDimension(), n = getColumnDimension();
        PNMatrix reduced = new PNMatrix(m, n);
        int[] cols = new int[n - 1];
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (i != toDelete) {
                cols[count++] = i;
            }
        }
        reduced = getMatrix(0, m - 1, cols);
        return reduced;
    }

    /**
	 * Access the internal two-dimensional array.
	 * 
	 * @return Pointer to the two-dimensional array of matrix elements.
	 */
    public int[][] getArray() {
        return A;
    }

    /**
	 * Copy the internal two-dimensional array.
	 * 
	 * @return Two-dimensional array copy of matrix elements.
	 */
    public int[][] getArrayCopy() {
        int[][] C = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j];
            }
        }
        return C;
    }

    /**
	 * Make a one-dimensional column packed copy of the internal array.
	 * 
	 * @return Matrix elements packed in a one-dimensional array by columns.
	 */
    public int[] getColumnPackedCopy() {
        int[] vals = new int[m * n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                vals[i + j * m] = A[i][j];
            }
        }
        return vals;
    }

    /**
	 * Make a one-dimensional row packed copy of the internal array.
	 * 
	 * @return Matrix elements packed in a one-dimensional array by rows.
	 */
    public int[] getRowPackedCopy() {
        int[] vals = new int[m * n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                vals[i * n + j] = A[i][j];
            }
        }
        return vals;
    }

    /**
	 * Get row dimension.
	 * 
	 * @return The number of rows.
	 */
    public int getRowDimension() {
        return m;
    }

    /**
	 * Get column dimension.
	 * 
	 * @return The number of columns.
	 */
    public int getColumnDimension() {
        return n;
    }

    /**
	 * Find the first non-zero row of a matrix.
	 * 
	 * @return Row index (starting from 0 for 1st row) of the first row from top
	 *         that is not only zeros, -1 of there is no such row.
	 */
    public int firstNonZeroRowIndex() {
        int m = getRowDimension();
        int n = getColumnDimension();
        int h = -1;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (get(i, j) != 0) {
                    return i;
                }
            }
        }
        return h;
    }

    /**
	 * Form a matrix with columns the row indices of non-zero elements.
	 * 
	 * @return The matrix with columns the row indices of non-zero elements.
	 *         First row has index 1.
	 */
    public PNMatrix nonZeroIndices() {
        PNMatrix X = new PNMatrix(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (get(i, j) == 0) {
                    X.set(i, j, 0);
                } else {
                    X.set(i, j, i + 1);
                }
            }
        }
        return X;
    }

    /**
	 * Find a column with non-minimal support.
	 * 
	 * @return The column index that has non-minimal support, -1 if there is
	 *         none.
	 */
    public int findNonMinimal() {
        int k = -1;
        int m = getRowDimension(), n = getColumnDimension();
        PNMatrix X = new PNMatrix(m, 1);
        PNMatrix Y = new PNMatrix(m, 1);
        PNMatrix Z = new PNMatrix(m, 1);
        for (int i = 0; i < n; i++) {
            X = getMatrix(0, m - 1, i, i);
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    Y = getMatrix(0, m - 1, j, j);
                    Z = X.minus(Y);
                    if (!(Z.hasNegativeElements())) {
                        return i;
                    }
                }
            }
        }
        return k;
    }

    /**
	 * Find if a column vector has negative elements.
	 * 
	 * @return True or false.
	 */
    public boolean hasNegativeElements() {
        boolean hasNegative = false;
        int m = getRowDimension();
        for (int i = 0; i < m; i++) {
            if (get(i, 0) < 0) {
                return true;
            }
        }
        return hasNegative;
    }

    /**
	 * Find the column index of the first non zero element of row h.
	 * 
	 * @param h
	 *            The row to look for the non-zero element in
	 * @return Column index (starting from 0 for 1st column) of the first
	 *         non-zero element of row h, -1 if there is no such column.
	 */
    public int firstNonZeroElementIndex(int h) {
        int n = getColumnDimension();
        int k = -1;
        for (int j = 0; j < n; j++) {
            if (get(h, j) != 0) {
                return j;
            }
        }
        return k;
    }

    /**
	 * Find the column indices of all but the first non zero elements of row h.
	 * 
	 * @param h
	 *            The row to look for the non-zero element in
	 * @return Array of ints of column indices (starting from 0 for 1st column)
	 *         of all but the first non-zero elements of row h.
	 */
    public int[] findRemainingNZIndices(int h) {
        int n = getColumnDimension();
        int[] k = new int[n];
        int count = 0;
        for (int j = 1; j < n; j++) {
            if (get(h, j) != 0) k[count++] = j;
        }
        return k;
    }

    /**
	 * Find the coefficients corresponding to column indices of all but the
	 * first non zero elements of row h.
	 * 
	 * @param h
	 *            The row to look for the non-zero coefficients in
	 * @return Array of ints of coefficients of all but the first non-zero
	 *         elements of row h.
	 */
    public int[] findRemainingNZCoef(int h) {
        int n = getColumnDimension();
        int[] k = new int[n];
        int count = 0;
        int anElement;
        for (int j = 1; j < n; j++) {
            if ((anElement = get(h, j)) != 0) {
                k[count++] = anElement;
            }
        }
        return k;
    }

    /**
	 * Get a single element.
	 * 
	 * @param i
	 *            Row index.
	 * @param j
	 *            Column index.
	 * @return A(i,j)
	 * @exception ArrayIndexOutOfBoundsException
	 */
    public int get(int i, int j) {
        return A[i][j];
    }

    /**
	 * Get a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @return A(i0:i1,j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
    public PNMatrix getMatrix(int i0, int i1, int j0, int j1) {
        PNMatrix X = new PNMatrix(i1 - i0 + 1, j1 - j0 + 1);
        int[][] B = X.getArray();
        try {
            for (int i = i0; i <= i1; i++) {
                for (int j = j0; j <= j1; j++) {
                    B[i - i0][j - j0] = A[i][j];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
	 * Get a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @return A(r(:),c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
    public PNMatrix getMatrix(int[] r, int[] c) {
        PNMatrix X = new PNMatrix(r.length, c.length);
        int[][] B = X.getArray();
        try {
            for (int i = 0; i < r.length; i++) {
                for (int j = 0; j < c.length; j++) {
                    B[i][j] = A[r[i]][c[j]];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
	 * Get a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param c
	 *            Array of column indices.
	 * @return A(i0:i1,c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
    public PNMatrix getMatrix(int i0, int i1, int[] c) {
        PNMatrix X = new PNMatrix(i1 - i0 + 1, c.length);
        int[][] B = X.getArray();
        try {
            for (int i = i0; i <= i1; i++) {
                for (int j = 0; j < c.length; j++) {
                    B[i - i0][j] = A[i][c[j]];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
	 * Get a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @return A(r(:),j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
    public PNMatrix getMatrix(int[] r, int j0, int j1) {
        PNMatrix X = new PNMatrix(r.length, j1 - j0 + 1);
        int[][] B = X.getArray();
        try {
            for (int i = 0; i < r.length; i++) {
                for (int j = j0; j <= j1; j++) {
                    B[i][j - j0] = A[r[i]][j];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
	 * For row rowNo of the matrix received return the column indices of all the
	 * negative elements
	 * 
	 * @param rowNo
	 *            row iside the Matrix to check for -ve elements
	 * @return Integer array of indices of negative elements.
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
    public int[] getNegativeIndices(int rowNo) {
        int n = getColumnDimension();
        try {
            PNMatrix A = new PNMatrix(1, n);
            A = getMatrix(rowNo, rowNo, 0, n - 1);
            int count = 0;
            int[] negativesArray = new int[n];
            for (int i = 0; i < n; i++) negativesArray[i] = 0;
            for (int i = 0; i < n; i++) {
                if (A.get(0, i) < 0) negativesArray[count++] = i + 1;
            }
            return negativesArray;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
	 * For row rowNo of the matrix received return the column indices of all the
	 * positive elements
	 * 
	 * @param rowNo
	 *            row iside the Matrix to check for +ve elements
	 * @return The integer array of indices of all positive elements.
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
    public int[] getPositiveIndices(int rowNo) {
        int n = getColumnDimension();
        try {
            PNMatrix A = new PNMatrix(1, n);
            A = getMatrix(rowNo, rowNo, 0, n - 1);
            int count = 0;
            int[] positivesArray = new int[n];
            for (int i = 0; i < n; i++) positivesArray[i] = 0;
            for (int i = 0; i < n; i++) {
                if (A.get(0, i) > 0) positivesArray[count++] = i + 1;
            }
            return positivesArray;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
	 * Check if a matrix is all zeros.
	 * 
	 * @return true if all zeros, false otherwise
	 */
    public boolean isZeroMatrix() {
        int m = getRowDimension();
        int n = getColumnDimension();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (get(i, j) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
	 * isZeroRow returns true if the ith row is all zeros
	 * 
	 * @param r
	 *            row to check for full zeros.
	 * @return true if the row is full of zeros.
	 */
    public boolean isZeroRow(int r) {
        PNMatrix A = new PNMatrix(1, getColumnDimension());
        A = getMatrix(r, r, 0, getColumnDimension() - 1);
        return A.isZeroMatrix();
    }

    /**
	 * Find if a matrix of invariants is covered.
	 * 
	 * @return true if it is covered, false otherwise.
	 */
    public boolean isCovered() {
        for (int i = 0; i < m; i++) {
            if (isZeroRow(i) || this.transpose().hasNegativeElements()) {
                return false;
            }
        }
        return true;
    }

    /**
	 * Add a linear combination of column k to columns in array j[].
	 * 
	 * @param k
	 *            Column index to add.
	 * @param chk
	 *            Coefficient of col to add
	 * @param j
	 *            Array of column indices to add to.
	 * @param jC
	 *            Array of coefficients of column indices to add to.
	 * @exception ArrayIndexOutOfBoundsException
	 */
    public void linearlyCombine(int k, int chk, int[] j, int[] jC) {
        int chj = 0;
        int m = getRowDimension();
        for (int i = 0; i < j.length; i++) {
            if (j[i] != 0) {
                chj = jC[i];
                for (int w = 0; w < m; w++) {
                    set(w, j[i] - 1, chj * get(w, k) + chk * get(w, j[i] - 1));
                }
            }
        }
    }

    /**
	 * Add a linear combination of column k to columns in array j[].
	 * 
	 * @param k
	 *            Column index to add.
	 * @param alpha
	 *            Array of coefficients of col to add
	 * @param j
	 *            Array of column indices to add to.
	 * @param beta
	 *            Array of coefficients of column indices to add to.
	 * @exception ArrayIndexOutOfBoundsException
	 */
    public void linearlyCombine(int k, int[] alpha, int[] j, int[] beta) {
        int m = getRowDimension(), n = j.length;
        for (int i = 0; i < n; i++) {
            if (j[i] != 0) {
                for (int w = 0; w < m; w++) {
                    set(w, j[i], alpha[i] * get(w, k) + beta[i] * get(w, j[i]));
                }
            }
        }
    }

    /**
	 * Find the first row with a negative element in a matrix.
	 * 
	 * @return Row index (starting from 0 for 1st row) of the first row from top
	 *         that is has a negative element, -1 of there is no such row.
	 */
    public int rowWithNegativeElement() {
        int m = getRowDimension();
        int n = getColumnDimension();
        int h = -1;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (get(i, j) < 0) return i;
            }
        }
        return h;
    }

    /**
	 * Set a single element.
	 * 
	 * @param i
	 *            Row index.
	 * @param j
	 *            Column index.
	 * @param s
	 *            A(i,j).
	 * @exception ArrayIndexOutOfBoundsException
	 */
    public void set(int i, int j, int s) {
        A[i][j] = s;
    }

    /**
	 * Set a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @param X
	 *            A(i0:i1,j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
    public void setMatrix(int i0, int i1, int j0, int j1, PNMatrix X) {
        try {
            for (int i = i0; i <= i1; i++) {
                for (int j = j0; j <= j1; j++) {
                    A[i][j] = X.get(i - i0, j - j0);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
	 * Set a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @param X
	 *            A(r(:),c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
    public void setMatrix(int[] r, int[] c, PNMatrix X) {
        try {
            for (int i = 0; i < r.length; i++) {
                for (int j = 0; j < c.length; j++) {
                    A[r[i]][c[j]] = X.get(i, j);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
	 * Set a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @param X
	 *            A(r(:),j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
    public void setMatrix(int[] r, int j0, int j1, PNMatrix X) {
        try {
            for (int i = 0; i < r.length; i++) {
                for (int j = j0; j <= j1; j++) {
                    A[r[i]][j] = X.get(i, j - j0);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
	 * Set a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param c
	 *            Array of column indices.
	 * @param X
	 *            A(i0:i1,c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
    public void setMatrix(int i0, int i1, int[] c, PNMatrix X) {
        try {
            for (int i = i0; i <= i1; i++) {
                for (int j = 0; j < c.length; j++) {
                    A[i][c[j]] = X.get(i - i0, j);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
	 * Matrix transpose.
	 * 
	 * @return A'
	 */
    public PNMatrix transpose() {
        PNMatrix X = new PNMatrix(n, m);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[j][i] = A[i][j];
            }
        }
        return X;
    }

    /**
	 * Find the greatest common divisor of a column matrix (vector) of integers.
	 * 
	 * @return The gcd of the column matrix.
	 */
    public int gcd() {
        int gcd = A[0][0];
        for (int i = 1; i < m; i++) {
            if ((A[i][0] != 0) || (gcd != 0)) {
                gcd = gcd2(gcd, A[i][0]);
            }
        }
        return gcd;
    }

    /**
	 * Find the greatest common divisor of 2 integers.
	 * 
	 * @param a
	 *            The first integer.
	 * @param b
	 *            The second integer.
	 * @return The gcd of the column
	 */
    private int gcd2(int a, int b) {
        int gcd;
        a = Math.abs(a);
        b = Math.abs(b);
        if (b <= a) {
            int tmp = b;
            b = a;
            a = tmp;
        }
        if (a != 0) {
            for (int tmp; (b %= a) != 0; ) {
                tmp = b;
                b = a;
                a = tmp;
            }
            gcd = a;
        } else if (b != 0) {
            gcd = b;
        } else {
            gcd = 0;
        }
        return gcd;
    }

    /**
	 * uminus() Unary minus
	 * 
	 * @return - A
	 */
    public PNMatrix uminus() {
        PNMatrix X = new PNMatrix(m, n);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = -A[i][j];
            }
        }
        return X;
    }

    /**
	 * C = A + B
	 * 
	 * @param B
	 *            another matrix
	 * @return A + B
	 */
    public PNMatrix plus(PNMatrix B) {
        checkMatrixDimensions(B);
        PNMatrix X = new PNMatrix(m, n);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] + B.A[i][j];
            }
        }
        return X;
    }

    /**
	 * A = A + B
	 * 
	 * @param B
	 *            another matrix
	 * @return A + B
	 */
    public PNMatrix plusEquals(PNMatrix B) {
        checkMatrixDimensions(B);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = A[i][j] + B.A[i][j];
            }
        }
        return this;
    }

    /**
	 * C = A - B
	 * 
	 * @param B
	 *            another matrix
	 * @return A - B
	 */
    public PNMatrix minus(PNMatrix B) {
        checkMatrixDimensions(B);
        int[][] C = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] - B.A[i][j];
            }
        }
        PNMatrix X = new PNMatrix(C);
        return X;
    }

    /**
	 * A = A - B
	 * 
	 * @param B
	 *            another matrix
	 * @return A - B
	 */
    public PNMatrix minusEquals(PNMatrix B) {
        checkMatrixDimensions(B);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = A[i][j] - B.A[i][j];
            }
        }
        return this;
    }

    /**
	 * Multiply a matrix by an int in place, A = s*A
	 * 
	 * @param s
	 *            int multiplier
	 * @return replace A by s*A
	 */
    public PNMatrix timesEquals(int s) {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = s * A[i][j];
            }
        }
        return this;
    }

    /**
	 * Multiply a row matrix by a column matrix, A = s*A
	 * 
	 * @param B
	 *            column vector
	 * @return product of row vector A by column vector B
	 */
    public int vectorTimes(PNMatrix B) {
        int product = 0;
        for (int j = 0; j < n; j++) {
            product += A[0][j] * B.get(j, 0);
        }
        return product;
    }

    /**
	 * Divide a matrix by an int in place, A = s*A
	 * 
	 * @param s
	 *            int divisor
	 * @return replace A by A/s
	 */
    public PNMatrix divideEquals(int s) {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = A[i][j] / s;
            }
        }
        return this;
    }

    /**
	 * Generate identity matrix]
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @return An m-by-n matrix with ones on the diagonal and zeros elsewhere.
	 */
    public static PNMatrix identity(int m, int n) {
        PNMatrix A = new PNMatrix(m, n);
        int[][] X = A.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                X[i][j] = (i == j ? 1 : 0);
            }
        }
        return A;
    }

    /**
	 * Print the matrix to stdout. Line the elements up in columns with a
	 * Fortran-like 'Fw.d' style format.
	 * 
	 * @param w
	 *            Column width.
	 * @param d
	 *            Number of digits after the decimal.
	 */
    public void print(int w, int d) {
        print(new PrintWriter(System.out, true), w, d);
    }

    /**
	 * Print the matrix to a string. Line the elements up in columns with a
	 * Fortran-like 'Fw.d' style format.
	 * 
	 * @param w
	 *            Column width.
	 * @param d
	 *            Number of digits after the decimal.
	 * @return The formated string to output.
	 */
    public String printString(int w, int d) {
        if (isZeroMatrix()) {
            return "\nNone\n\n";
        }
        ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
        print(new PrintWriter(arrayStream, true), w, d);
        return arrayStream.toString();
    }

    /**
	 * Print the matrix to the output stream. Line the elements up in columns
	 * with a Fortran-like 'Fw.d' style format.
	 * 
	 * @param output
	 *            Output stream.
	 * @param w
	 *            Column width.
	 * @param d
	 *            Number of digits after the decimal.
	 */
    public void print(PrintWriter output, int w, int d) {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.UK));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        print(output, format, w + 2);
    }

    /**
	 * Print the matrix to stdout. Line the elements up in columns. Use the
	 * format object, and right justify within columns of width characters. Note
	 * that if the matrix is to be read back in, you probably will want to use a
	 * NumberFormat that is set to UK Locale.
	 * 
	 * @param format
	 *            A Formatting object for individual elements.
	 * @param width
	 *            Field width for each column.
	 * @see java.text.DecimalFormat#setDecimalFormatSymbols
	 */
    public void print(NumberFormat format, int width) {
        print(new PrintWriter(System.out, true), format, width);
    }

    /**
	 * Print the matrix to the output stream. Line the elements up in columns.
	 * Use the format object, and right justify within columns of width
	 * characters. Note that is the matrix is to be read back in, you probably
	 * will want to use a NumberFormat that is set to US Locale.
	 * 
	 * @param output
	 *            the output stream.
	 * @param format
	 *            A formatting object to format the matrix elements
	 * @param width
	 *            Column width.
	 * @see java.text.DecimalFormat#setDecimalFormatSymbols
	 */
    public void print(PrintWriter output, NumberFormat format, int width) {
        output.println();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String s = format.format(A[i][j]);
                int padding = Math.max(1, width - s.length());
                for (int k = 0; k < padding; k++) {
                    output.print(' ');
                }
                output.print(s);
            }
            output.println();
        }
        output.println();
    }

    /**
	 * Throws IllegalArgumentException if dimensions of A and B differ.
	 * 
	 * @param B
	 *            The matrix to check the dimensions.
	 */
    private void checkMatrixDimensions(PNMatrix B) {
        if (B.m != m || B.n != n) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
    }

    public void setToZero() {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = 0;
            }
        }
    }

    public int[] getColumn(int i) {
        int[] r = new int[this.getColumnDimension()];
        for (int j = 0; j < this.getColumnDimension(); j++) {
            r[j] = A[i][j];
        }
        return r;
    }

    public int[] getRow(int i) {
        int[] r = new int[this.getRowDimension()];
        for (int j = 0; j < this.getRowDimension(); j++) {
            r[j] = A[j][i];
        }
        return r;
    }

    /**
    */
    public void clearColumn(int place) {
        for (int j = 0; j < this.getColumnDimension(); j++) {
            A[place][j] = 0;
        }
    }
}

package net.maizegenetics.pal.math;

/**
 * <p>Title: Matrix </p>
 * <p>Description: A very simple implementation of a general matrix. Not really that useful. Will probably become an interface at some point, with this class as a default implementation (so that users may switch to alternative libraries such as Jama)</p>
 * @author Matthew Goode
 * @version 1.0
 */
public final class Matrix {

    private final int width_;

    private final int height_;

    private final double[][] data_;

    public Matrix(int size, boolean identity) {
        this(size, size);
        if (identity) {
            for (int i = 0; i < size; i++) {
                setValue(i, i, 1);
            }
        }
    }

    public Matrix(int width, int height) {
        this.data_ = new double[height][width];
        this.width_ = width;
        this.height_ = height;
    }

    public double[] toArray() {
        double[] result = new double[width_ * height_];
        int index = 0;
        for (int row = 0; row < height_; row++) {
            for (int col = 0; col < width_; col++) {
                result[index++] = data_[row][col];
            }
        }
        return result;
    }

    public Matrix(double[][] data) {
        double[][] copy = new double[data.length][];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = new double[data[i].length];
            System.arraycopy(data[i], 0, copy[i], 0, data[i].length);
        }
        this.data_ = copy;
        this.height_ = data.length;
        this.width_ = data[0].length;
    }

    private Matrix(Matrix toCopy) {
        this(toCopy.data_);
    }

    public final void setValue(final int row, final int col, final double value) {
        data_[row][col] = value;
    }

    public final double getValue(final int row, final int col) {
        return data_[row][col];
    }

    public final boolean isSquare() {
        return width_ == height_;
    }

    public final int getWidth() {
        return width_;
    }

    public final int getHeight() {
        return height_;
    }

    /**
	 * @return a new Matrix that is this matrix with other appended on the end (eg [this | other ])
	 */
    public Matrix getAppendedHorizontally(Matrix other) {
        if (other.height_ != this.height_) {
            throw new IllegalArgumentException("Height not same!");
        }
        double[][] otherData = other.data_;
        double[][] newData = new double[height_][width_ + other.width_];
        for (int y = 0; y < height_; y++) {
            for (int x = 0; x < width_; x++) {
                newData[y][x] = data_[y][x];
            }
            for (int x = 0; x < other.width_; x++) {
                newData[y][x + width_] = otherData[y][x];
            }
        }
        return new Matrix(newData);
    }

    /**
	 * @return a new Matrix that is this matrix with other appended on the bottom (eg [this / other ]
	 */
    public Matrix getAppendedVertically(Matrix other) {
        if (other.width_ != this.width_) {
            throw new IllegalArgumentException("Width not same!");
        }
        double[][] otherData = other.data_;
        double[][] newData = new double[height_ + other.height_][width_];
        for (int x = 0; x < width_; x++) {
            for (int y = 0; y < height_; y++) {
                newData[y][x] = data_[y][x];
            }
            for (int y = 0; y < other.height_; y++) {
                newData[y + height_][x] = otherData[y][x];
            }
        }
        return new Matrix(newData);
    }

    /**
	 * Returns a new Matrix that is formed from a subset of the colums of this matrix
	 * @param startColumn the first column to include in new Matrix
	 * @param numberToKeep the number of columns to keep
	 */
    public Matrix getSubsetColumns(int startColumn, int numberToKeep) {
        double[][] newData = new double[height_][numberToKeep];
        for (int row = 0; row < height_; row++) {
            for (int i = 0; i < numberToKeep; i++) {
                newData[row][i] = data_[row][i + startColumn];
            }
        }
        return new Matrix(newData);
    }

    public final void transpose() {
        if (!isSquare()) {
            throw new RuntimeException("Cannot transpose no square matrix!");
        }
        for (int row = 0; row < height_; row++) {
            for (int col = row + 1; col < width_; col++) {
                double temp = data_[row][col];
                data_[row][col] = data_[col][row];
                data_[col][row] = temp;
            }
        }
    }

    public final Matrix getTranspose() {
        double[][] newData = new double[width_][height_];
        for (int row = 0; row < height_; row++) {
            for (int col = 0; col < width_; col++) {
                newData[col][row] = data_[row][col];
            }
        }
        return new Matrix(newData);
    }

    public final void multiply(double scale) {
        for (int row = 0; row < height_; row++) {
            for (int col = 0; col < width_; col++) {
                data_[row][col] *= scale;
            }
        }
    }

    /**
	 *  Multiply as in [this][other]
	 */
    public final Matrix getMultiplied(Matrix other) {
        if (width_ != other.height_) {
            throw new IllegalArgumentException("Other matrix wrong size!");
        }
        final double[][] otherData = other.data_;
        final double[][] newData = new double[height_][other.width_];
        for (int row = 0; row < height_; row++) {
            for (int otherCol = 0; otherCol < other.width_; otherCol++) {
                double total = 0;
                for (int col = 0; col < width_; col++) {
                    total += data_[row][col] * otherData[col][otherCol];
                }
                newData[row][otherCol] = total;
            }
        }
        return new Matrix(newData);
    }

    public final Matrix getMultiplied(double scale) {
        Matrix m = getMatrixCopy();
        m.multiply(scale);
        return m;
    }

    /**
	 * Obtains the inverse of a matrix by appending identity and doing row reduction. May not be the most
	 * accurate way of doing things (errors tend to accumulate)
	 * @return The inverse of this matrix
	 */
    public Matrix getInverse() {
        if (!isSquare()) {
            throw new RuntimeException("Can invert non square matrix");
        }
        Matrix combined = getAppendedHorizontally(new Matrix(height_, true));
        combined.rowReduce();
        return combined.getSubsetColumns(width_, width_);
    }

    private void scaleRow(int row, double scale) {
        final double[] rowData = data_[row];
        for (int i = 0; i < width_; i++) {
            rowData[i] *= scale;
        }
    }

    private void divideRow(int row, double factor) {
        final double[] rowData = data_[row];
        for (int i = 0; i < width_; i++) {
            rowData[i] /= factor;
        }
    }

    /**
	 * Adds values in row from to row to (changes row to)
	 */
    private void addRow(int fromRow, int toRow) {
        final double[] fromRowData = data_[fromRow];
        final double[] toRowData = data_[toRow];
        for (int i = 0; i < width_; i++) {
            toRowData[i] += fromRowData[i];
        }
    }

    /**
	 * Subtracts values in row from to row to (changes row to)
	 */
    private void subtractRow(int fromRow, int toRow) {
        final double[] fromRowData = data_[fromRow];
        final double[] toRowData = data_[toRow];
        for (int i = 0; i < width_; i++) {
            toRowData[i] -= fromRowData[i];
        }
    }

    /**
	 * Subtracts values in row from to row to (changes row to)
	 * @param scale a scale value. For each value in fromRow, x, x is multiplied by scale before
	 * being subtracted from toRow
	 */
    private void subtractRow(int fromRow, double scale, int toRow) {
        final double[] fromRowData = data_[fromRow];
        final double[] toRowData = data_[toRow];
        for (int i = 0; i < width_; i++) {
            toRowData[i] -= scale * fromRowData[i];
        }
    }

    private static final boolean equalsZero(final double value) {
        return ((value < 0.0000001) && (value > -0.0000001));
    }

    private void swapRow(int rowOne, int rowTwo) {
        double[] temp = data_[rowOne];
        data_[rowOne] = data_[rowTwo];
        data_[rowTwo] = temp;
    }

    /**
		* Check forward in rows after start row until we find one with a \
		* non zero value at targetColumn and then swap with startRow
		* @return true if successful, or false if no row with zero at required position
		*/
    private boolean swapZeroRow(int startRow, int targetColumn) {
        for (int check = startRow + 1; check < height_; check++) {
            if (!equalsZero(getValue(check, targetColumn))) {
                swapRow(startRow, check);
                return true;
            }
        }
        return false;
    }

    public void rowReduce() {
        int extent = Math.min(width_, height_);
        for (int reduce = 0; reduce < extent; reduce++) {
            boolean doColumn = true;
            double primaryFactor = getValue(reduce, reduce);
            if (equalsZero(primaryFactor)) {
                if (swapZeroRow(reduce, reduce)) {
                    doColumn = false;
                } else {
                    primaryFactor = getValue(reduce, reduce);
                }
            }
            if (doColumn) {
                divideRow(reduce, primaryFactor);
                for (int row = 0; row < reduce; row++) {
                    double subScale = getValue(row, reduce);
                    if (!equalsZero(subScale)) {
                        subtractRow(reduce, subScale, row);
                    }
                }
                for (int row = reduce + 1; row < height_; row++) {
                    double subFactor = getValue(row, reduce);
                    if (equalsZero(subFactor)) {
                        if (!swapZeroRow(row, reduce)) {
                            break;
                        }
                        subFactor = getValue(row, reduce);
                    }
                    divideRow(row, subFactor);
                    subtractRow(reduce, row);
                }
            }
        }
    }

    /**
	 * Peforms a simple row reduction tramsformation
	 * @return A row reduced version of this matrix
	 */
    public Matrix getRowReduced() {
        Matrix m = getMatrixCopy();
        m.rowReduce();
        return m;
    }

    /**
	 * Cloning
	 * @return an exact copy of this matrix
	 */
    public Matrix getMatrixCopy() {
        return new Matrix(this);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('(');
        sb.append(width_);
        sb.append(", ");
        sb.append(height_);
        sb.append(")\n");
        for (int row = 0; row < height_; row++) {
            sb.append('[');
            for (int col = 0; col < width_; col++) {
                sb.append(getValue(row, col));
                sb.append("  ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
}

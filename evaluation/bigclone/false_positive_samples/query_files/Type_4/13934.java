public class MatrixMath {

    public static double getDistanceSqr(double[] v1, double[] v2) {
        double sum = 0;
        for (int i = 0; i < v1.length; i++) {
            double cD = v1[i] - v2[i];
            sum += cD * cD;
        }
        return sum;
    }

    public static double getDistance(double[] v1, double[] v2) {
        double dsqr = getDistanceSqr(v1, v2);
        return Math.sqrt(dsqr);
    }

    public static double[] getCol(double[][] v, int iCol) {
        double[] col = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            col[i] = v[i][iCol];
        }
        return col;
    }

    public static double[][] multiply(double[][] v1, double[][] v2) {
        double[][] result = new double[v1.length][v2[0].length];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                double[] row = v1[i];
                double[] col = getCol(v2, j);
                double sum = 0;
                for (int ii = 0; ii < row.length; ii++) {
                    sum += row[ii] * col[ii];
                }
                result[i][j] = sum;
            }
        }
        return result;
    }

    public static double[] multiply(double[] v1, double[][] v2) {
        double[] result = new double[v2[0].length];
        for (int j = 0; j < result.length; j++) {
            double[] col = getCol(v2, j);
            double sum = 0;
            for (int ii = 0; ii < v1.length; ii++) {
                sum += v1[ii] * col[ii];
            }
            result[j] = sum;
        }
        return result;
    }

    public static double[] multiplyElementWise(double[] v1, double[] v2) {
        double[] result = new double[v1.length];
        for (int i = 0; i < v1.length; i++) {
            result[i] = v1[i] * v2[i];
        }
        return result;
    }

    public static double[][] multiplyElementWise(double[][] v1, double[][] v2) {
        double[][] result = new double[v1.length][v1[0].length];
        for (int i = 0; i < v1.length; i++) {
            for (int j = 0; j < v1[0].length; j++) {
                result[i][j] = v1[i][j] * v2[i][j];
            }
        }
        return result;
    }

    public static double[] subtract(double[] v1, double[] v2) {
        double[] result = new double[v1.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = v1[i] - v2[i];
        }
        return result;
    }

    public static double[] add(double[] v1, double[] v2) {
        double[] result = new double[v1.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = v1[i] + v2[i];
        }
        return result;
    }

    public static double[][] subtractFromRows(double[][] values, double[] a) {
        double[][] result = new double[values.length][values[0].length];
        for (int i = 0; i < values.length; i++) {
            result[i] = subtract(values[i], a);
        }
        return result;
    }

    public static double[][] transpose(double[][] values) {
        double[][] swapValues = new double[values[0].length][values.length];
        for (int x = 0; x < values.length; x++) {
            for (int y = 0; y < values[x].length; y++) {
                swapValues[y][x] = values[x][y];
            }
        }
        return swapValues;
    }

    public static double[][] normalise(double[][] v) {
        double[][] result = new double[v.length][v[0].length];
        double[] mvals = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            mvals[i] = max(v[i]);
        }
        for (int i = 0; i < v.length; i++) {
            for (int j = 0; j < v[0].length; j++) {
                result[i][j] = v[i][j] / mvals[i];
            }
        }
        return result;
    }

    public static double min(double[][] arr) {
        double m = Double.MAX_VALUE;
        for (int i = 0; i < arr.length; i++) {
            m = min(arr[i]);
        }
        return m;
    }

    public static double min(double[] arr) {
        double m = Double.MAX_VALUE;
        for (int i = 0; i < arr.length; i++) {
            m = Math.min(m, arr[i]);
        }
        return m;
    }

    public static double max(double[][] arr) {
        double m = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < arr.length; i++) {
            m = max(arr[i]);
        }
        return m;
    }

    public static double max(double[] arr) {
        double m = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < arr.length; i++) {
            m = Math.max(m, arr[i]);
        }
        return m;
    }

    public static double mag(double[] oneDArray) {
        double val = 0.0;
        for (int i = 0; i < oneDArray.length; i++) {
            val += (oneDArray[i] * oneDArray[i]);
        }
        return Math.sqrt(val);
    }

    public static double[] flatten(double[][] values) {
        double[] res = new double[values.length * values[0].length];
        int i = 0;
        for (int r = 0; r < values.length; r++) {
            for (int c = 0; c < values[0].length; c++) {
                res[i] = values[r][c];
                i++;
            }
        }
        return res;
    }

    public static double[] getAverage(double[][] values) {
        double[] average = new double[values[0].length];
        for (int i = 0; i < values[0].length; i++) {
            double sum = 0;
            for (int ii = 0; ii < values.length; ii++) {
                sum += values[ii][i];
            }
            average[i] = sum / values.length;
        }
        return average;
    }

    public static double[] normalizeLength(double[] values) {
        double[] result = new double[values.length];
        double l = mag(values);
        for (int i = 0; i < result.length; i++) {
            result[i] = values[i] / l;
        }
        return result;
    }

    public static double[][] normalizeRows(double[][] values) {
        double[][] result = new double[values.length][values[0].length];
        for (int i = 0; i < values.length; i++) {
            result[i] = normalizeLength(values[i]);
        }
        return result;
    }

    public static double[][] getSubMatrix(double[][] values, int row1, int row2) {
        double[][] newValues = new double[row2 - row1][];
        for (int i = row1; i < row2; i++) {
            newValues[i] = values[i];
        }
        return newValues;
    }

    public static double[] getSubMatrix(double[] values, int row1, int row2) {
        double[] newValues = new double[row2 - row1];
        for (int i = row1; i < row2; i++) {
            newValues[i] = values[i];
        }
        return newValues;
    }

    public static double[] diag(double[][] m) {
        double[] d = new double[m.length];
        for (int i = 0; i < m.length; i++) d[i] = m[i][i];
        return d;
    }

    public static void print(double[] values) {
        for (int i = 0; i < values.length; i++) {
            System.out.println("i: " + values[i]);
        }
    }
}

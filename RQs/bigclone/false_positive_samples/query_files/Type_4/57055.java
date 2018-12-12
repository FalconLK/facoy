import java.util.*;
import sis.studentinfo.*;

public class MiscTest extends junit.framework.TestCase {

    public static boolean isPalindrome(String string) {
        if (string.length() == 0) return true;
        int limit = string.length() / 2;
        for (int forward = 0, backward = string.length() - 1; forward < limit; forward++, backward--) if (string.charAt(forward) != string.charAt(backward)) return false;
        return true;
    }

    private int fib(int x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        return fib(x - 1) + fib(x - 2);
    }

    public void testPalindrome() {
        assertFalse(isPalindrome("abcdef"));
        assertFalse(isPalindrome("abccda"));
        assertTrue(isPalindrome("abccba"));
        assertFalse(isPalindrome("abcxba"));
        assertTrue(isPalindrome("a"));
        assertTrue(isPalindrome("aa"));
        assertFalse(isPalindrome("ab"));
        assertTrue(isPalindrome(""));
        assertTrue(isPalindrome("aaa"));
        assertTrue(isPalindrome("aba"));
        assertTrue(isPalindrome("abbba"));
        assertTrue(isPalindrome("abba"));
        assertFalse(isPalindrome("abbaa"));
        assertFalse(isPalindrome("abcda"));
    }

    public void testForSkip() {
        StringBuilder builder = new StringBuilder();
        String string = "123456";
        for (int i = 0; i < string.length(); i += 2) builder.append(string.charAt(i));
        assertEquals("135", builder.toString());
    }

    public void testCommas() {
        String sequence = "1,2,3,4,5";
        assertEquals(sequence, sequenceUsingDo(1, 5));
        assertEquals(sequence, sequenceUsingFor(1, 5));
        assertEquals(sequence, sequenceUsingWhile(1, 5));
        sequence = "8";
        assertEquals(sequence, sequenceUsingDo(8, 8));
        assertEquals(sequence, sequenceUsingFor(8, 8));
        assertEquals(sequence, sequenceUsingWhile(8, 8));
    }

    String sequenceUsingDo(int start, int stop) {
        StringBuilder builder = new StringBuilder();
        int i = start;
        do {
            if (i > start) builder.append(',');
            builder.append(i);
        } while (++i <= stop);
        return builder.toString();
    }

    String sequenceUsingFor(int start, int stop) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i <= stop; i++) {
            if (i > start) builder.append(',');
            builder.append(i);
        }
        return builder.toString();
    }

    String sequenceUsingWhile(int start, int stop) {
        StringBuilder builder = new StringBuilder();
        int i = start;
        while (i <= stop) {
            if (i > start) builder.append(',');
            builder.append(i);
            i++;
        }
        return builder.toString();
    }

    public void testEndTrim() {
        assertEquals("", endTrim(""));
        assertEquals("  x", endTrim("  x  "));
        assertEquals("y", endTrim("y"));
        assertEquals("xaxa", endTrim("xaxa"));
        assertEquals("", endTrim(" "));
        assertEquals("xxx", endTrim("xxx      "));
    }

    public String endTrim(String source) {
        int i = source.length();
        while (--i >= 0) if (source.charAt(i) != ' ') break;
        return source.substring(0, i + 1);
    }

    public void testLabeledBreak() {
        List<List<String>> table = new ArrayList<List<String>>();
        List<String> row1 = new ArrayList<String>();
        row1.add("5");
        row1.add("2");
        List<String> row2 = new ArrayList<String>();
        row2.add("3");
        row2.add("4");
        table.add(row1);
        table.add(row2);
        assertTrue(found(table, "3"));
        assertFalse(found(table, "8"));
    }

    private boolean found(List<List<String>> table, String target) {
        boolean found = false;
        search: for (List<String> row : table) {
            for (String value : row) {
                if (value.equals(target)) {
                    found = true;
                    break search;
                }
            }
        }
        return found;
    }

    public void testCasting() {
        List students = new ArrayList();
        students.add(new Student("a"));
        students.add(new Student("b"));
        List names = new ArrayList();
        Iterator it = students.iterator();
        while (it.hasNext()) {
            Student student = (Student) it.next();
            names.add(student.getLastName());
        }
        assertEquals("a", names.get(0));
        assertEquals("b", names.get(1));
    }

    public void testUnboxing() {
        int x = new Integer(5);
        assertEquals(5, x);
    }

    public void testUnboxingMath() {
        assertEquals(10, new Integer(2) * new Integer(5));
    }

    public void testTwoDimensionalArrays() {
        final int rows = 3;
        final int cols = 4;
        int count = 0;
        int[][] matrix = new int[rows][cols];
        for (int x = 0; x < rows; x++) for (int y = 0; y < cols; y++) matrix[x][y] = count++;
        assertEquals(11, matrix[2][3]);
        assertEquals(6, matrix[1][2]);
    }

    public void testPartialDimensions() {
        final int rows = 3;
        int[][] matrix = new int[rows][];
        matrix[0] = new int[] { 0 };
        matrix[1] = new int[] { 1, 2 };
        matrix[2] = new int[] { 3, 4, 5 };
        assertEquals(1, matrix[1][0]);
        assertEquals(5, matrix[2][2]);
        int[][] matrix2 = { { 0 }, { 1, 2 }, { 3, 4, 5 } };
        assertEquals(1, matrix2[1][0]);
        assertEquals(5, matrix2[2][2]);
        int[] z1 = { 3, 4, 5 };
        int[] z2 = { 3, 4, 5 };
        assertTrue(Arrays.equals(z1, z2));
    }

    public void testArrayEquality() {
        int[] a = { 1, 2, 3 };
        int[] b = { 1, 2, 3 };
        assertFalse(a.equals(b));
        assertFalse(a == b);
    }

    public void testArraysEquals() {
        int[] a = { 1, 2, 3 };
        int[] b = { 1, 2, 3 };
        assertTrue(Arrays.equals(a, b));
    }

    public void testArraysAsList() {
        String[] n = { "a", "b", "c" };
        List x = Arrays.asList(n);
        n[1] = "z";
    }

    public void testRetroCollections() {
        {
            List names = new Vector();
            Map dictionary = new Hashtable();
        }
        {
            List<String> names = new Vector<String>();
            Map<String, String> dictionary = new Hashtable<String, String>();
        }
    }
}

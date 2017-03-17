import java.io.*;

/**
 * Dummy javadoc comment.
 * @author jjg
 * @see DoesNotExist
 */
public class TestStdDoclet {

    public static void main(String... args) throws Exception {
        new TestStdDoclet().run();
    }

    /**
     * More dummy comments.
     * @throws DoesNotExist   oops, javadoc does not see this
     * @see DoesNotExist
     */
    void run() throws Exception {
        File javaHome = new File(System.getProperty("java.home"));
        if (javaHome.getName().equals("jre")) javaHome = javaHome.getParentFile();
        File javadoc = new File(new File(javaHome, "bin"), "javadoc");
        File testSrc = new File(System.getProperty("test.src"));
        String thisClassName = TestStdDoclet.class.getName();
        Process p = new ProcessBuilder().command(javadoc.getPath(), "-J-Xbootclasspath:" + System.getProperty("sun.boot.class.path"), "-package", new File(testSrc, thisClassName + ".java").getPath()).redirectErrorStream(true).start();
        int actualDocletWarnCount = 0;
        int reportedDocletWarnCount = 0;
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        try {
            String line;
            while ((line = in.readLine()) != null) {
                System.err.println(line);
                if (line.contains("DoesNotExist")) actualDocletWarnCount++;
                if (line.matches("[0-9]+ warning(s)?")) reportedDocletWarnCount = Integer.valueOf(line.substring(0, line.indexOf(" ")));
            }
        } finally {
            in.close();
        }
        int rc = p.waitFor();
        if (rc != 0) System.err.println("javadoc failed, rc:" + rc);
        int expectedDocletWarnCount = 2;
        checkEqual("actual", actualDocletWarnCount, "expected", expectedDocletWarnCount);
        checkEqual("actual", actualDocletWarnCount, "reported", reportedDocletWarnCount);
    }

    /**
     * Private method should not cause a warning.
     * @see DoesNotExist
     */
    private void checkEqual(String l1, int i1, String l2, int i2) throws Exception {
        if (i1 != i2) throw new Exception(l1 + " warn count, " + i1 + ", does not match " + l2 + " warn count, " + i2);
    }
}

import java.lang.*;
import java.io.*;
import java.util.*;

public class BioSapper {

    static int VERBOSE = 0;

    static Hashtable htDirs;

    static String baseDir;

    static int maxThreads;

    public static void main(String[] args) {
        maxThreads = 2;
        htDirs = new Hashtable(maxThreads);
        BioSapThread[] threads = new BioSapThread[maxThreads];
        for (int i = 0; i < maxThreads; i++) {
            threads[i] = null;
        }
        baseDir = new String(args[0]);
        if (baseDir.endsWith("/")) {
            baseDir = baseDir.substring(0, baseDir.length() - 1);
        }
        int threadCount = 0;
        String newDir = null;
        big_loop: while (true) {
            int i = 0;
            for (i = 0; i < maxThreads; i++) {
                if (threads[i] == null || !threads[i].isAlive()) {
                    if (threads[i] != null) {
                        htDirs.remove(threads[i]);
                    }
                    while ((newDir = lookForNewDirectory()) == null) {
                        try {
                            Thread.sleep(30000);
                        } catch (Exception e) {
                            System.err.println("Problems sleeping");
                        }
                    }
                    threads[i] = new BioSapThread(baseDir + "/" + newDir);
                    htDirs.put(threads[i], newDir);
                    threads[i].start();
                    continue big_loop;
                }
            }
        }
    }

    private static String lookForNewDirectory() {
        File bioSapOut;
        File baseDirObj = new java.io.File(baseDir);
        File[] baseDirFiles = baseDirObj.listFiles();
        if (VERBOSE >= 1) System.out.println("lookForNewDirectory: Starting in base directory " + baseDir);
        for (int i = 0; i < baseDirFiles.length; i++) {
            if (VERBOSE >= 2) System.out.println("  Considering directory entry " + baseDirFiles[i].getName());
            if (baseDirFiles[i].isDirectory()) {
                bioSapOut = new File(baseDirFiles[i].getPath() + "/BioSapOut.xml");
                if (!bioSapOut.exists()) {
                    if (!htDirs.containsValue(baseDirFiles[i].getName())) {
                        return (baseDirFiles[i].getName());
                    }
                } else {
                    if (VERBOSE >= 3) System.out.println("  BioSapOut.xml exists...skipping");
                }
            }
        }
        return null;
    }

    private static String execCmd(String cmd) {
        String msg = new String("");
        boolean cmdOK = true;
        Process proc = null;
        Runtime runtime = Runtime.getRuntime();
        try {
            proc = runtime.exec(cmd);
            InputStreamReader isrError = new InputStreamReader(proc.getErrorStream());
            BufferedReader brError = new BufferedReader(isrError);
            String line = null;
            while ((line = brError.readLine()) != null) {
                System.out.println("ERROR MSG>" + line);
            }
            InputStreamReader isrOutput = new InputStreamReader(proc.getInputStream());
            BufferedReader brOutput = new BufferedReader(isrOutput);
            line = null;
            while ((line = brOutput.readLine()) != null) {
                System.out.println("OUTPUT>" + line);
            }
            int exitVal = proc.waitFor();
            System.out.println("ExitValue: " + exitVal);
        } catch (Exception e) {
            cmdOK = false;
            msg += "Problem executing command: " + cmd + "  DAMN. Exception " + e.toString();
            System.err.println(msg);
        }
        if (cmdOK) {
            msg += "Command: " + cmd + " OK.";
        }
        return msg;
    }
}

;

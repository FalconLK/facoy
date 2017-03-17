package com.windsor.node.service.helper.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import com.windsor.node.service.helper.CompressionService;
import com.windsor.node.service.helper.id.UUIDGenerator;
import com.windsor.node.util.IOUtil;

public class ZipCompressionService implements CompressionService, InitializingBean {

    private static final String UNHANDLED_EXCEPTION = "Unhandled exception: ";

    private static final int MEGA = 1024;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private File tempDir;

    public void afterPropertiesSet() {
        if (tempDir == null) {
            throw new RuntimeException("Null tempDir");
        }
        if (!tempDir.exists()) {
            throw new RuntimeException("Dir does not exist");
        }
        if (!tempDir.isDirectory()) {
            throw new RuntimeException("Dir path not dir");
        }
        if (!tempDir.canWrite()) {
            throw new RuntimeException("Dir not writable");
        }
    }

    public byte[] zip(byte[] content, String fileName) {
        if (content == null) {
            throw new RuntimeException("contentO Not Set");
        }
        try {
            File tempFile = new File(FilenameUtils.concat(tempDir.getAbsolutePath(), fileName));
            logger.debug("tempFile: " + tempFile);
            FileUtils.writeByteArrayToFile(tempFile, content);
            if (!tempFile.exists()) {
                throw new RuntimeException("No errors and no file after FileUtils.writeByteArrayToFile");
            }
            String compressedFilePath = zip(tempFile.getAbsolutePath());
            logger.debug("compressedFilePath: " + compressedFilePath);
            return FileUtils.readFileToByteArray(new File(compressedFilePath));
        } catch (IOException ioe) {
            logger.error(UNHANDLED_EXCEPTION + ioe.getMessage());
            ioe.printStackTrace();
            throw new RuntimeException("Error while zipping file", ioe);
        }
    }

    public void unzip(byte[] content, String targetDirPath) {
        String sourcePath = FilenameUtils.concat(tempDir.getAbsolutePath(), IOUtil.cleanForPath(UUIDGenerator.makeId()));
        File sourceDir = new File(sourcePath);
        try {
            FileUtils.writeByteArrayToFile(sourceDir, content);
            unzip(sourceDir.getAbsolutePath(), targetDirPath);
            FileUtils.forceDeleteOnExit(sourceDir);
        } catch (Exception ex) {
            logger.error(UNHANDLED_EXCEPTION + ex.getMessage(), ex);
            throw new RuntimeException("Error while unzipping file: " + targetDirPath + " Message: " + ex.getMessage(), ex);
        }
    }

    public void unzip(String sourceFilePath, String targetDirPath) {
        Enumeration<? extends ZipEntry> entries = null;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(getExistentFile(sourceFilePath));
            entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String entryName = entry.getName();
                if (entry.isDirectory()) {
                    logger.debug("Extracting directory: " + entryName);
                    FileUtils.forceMkdir(new File(entryName));
                    continue;
                } else if (entryName.contains("/")) {
                    String zipPath = FilenameUtils.getPath(entryName);
                    String baseDirName = FilenameUtils.concat(targetDirPath, zipPath);
                    File basedir = new File(baseDirName);
                    if (!basedir.exists()) {
                        FileUtils.forceMkdir(basedir);
                    }
                }
                String targetPath = FilenameUtils.concat(targetDirPath, IOUtil.cleanForPath(entryName));
                logger.debug("Extracting file: " + entryName + " to: " + targetPath);
                copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(targetPath)));
            }
            zipFile.close();
        } catch (IOException ioe) {
            logger.error(UNHANDLED_EXCEPTION);
            logger.error(ioe.getMessage(), ioe);
            throw new RuntimeException("Error while unzipping file: " + sourceFilePath, ioe);
        }
    }

    public void zip(String targetFilePath, String sourceDirPath) {
        byte[] buf = new byte[MEGA];
        try {
            File sourceDir = IOUtil.getExistentFile(sourceDirPath);
            if (!sourceDir.exists() || !sourceDir.isDirectory()) {
                throw new RemoteException("Invalid sourceDirPath: " + sourceDirPath);
            }
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetFilePath));
            File[] files = sourceDir.listFiles();
            logger.debug("Found: " + files.length + " files");
            for (int i = 0; i < files.length; i++) {
                FileInputStream in = new FileInputStream(files[i]);
                out.putNextEntry(new ZipEntry(files[i].getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
        } catch (IOException ioe) {
            logger.error(UNHANDLED_EXCEPTION + ioe.getMessage());
            ioe.printStackTrace();
            throw new RuntimeException("Error while zipping files from: " + sourceDirPath, ioe);
        }
    }

    public File zip(File sourceFile) {
        if (sourceFile == null || !sourceFile.exists()) {
            throw new IllegalArgumentException("File does not exist: " + sourceFile);
        }
        if (FilenameUtils.getExtension(sourceFile.getAbsolutePath()).equalsIgnoreCase("zip")) {
            return sourceFile;
        }
        byte[] buf = new byte[MEGA];
        try {
            String resultPath = sourceFile.getAbsolutePath() + ".zip";
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(resultPath));
            FileInputStream in = new FileInputStream(sourceFile);
            out.putNextEntry(new ZipEntry(sourceFile.getName()));
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.closeEntry();
            in.close();
            out.close();
            return new File(resultPath);
        } catch (Exception ioe) {
            logger.error("Error while zipping file: " + ioe.getMessage());
            ioe.printStackTrace();
            throw new RuntimeException("Error while zipping file: " + sourceFile, ioe);
        }
    }

    public String zip(String sourceFilePath) {
        byte[] buf = new byte[MEGA];
        try {
            File sourceFile = IOUtil.getExistentFile(sourceFilePath);
            String resultPath = sourceFilePath + ".zip";
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(resultPath));
            FileInputStream in = new FileInputStream(sourceFile);
            out.putNextEntry(new ZipEntry(sourceFile.getName()));
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.closeEntry();
            in.close();
            out.close();
            return resultPath;
        } catch (IOException ioe) {
            logger.error(UNHANDLED_EXCEPTION + ioe.getMessage());
            ioe.printStackTrace();
            throw new RuntimeException("Error while zipping file: " + sourceFilePath, ioe);
        }
    }

    /**
     * getExistentFile
     * 
     * @param path
     * @return
     * @throws RuntimeException
     */
    private File getExistentFile(String path) throws RuntimeException {
        File testFile = new File(path);
        if (!testFile.exists()) {
            throw new RuntimeException("Unable to find document: " + testFile.getAbsolutePath());
        }
        return testFile;
    }

    /**
     * copyInputStream
     * 
     * @param in
     * @param out
     * @throws IOException
     */
    private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[MEGA];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        in.close();
        out.close();
    }

    public void setTempDir(File tempDir) {
        this.tempDir = tempDir;
    }

    public File getTempDir() {
        return tempDir;
    }
}

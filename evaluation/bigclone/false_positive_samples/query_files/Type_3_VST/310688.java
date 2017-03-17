package org.pointrel.pointrel20090201;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class ResourceFileSupport {

    public static final String ResourceFilePrefix = "PRF_";

    public static final String TransactionFileExtension = ".pointrel";

    public static String wildcardMatchStringForAnyResourceFileWithSuffix(String suffix) {
        return ResourceFilePrefix + "*" + suffix;
    }

    public static String wildcardMatchStringForTransactionFile() {
        return ResourceFilePrefix + "*" + TransactionFileExtension;
    }

    public static String wildcardMatchStringForAnyResourceFile() {
        return ResourceFilePrefix + "*";
    }

    public static boolean isValidTransactionFileName(String name) {
        return isValidResourceFileName(name) && name.endsWith(TransactionFileExtension);
    }

    public static FilenameFilter getTransactionFileNameFilter() {
        return new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return isValidTransactionFileName(name);
            }
        };
    }

    public static File[] getTransactionResourceFiles(String directoryPath) {
        File folder = new File(directoryPath);
        File[] listOfFiles = folder.listFiles(getTransactionFileNameFilter());
        return listOfFiles;
    }

    public static boolean isValidResourceFileName(String name) {
        if (!name.startsWith(ResourceFilePrefix)) return false;
        int lengthOfHexEncodedSHA256Hash = 64;
        int positionOfSizeUnderscore = ResourceFilePrefix.length() + lengthOfHexEncodedSHA256Hash;
        if (name.length() <= positionOfSizeUnderscore + 1) return false;
        String hexEncodedHash = name.substring(ResourceFilePrefix.length(), positionOfSizeUnderscore);
        if (!hexEncodedHash.matches("[0123456789abcdef]{" + lengthOfHexEncodedSHA256Hash + "}")) return false;
        if (name.charAt(positionOfSizeUnderscore) != '_') return false;
        int firstDotPosition = name.indexOf(".");
        String sizeField = null;
        if (firstDotPosition == -1) {
            sizeField = name.substring(positionOfSizeUnderscore + 1);
        } else {
            sizeField = name.substring(positionOfSizeUnderscore + 1, firstDotPosition);
        }
        int lengthOfSizeField = sizeField.length();
        if (!sizeField.matches("[0123456789]{" + lengthOfSizeField + "}")) return false;
        return true;
    }

    public static FilenameFilter getResourceFileNameFilter() {
        return new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return isValidResourceFileName(name);
            }
        };
    }

    public static File[] getAllResourceFiles(String directoryPath) {
        File folder = new File(directoryPath);
        File[] listOfFiles = folder.listFiles(getResourceFileNameFilter());
        return listOfFiles;
    }

    public static boolean isValidResourceFileNameWithSuffix(String name, String suffix) {
        return isValidResourceFileName(name) && name.endsWith(suffix);
    }

    public static FilenameFilter getResourceFileNameFilterWithSuffix(final String suffix) {
        return new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return isValidResourceFileNameWithSuffix(name, suffix);
            }
        };
    }

    public static File[] getAllResourceFilesWithSuffix(String directoryPath, String suffix) {
        File folder = new File(directoryPath);
        File[] listOfFiles = folder.listFiles(getResourceFileNameFilterWithSuffix(suffix));
        return listOfFiles;
    }

    public static void copyInputStreamToOutputStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }

    public static void copyFromFileToFile(File inputFile, File outputFile) throws IOException {
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        try {
            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            inputStream.close();
            outputStream.close();
        }
    }

    public static void copyFromFileToFileUsingNIO(File inputFile, File outputFile) throws FileNotFoundException, IOException {
        FileChannel inputChannel = new FileInputStream(inputFile).getChannel();
        FileChannel outputChannel = new FileOutputStream(outputFile).getChannel();
        try {
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
        } catch (IOException e) {
            throw e;
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }
    }

    public static String getExtensionWithDotOrEmptyString(String filename, boolean allExtensions) {
        String extension = "";
        if (filename.contains(".")) {
            if (allExtensions) {
                extension = filename.substring(filename.indexOf("."));
            } else {
                extension = filename.substring(filename.lastIndexOf("."));
            }
        }
        return extension;
    }

    public static boolean isValidHashFromFileAndName(File file) {
        String resourceFileReference = Standards.getResourceFileReferenceWithSHA256HashAsHexEncodedString(file);
        boolean matches = resourceFileReference.equals(file.getName());
        return matches;
    }

    public static String extractTimestampFromResourceFileReferenceIfPossible(String resourceFileReference, String partialSuffix) {
        int startOfFullSuffix = resourceFileReference.indexOf(".");
        if (startOfFullSuffix == -1) return null;
        String fullSuffix = resourceFileReference.substring(startOfFullSuffix + 1);
        int startOfPartialSuffix = fullSuffix.indexOf(".");
        if (startOfPartialSuffix == -1) return null;
        String partialSuffixToCheck = fullSuffix.substring(startOfPartialSuffix);
        if (!partialSuffixToCheck.equals(partialSuffix)) return null;
        String timestamp = fullSuffix.substring(0, startOfPartialSuffix);
        if (timestamp.equals("")) return null;
        if (!timestamp.endsWith("Z")) return null;
        if (!timestamp.contains("_")) return null;
        return timestamp;
    }

    public static String wrapRepositoryPath(String path) {
        if (new File(path).exists()) return path;
        String alternativePath = "../" + path;
        if (new File(alternativePath).exists()) return alternativePath;
        alternativePath = "Pointrel20090201/" + path;
        if (new File(alternativePath).exists()) return alternativePath;
        alternativePath = "trunk/" + alternativePath;
        if (new File(alternativePath).exists()) return alternativePath;
        return path;
    }

    public static boolean isValidResourceFileExtension(String extension) {
        if (extension == null) return true;
        if (extension.equals("")) return true;
        if (extension.contains("/")) return false;
        if (extension.contains("\\")) return false;
        if (extension.contains(":")) return false;
        if (extension.contains("\n")) return false;
        if (extension.contains("\r")) return false;
        if (extension.contains("..")) return false;
        if (extension.charAt(0) != '.') return false;
        return true;
    }
}

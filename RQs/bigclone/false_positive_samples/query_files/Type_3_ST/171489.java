package is.iclt.icenlp.core.utils;

import java.io.*;
import java.util.Scanner;
import java.util.zip.*;

/**
 * A class for zipping and unzipping
 */
public class ZipGzipper {

    public static final int BUF_SIZE = 8192;

    public static final int STATUS_OK = 0;

    public static final int STATUS_OUT_FAIL = 1;

    public static final int STATUS_ZIP_FAIL = 2;

    public static final int STATUS_GZIP_FAIL = 3;

    public static final int STATUS_IN_FAIL = 4;

    public static final int STATUS_UNZIP_FAIL = 5;

    public static final int STATUS_GUNZIP_FAIL = 6;

    private static String fMessages[] = { "Operation succeeded", "Failed to create output stream", "Failed to create zipped file", "Failed to create gzipped file", "Failed to open input stream", "Failed to decompress zip file", "Failed to decompress gzip file" };

    /** Return a brief message for each status number. **/
    public static String getStatusMessage(int msg_number) {
        return fMessages[msg_number];
    }

    /**
    * Zip the input file and send the zip archive to the output directory.
    * This method only packs one file into the archive.
   **/
    public static int zipFile(File file_input, File dir_output) {
        File zip_output = new File(dir_output, file_input.getName() + ".zip");
        ZipOutputStream zip_out_stream;
        try {
            FileOutputStream out = new FileOutputStream(zip_output);
            zip_out_stream = new ZipOutputStream(new BufferedOutputStream(out));
        } catch (IOException e) {
            return STATUS_OUT_FAIL;
        }
        byte[] input_buffer = new byte[BUF_SIZE];
        int len = 0;
        try {
            ZipEntry zip_entry = new ZipEntry(file_input.getName());
            zip_out_stream.putNextEntry(zip_entry);
            FileInputStream in = new FileInputStream(file_input);
            BufferedInputStream source = new BufferedInputStream(in, BUF_SIZE);
            while ((len = source.read(input_buffer, 0, BUF_SIZE)) != -1) zip_out_stream.write(input_buffer, 0, len);
            in.close();
        } catch (IOException e) {
            return STATUS_ZIP_FAIL;
        }
        try {
            zip_out_stream.close();
        } catch (IOException e) {
        }
        return STATUS_OK;
    }

    /**
    *  Gzip the input file to an archive with the same name except for
    *  ".gz" appended to it. The archive will be in the chosen output 
    *  directory.
   **/
    public static int gzipFile(File file_input, String file_output) {
        File gzip_output = new File(file_output);
        GZIPOutputStream gzip_out_stream;
        try {
            FileOutputStream out = new FileOutputStream(gzip_output);
            gzip_out_stream = new GZIPOutputStream(new BufferedOutputStream(out));
        } catch (IOException e) {
            return STATUS_OUT_FAIL;
        }
        byte[] input_buffer = new byte[BUF_SIZE];
        int len = 0;
        try {
            FileInputStream in = new FileInputStream(file_input);
            BufferedInputStream source = new BufferedInputStream(in, BUF_SIZE);
            while ((len = source.read(input_buffer, 0, BUF_SIZE)) != -1) gzip_out_stream.write(input_buffer, 0, len);
            in.close();
        } catch (IOException e) {
            return STATUS_GZIP_FAIL;
        }
        try {
            gzip_out_stream.close();
        } catch (IOException e) {
        }
        return STATUS_OK;
    }

    /**
    *  Unzip the files from a zip archive into the given output directory.
    *  It is assumed the archive file ends in ".zip".
   **/
    public static int unzipFile(File file_input, File dir_output) {
        ZipInputStream zip_in_stream;
        try {
            FileInputStream in = new FileInputStream(file_input);
            BufferedInputStream source = new BufferedInputStream(in);
            zip_in_stream = new ZipInputStream(source);
        } catch (IOException e) {
            return STATUS_IN_FAIL;
        }
        byte[] input_buffer = new byte[BUF_SIZE];
        int len = 0;
        do {
            try {
                ZipEntry zip_entry = zip_in_stream.getNextEntry();
                if (zip_entry == null) break;
                File output_file = new File(dir_output, zip_entry.getName());
                FileOutputStream out = new FileOutputStream(output_file);
                BufferedOutputStream destination = new BufferedOutputStream(out, BUF_SIZE);
                while ((len = zip_in_stream.read(input_buffer, 0, BUF_SIZE)) != -1) destination.write(input_buffer, 0, len);
                destination.flush();
                out.close();
            } catch (IOException e) {
                return STATUS_GUNZIP_FAIL;
            }
        } while (true);
        try {
            zip_in_stream.close();
        } catch (IOException e) {
        }
        return STATUS_OK;
    }

    /**
    * Gunzip the input archive. Send the output to the directory specified 
    * by dir_output. Assumes that the input file name ends with ".gz"
   **/
    public static int gunzipFile(File file_input, File dir_output) {
        GZIPInputStream gzip_in_stream;
        try {
            FileInputStream in = new FileInputStream(file_input);
            BufferedInputStream source = new BufferedInputStream(in);
            gzip_in_stream = new GZIPInputStream(source);
        } catch (IOException e) {
            return STATUS_IN_FAIL;
        }
        String file_input_name = file_input.getName();
        String file_output_name = file_input_name.substring(0, file_input_name.length() - 3);
        File output_file = new File(dir_output, file_output_name);
        byte[] input_buffer = new byte[BUF_SIZE];
        int len = 0;
        try {
            FileOutputStream out = new FileOutputStream(output_file);
            BufferedOutputStream destination = new BufferedOutputStream(out, BUF_SIZE);
            while ((len = gzip_in_stream.read(input_buffer, 0, BUF_SIZE)) != -1) destination.write(input_buffer, 0, len);
            destination.flush();
            out.close();
        } catch (IOException e) {
            return STATUS_GUNZIP_FAIL;
        }
        try {
            gzip_in_stream.close();
        } catch (IOException e) {
        }
        return STATUS_OK;
    }

    public static String gz2String(InputStream fis) {
        StringBuffer output = new StringBuffer();
        GZIPInputStream gzip_in_stream;
        try {
            BufferedInputStream source = new BufferedInputStream(fis);
            gzip_in_stream = new GZIPInputStream(source);
            BufferedReader reader = FileEncoding.getReader(gzip_in_stream);
            String gutti;
            while ((gutti = reader.readLine()) != null) {
                output.append(gutti + System.getProperty("line.separator"));
            }
        } catch (IOException ex) {
            System.out.println("IO Exception while unzipping data!");
            ex.printStackTrace();
        }
        return output.toString();
    }

    public static String gz2String(String filePath) {
        StringBuffer output = new StringBuffer();
        try {
            GZIPInputStream stream = new GZIPInputStream(new BufferedInputStream(new FileInputStream(filePath)));
            Scanner scanner = new Scanner(stream);
            while (scanner.hasNextLine()) {
                output.append(scanner.nextLine() + "\n");
            }
        } catch (Exception e) {
            System.out.println("Exception in gz2String:");
            e.printStackTrace();
        }
        return output.toString();
    }
}

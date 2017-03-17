import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ZipFiles {
	public static void ZipFiles(File zipfile, File[] files) throws IOException {
		byte[] buffer = new byte[1024];
		FileOutputStream fos = new FileOutputStream(zipfile);
		ZipOutputStream zos = new ZipOutputStream(fos);
		
		// For Each File
		for(int i = 0; i < files.length; i++) {
			// Open File
			File src = files[i];
			FileInputStream fis = new FileInputStream(src);
			
			//Create new zip entry
			ZipEntry entry = new ZipEntry(src.getName());
			zos.putNextEntry(entry);
			
			//Write the file to the entry in the zip file (compressed)
			int length;
			while((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
			zos.closeEntry();
			
			//Close Original File
			fis.close();
		}
		
		// Close Zip File
		zos.close();
	}
	
//	public static void main(String args[]) throws IOException {
//		File[] files = {new File("/media/jeff/ssd/test1"), new File("/media/jeff/ssd/test2")};
//		ZipFiles.ZipFiles(new File("/media/jeff/ssd/zip.zip"), files);
//	}
}

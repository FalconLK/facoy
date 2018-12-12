import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

public class CRC32FileChecksum {
	public static long checksum1(File file) throws IOException {
		CRC32 crc = new CRC32();
		FileReader fr = new FileReader(file);
		int data;
		while((data = fr.read()) != -1) {
			crc.update(data);
		}
		fr.close();
		return crc.getValue();
	}
}

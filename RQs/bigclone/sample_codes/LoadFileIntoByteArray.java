import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;


public class LoadFileIntoByteArray {
	public static byte[] loadFile(File file) throws Exception {
        if (!file.exists() || !file.canRead()) {
            String message = "Cannot read file: " + file.getCanonicalPath();
            throw new Exception(message);
        }
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        int len = 0;
        byte[] buf = new byte[1024];
        while ((len = fis.read(buf)) >= 0) {
            data.write(buf, 0, len);
        }
        fis.close();
        byte[] retval = data.toByteArray();
        data.close();
        return retval;
    }
}

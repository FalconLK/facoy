import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class EncryptFile {
	public static void encryptFile(File in, File out, SecretKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
		// Create Cipher for Algorithm using Encryption Key
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.ENCRYPT_MODE, key);
		
		// Create File Read/Writers
		FileInputStream fin = new FileInputStream(in);
		FileOutputStream fout = new FileOutputStream(out);
		
		// Create Cipher Output Stream
		CipherOutputStream cout = new CipherOutputStream(fout, c);
		
		// Read input writer and write encrypted version using cipher stream
		int i;
		byte[] data = new byte[1024];
		while((i = fin.read(data)) != -1)
			cout.write(data, 0, i);
		
		// Clean-up
		fin.close();
		cout.close();
	}
	
}

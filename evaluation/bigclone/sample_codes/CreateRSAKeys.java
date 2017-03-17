import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class CreateRSAKeys {
	
	public static void main(String args[]) throws NoSuchAlgorithmException, IOException {
		System.out.println(generateKeys1());
	}
	
	public static KeyPair generateKeys1() throws NoSuchAlgorithmException, IOException {
		//Minimum Example
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		KeyPair keyPair = keyGen.generateKeyPair();
		return keyPair;
	}
	
	public static void generateKeys2(int keySize, Path publicKey, Path privateKey) throws NoSuchAlgorithmException, IOException {
		//Fuller Example
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(keySize);
		KeyPair keyPair = keyGen.generateKeyPair();
		PublicKey pubkey = keyPair.getPublic();
		PrivateKey privkey = keyPair.getPrivate();
		
		Files.createDirectories(publicKey.getParent());
		Files.createFile(publicKey);
		Files.createDirectories(privateKey.getParent());
		Files.createFile(privateKey);
		
		ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(publicKey.toFile())));
		oout.writeObject(pubkey);
		oout.close();
		
		oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(privateKey.toFile())));
		oout.writeObject(privkey);
		oout.close();
	}
	
}

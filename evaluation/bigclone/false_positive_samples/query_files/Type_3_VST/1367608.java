package it.ge.condam.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.naming.ServiceUnavailableException;
import sun.misc.BASE64Encoder;

/**
 * 
 * @author Leone
 *
 */
public class PasswordService {

    private static PasswordService instance = null;

    public synchronized String encrypt(String plaintext) throws ServiceUnavailableException {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceUnavailableException(e.getMessage());
        }
        try {
            md.reset();
            md.update(plaintext.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ServiceUnavailableException(e.getMessage());
        }
        byte raw[] = md.digest();
        String hash = (new BASE64Encoder()).encode(raw);
        return hash;
    }

    public static synchronized PasswordService getInstance() {
        if (instance == null) {
            return new PasswordService();
        } else {
            return instance;
        }
    }
}

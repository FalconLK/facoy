package model;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Classe che consente di crittografare una stringa mediante l'utilizzo
 * dell'algoritmo MD5. Tale algoritmo opera su stringhe di lunghezza 
 * arbitraria e fornisce come output una stringa che sarà con alta 
 * probabilità univoca. Dall'output è molto complicato risalire alla 
 * stringa di partenza. 
 * 
 * @author Gruppo Capo, De Notaris, Pastore e Vento
 * @since Luglio 2011
 *
 */
public class CryptMD5 {

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) buf.append((char) ('0' + halfbyte)); else buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * Metodo che consente di crittografare secondo l'algoritmo di hashing
     * MD5 una stringa di lunghezza arbitraria.
     * @param text stringa da crittografare
     * @return String la stringa crittografata
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String MD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        byte[] md5hash = new byte[32];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    }
}

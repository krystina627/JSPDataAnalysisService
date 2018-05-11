/**
 *
 * @author Jeff
 */

package login;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.*;
import org.apache.commons.codec.binary.Base64;

public class Encryption {
    
    static Cipher cipher;

    public static void main(String[] args) throws Exception {
 	   String password = "tickOK";
 	   String encryptedPassword = encrypt(password);
    } 

	public static String encrypt(String textIn)
		throws Exception {
            
        //Our encryption key; for our purposes hard-coded here
        String keyString = "MIICXQIBAAKBgQCjdlhdA4";

        byte[] decodedKey = Base64.decodeBase64(keyString);
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        cipher = Cipher.getInstance("AES");    
            
        byte[] plainTextBytes = textIn.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainTextBytes);
        String textOut = Base64.encodeBase64String(encryptedBytes);
		//String textOut = textIn + "OK";
        return textOut;
                
	}

}
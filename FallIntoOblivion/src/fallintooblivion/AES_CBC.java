package fallintooblivion;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES_CBC {
    
    public static byte[] encrypt(String key, String IV, byte[] file) {
        
        try {
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(file);

            return encrypted;
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    
    public static byte[] decrypt(String key, String IV, byte[] file) {
        try {
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = file;

            return original;
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    
}
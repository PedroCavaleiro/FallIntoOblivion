package fallintooblivion;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

public class AES_CBC {

    /**
     * Encripta um ficheiro por blocos de 1024 bytes
     * @param in         stream de entrada
     * @param out        stream de saida
     * @param password   password/chave
     * @param iv         iv
     * @throws Exception Excepção generica
     */
    public static void encrypt(InputStream in, OutputStream out, String password, String iv) throws Exception{

        Cipher cipher = Cipher.getInstance("AES/CFB8/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        out = new CipherOutputStream(out, cipher);
        byte[] buf = new byte[1024];
        int numRead = 0;
        while ((numRead = in.read(buf)) >= 0) {
            out.write(buf, 0, numRead);
        }
        out.close();
    }


    /**
     * Desencripta um ficheiro por blocos de 1024 bytes
     * @param in         stream de entrada
     * @param out        stream de saida
     * @param password   password/chave
     * @param iv         iv
     * @throws Exception Excepção genérica
     */
    public static void decrypt(InputStream in, OutputStream out, String password, String iv) throws Exception{

        Cipher cipher = Cipher.getInstance("AES/CFB8/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        in = new CipherInputStream(in, cipher);
        byte[] buf = new byte[1024];
        int numRead = 0;
        while ((numRead = in.read(buf)) >= 0) {
            out.write(buf, 0, numRead);
        }
        out.close();
    }

    /**
     * Define se vai efetuar encriptação ou desencriptação copiando o ficheiro
     * @param mode       modo da cifra (ENCRYPT_MODE / DECRYPT_MODE)
     * @param inputFile  Ficheiro de entrada
     * @param outputFile Ficheiro de saida
     * @param password   password/chave
     * @param iv         iv
     * @throws Exception Excepção genérica
     */
    public static void copy(int mode, String inputFile, String outputFile, String password, String iv) throws Exception {

        BufferedInputStream is = new BufferedInputStream(new FileInputStream(inputFile));
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile));
        if(mode==Cipher.ENCRYPT_MODE){
            encrypt(is, os, password, iv);
        }
        else if(mode==Cipher.DECRYPT_MODE){
            decrypt(is, os, password, iv);
        }
        else throw new Exception("unknown mode");
        is.close();
        os.close();
    }
    
}
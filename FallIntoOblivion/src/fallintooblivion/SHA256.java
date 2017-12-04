/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 *
 * Calcula o HASH de um ficheiro usando o algoritmo SHA256
 * Apenas é necessário chamar a função calculateMAC para calcular o mac de um
 * ficheiro eg.
 * String mac = SHA256.calculateMAC("C:\somefile.txt");
 * 
 */
public class SHA256 {
    
    /**
    * Calcular o MAC de um ficheiro usando o algoritmo SHA256
    * O ficheiro é lido dentro desta função.
    * Se ocorrer um erro é atirada uma excepção
     *
    * @param  filename  a localização do ficheiro
    * @return           checksum em bytes
    */
    public static byte[] calculateMACBytes(String filename) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        FileInputStream fis = new FileInputStream(filename);

        byte[] dataBytes = new byte[1024];

        int nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        };
        byte[] mdbytes = md.digest();

        return mdbytes;
    }
    
    public static String calculateStringMAC(String text) throws Exception {
        MessageDigest hash = MessageDigest.getInstance("SHA-256");
        byte[] textBytes = text.getBytes();
        hash.update(textBytes);
        return getSHA256Checksum(hash.digest());
    }
    
    /**
    * Converte o checksum do ficheiro de um array de bytes para uma string
    *
    * @param  digest  byte array com o checksup
    * @return         checksum em string
    */
    private static String getSHA256Checksum(byte[] digest) {
        String result = "";
        for (int i=0; i < digest.length; i++) {
            result += Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        //System.out.println(result.toString());
        return result;
    }
    
}

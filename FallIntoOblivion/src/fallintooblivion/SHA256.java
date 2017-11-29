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
    * Antes da função terminar é chamada a função getSHA256Checksum() para
    * converter o checksum the byte[] para String
    *
    * @param  filename  a localização do ficheiro
    * @return           checksum em string
    */
    public static String calculateMAC(String filename) throws Exception {
        InputStream fileIS =  new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("SHA-256");
        
        int numRead;
        
        do {
         numRead = fileIS.read(buffer);
         if (numRead > 0) {
           complete.update(buffer, 0, numRead);
           }
         } while (numRead != -1);
        
        fileIS.close();
        
        return getSHA256Checksum(complete.digest());
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.System.in;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;


public class functionsDebug {
    public static void main(String[] args) {
        String inFile = "Fall_Into_Oblivion/Trashed/Sem nome.rtf/Sem nome.rtf.aescbc";
        String hashFile = "Fall_Into_Oblivion/Trashed/Sem nome.rtf/Sem nome.rtf.aescbc.hash";
        File iFile = new File(inFile);
        System.out.println("IN FILE: " + iFile.exists());
        File hFile = new File(hashFile);
        System.out.println("HASH FILE: " + hFile.exists());
        try {
            byte[] checker = SHA256.calculateMACBytes(inFile);
            byte[] hashBytes = Helpers.FileHelpers.readFile(hashFile);
            //Helpers.FileHelpers.writeFile(hashFile + "2","", checker);
            if (Arrays.equals(checker, hashBytes))
                System.out.println("The file is valid");
            else
                System.out.println("The file is not valid");

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
    
}

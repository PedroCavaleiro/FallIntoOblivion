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

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;


public class functionsDebug {
    public static void main(String[] args) {
        String inFile = "Fall_Into_Oblivion/Trashed/SemNome.rtf.aescbc.lock";
        File iFile = new File(inFile);
        try {
            FileChannel channel = new RandomAccessFile(inFile, "rw").getChannel();
            channel.lock();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
    
}

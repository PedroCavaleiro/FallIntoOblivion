/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author pedrocavaleiro
 */
public class FallIntoOblivion {

    static Configs conf = new Configs();
    
    public static void main(String[] args) throws IOException {
        List<String> lines = Arrays.asList("The first line", "The second line");
        Path file = Paths.get("config.txt");
        Files.write(file, lines, Charset.forName("UTF-8"));
        List<String> configs = Arrays.asList();
        String choice = new String();
        while(true){
            System.out.print("FallIntoOblivion> ");
            choice=Ler.umaString();
            String[] words = choice.split("\\s+");
            switch (words[0]){
                case "setcypher":
                    if(words.length == 3)
                        setCyper(words);
                    else
                        System.out.println("setcypher [cyphertype] [keysize]");
                    break;
                case "sethash":
                    if(words.length == 2)
                        setHash(words);
                    else
                        System.out.println("sethash [hashtype]");
                    break;
                case "setenabled":
                    if(words.length == 2)
                        setEnabled(words);
                    else
                        System.out.println("setenabled [boolean]");
                    break;
                default:
                    System.out.println("Commands include: setcypher sethash setenabled"); //add new commands here too
                    break;
            }
        }
    }

    private static void setCyper(String[] words) {
        switch (words[1]){
                case "sha256": 
                    try{
                        Integer.parseInt(words[2]);
                    } catch(NumberFormatException e) {
                        System.out.println("setcypher sha256 [keysize] \nInsert a valid number for parameter [keysize]");
                    }
                    if (Integer.parseInt(words[2])<1)
                        System.out.println("setcypher sha256 [keysize] \nInsert a valid positive number for parameter [keysize]");
                    else {
                        conf.saveProp("cyphertype", words[1]);
                        conf.saveProp("keysize", words[2]);
                    }
                    break;
                case "aes_cbc": 
                    try{
                        Integer.parseInt(words[2]);
                    } catch(NumberFormatException e) {
                        System.out.println("setcypher aes_cbc [keysize] \nInsert a valid number for parameter [keysize]");
                    }
                    if (Integer.parseInt(words[2])<1)
                        System.out.println("setcypher aes_cbc [keysize] \nInsert a valid positive number for parameter [keysize]");
                    else {
                        conf.saveProp("cyphertype", words[1]);
                        conf.saveProp("keysize", words[2]);
                    }
                    break;
                default:
                    System.out.println("setcypher [keytype] [keysize]\nKeyTypes include: aes_cbc sha256"); //add new types here too
                    break;
        }
    }

    private static void setHash(String[] words) {
        switch (words[1]){
                case "sha1":
                    conf.saveProp("hashtype", words[1]);
                    break;
                default:
                    System.out.println("sethash [hashtype] \nHashTypes include: sha1"); //add new types here too
        }
    }

    private static void setEnabled(String[] words) {
        switch (words[1]){
                case "true":
                    conf.saveProp("setenabled", words[1]);
                    break;
                case "false":
                    conf.saveProp("setenabled", words[1]);
                    break;
                default:
                    System.out.println("setEnabled [boolean]"); //add new types here too
        }
    }
}

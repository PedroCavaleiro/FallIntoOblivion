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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author pedrocavaleiro
 */
public class FallIntoOblivion {

    private static Configs conf = new Configs();
    private static boolean propertiesSemaphore;
    
    public static void main(String[] args) throws IOException {
        //test if conf file exists if not create a default
        if(conf.getProp("cfgexists").isEmpty()){
            System.out.println("cfg created");
            conf.saveProp("cfgexists", "1");
            conf.saveProp("setenabled", "false");
            conf.saveProp("hashtype", "sha1");
            conf.saveProp("keysize", "16");
            conf.saveProp("cyphertype", "aes_cbc");
        }
            
        String choice = new String();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable periodicTask = new Runnable() {
            public void run() {
                if(!propertiesSemaphore){
                    
                    // Clears the text on the line where the cursor is
                    // deleting the "FallIntoOblivion> " text
                    System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
                    // This seems to be for debug purposes only
                    System.out.println("LOG DEBUG: thread work");
                    // the input area desapeared so you make a new one
                    System.out.print("FallIntoOblivion> ");
                    
                    propertiesSemaphore = true;
                    String setenabled = conf.getProp("setenabled");
                    String hashtype = conf.getProp("hashtype");
                    String keysize = conf.getProp("keysize");
                    String cyphertype = conf.getProp("cyphertype");
                    propertiesSemaphore = false;
                    
                    if (setenabled.equals(false)) //stopping if setenabled was disabled in the configs
                        return;
                }
                else{
                    System.out.println("Thread didn't run due to properties being changed");
                }
            }
        };
        executor.scheduleAtFixedRate(periodicTask, 5, 10, TimeUnit.SECONDS);
        
        while(true){
            System.out.print("FallIntoOblivion> ");
            choice=Ler.umaString();
            String[] words = choice.split("\\s+");
            switch (words[0]){
                case "setcypher":
                    if(words.length == 3)
                        setCyper(words);
                    else if(words.length == 2)
                       System.out.println("setcypher [cyphertype] [keysize]\n cypher types inlude: aes_cbc sha256");
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
                case "exit":
                    return;
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
                        return;
                    }
                    if (Integer.parseInt(words[2])<1)
                        System.out.println("setcypher sha256 [keysize] \nInsert a valid positive number for parameter [keysize]");
                    else {
                        while(!propertiesSemaphore)
                            ;//wait for acess
                        propertiesSemaphore=true;
                        conf.saveProp("cyphertype", words[1]);
                        conf.saveProp("keysize", words[2]);
                        propertiesSemaphore=false;
                    }
                    break;
                case "aes_cbc": 
                    try{
                        Integer.parseInt(words[2]);
                    } catch(NumberFormatException e) {
                        System.out.println("setcypher aes_cbc [keysize] \nInsert a valid number for parameter [keysize]");
                        return;
                    }
                    if (Integer.parseInt(words[2])<1)
                        System.out.println("setcypher aes_cbc [keysize] \nInsert a valid positive number for parameter [keysize]");
                    else {
                        while(!propertiesSemaphore)
                            ;//wait for acess
                        propertiesSemaphore=true;
                        conf.saveProp("cyphertype", words[1]);
                        conf.saveProp("keysize", words[2]);
                        propertiesSemaphore=false;
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
                    while(!propertiesSemaphore)
                        ; //wait for acess
                    propertiesSemaphore=true;
                    conf.saveProp("hashtype", words[1]);
                    propertiesSemaphore=false;
                    break;
                default:
                    System.out.println("sethash [hashtype] \nHashTypes include: sha1"); //add new types here too
        }
    }

    private static void setEnabled(String[] words) {
        switch (words[1]){
                case "true":
                    while(!propertiesSemaphore)
                        ;//wait for acess
                    propertiesSemaphore=true;
                    conf.saveProp("setenabled", words[1]);
                    propertiesSemaphore=false;
                    break;
                case "false":
                    while(!propertiesSemaphore)
                        ;//wait for acess
                    propertiesSemaphore=true;
                    conf.saveProp("setenabled", words[1]);
                    propertiesSemaphore=false;
                    break;
                default:
                    System.out.println("setEnabled [boolean]"); //add new types here too
        }
    }
}

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
    
    private static final String availableCommands = "Available commands: setcypher sethash setenabled showconfig\n";
    private static final String setCypherCommandString = "setcypher [cyphertype] [keysize]\nCypher Types: aes_cbc\n";
    private static final String setHashCommandString = "sethash [hashtype] \nHash Algorithms: sha256\n";
    
    private static final String setCypherInvalidKeySizeErr = " [keysize] \nInsert a valid number for parameter [keysize]\n";
    private static final String setCypherIncalidKeyErr = " \nInsert a valid positive number for parameter [keysize]\n";
    
    
    public static void main(String[] args) throws IOException {
        //test if conf file exists if not create a default
        if(conf.getProp("cfgexists").isEmpty()){
            System.out.println("cfg created");
            conf.saveProp("cfgexists", "1");
            conf.saveProp("setenabled", "false");
            conf.saveProp("hashtype", "sha256");
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
                    
                    propertiesSemaphore = true;
                    String setenabled = conf.getProp("setenabled");
                    String hashtype = conf.getProp("hashtype");
                    String keysize = conf.getProp("keysize");
                    String cyphertype = conf.getProp("cyphertype");
                    propertiesSemaphore = false;
                    
                    // the input area desapeared so you make a new one
                    System.out.print("FallIntoOblivion> ");
                    
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
                       System.out.println(setCypherCommandString);
                    else
                       System.out.println(setCypherCommandString);
                    break;
                case "sethash":
                    if(words.length == 2)
                        setHash(words);
                    else
                        System.out.println(setHashCommandString);
                    break;
                case "setenabled":
                    if(words.length == 2)
                        setEnabled(words);
                    else
                        System.out.println("setenabled [boolean]\n");
                    break;
                case "showconfig":
                    System.out.println("Current Configuration");
                    System.out.println(conf.getProp("setenabled"));
                    System.out.println(conf.getProp("hashtype"));
                    System.out.println(conf.getProp("keysize"));
                    System.out.println(conf.getProp("cyphertype"));
                    System.out.println();
                    break;
                case "exit":
                    return;
                default:
                    System.out.println(availableCommands);
                    break;
            }
        }
    }

    private static void setCyper(String[] words) {
        switch (words[1]){
                case "aes_cbc": 
                    try{
                        Integer.parseInt(words[2]);
                    } catch(NumberFormatException e) {
                        System.out.println("setcypher aes_cbc" + setCypherInvalidKeySizeErr);
                        return;
                    }
                    if (Integer.parseInt(words[2])<1)
                        System.out.println("setcypher aes_cbc [keysize]" + setCypherIncalidKeyErr);
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
                    System.out.println(setCypherCommandString);
                    break;
        }
    }

    private static void setHash(String[] words) {
        switch (words[1]){
                case "sha256":
                    while(!propertiesSemaphore)
                        ; //wait for acess
                    propertiesSemaphore=true;
                    conf.saveProp("hashtype", words[1]);
                    propertiesSemaphore=false;
                    break;
                default:
                    System.out.println(setHashCommandString); //add new types here too
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
                    System.out.println("setEnabled [boolean]\n"); //add new types here too
        }
    }
}

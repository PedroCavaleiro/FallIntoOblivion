/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchService;
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
        //The user is supposed to drop his trash into FallIntoOblivion, it is then cyphered and put into the trashed folder
        //Test if FallIntoOblivion folder exists and if not create it
        File Dir = new File("Fall_Into_Oblivion");
        if (!Dir.exists()) {
            System.out.println("creating directory: " + Dir.getName());
            boolean result = false;
            try{
                Dir.mkdir();
                result = true;
            } 
            catch(SecurityException se){
                System.out.println("Something went wrong with creating your new very useless trash folder");
            }        
            if(result) {    
                System.out.println("DIR created");  
            }
        }
        
        //Test if FallIntoOblivion has a trashed folder if not create it
        File TrashDir = new File(Dir.getName() + "/Trashed");
        if (!TrashDir.exists()) {
            System.out.println("creating directory: " + TrashDir.getName());
            boolean result = false;
            try{
                TrashDir.mkdir();
                result = true;
            } 
            catch(SecurityException se){
                System.out.println("You shouln't delete your Trashed folder");
            }        
            if(result) {    
                System.out.println("Trashed DIR created");
            }
        }
                
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable periodicTask = new Runnable() {
            public void run() {
                while(propertiesSemaphore) //wait for access
                    ;
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
        };
        
        WatchDir watcher = new WatchDir(Dir.toPath()); //Instanciation of the new trash monitor
        String choice;
        executor.scheduleAtFixedRate(periodicTask, 5, 10, TimeUnit.SECONDS);
        while(true){
            watcher.processEvents(); //temporarily here
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

    @SuppressWarnings("empty-statement")
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
                        System.out.println("Uploading to configurations");
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
                    System.out.println("Uploading to configurations");
                    while(!propertiesSemaphore)
                        ;//wait for acess
                    propertiesSemaphore=true;
                    conf.saveProp("setenabled", words[1]);
                    propertiesSemaphore=false;
                    break;
                case "false":
                    System.out.println("Uploading to configurations");
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

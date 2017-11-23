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
        
        WatchDir watcher = new WatchDir(Dir.toPath());
        
        //program starts here
        String choice;
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        @SuppressWarnings("empty-statement")
        Runnable periodicTask = () -> {
            while(propertiesSemaphore) //wait for acess
                ;
            System.out.println("thread work");
            watcher.processEvents();
            propertiesSemaphore = true;
            String setenabled = conf.getProp("setenabled");
            String hashtype = conf.getProp("hashtype");
            String keysize = conf.getProp("keysize");
            String cyphertype = conf.getProp("cyphertype");
            propertiesSemaphore = false;
            if (setenabled.equals(false)) //stopping if setenabled was disabled in the configs
                return;
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

    @SuppressWarnings("empty-statement")
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
                        System.out.println("Uploading to configurations");
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
                    System.out.println("setcypher [keytype] [keysize]\nKeyTypes include: aes_cbc sha256"); //add new types here too
                    break;
        }
    }

    private static void setHash(String[] words) {
        switch (words[1]){
                case "sha1":
                    System.out.println("Uploading to configurations");
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
                    System.out.println("setEnabled [boolean]"); //add new types here too
        }
    }
}

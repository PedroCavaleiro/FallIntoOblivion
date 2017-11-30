/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


/**
 *
 * @author pedrocavaleiro
 */
public class FallIntoOblivion {

    private static Configs conf = new Configs();
    private static ReentrantLock propertiesLock = new ReentrantLock();
    
    private static final String availableCommands = "Available commands: setcypher setvalidation sethash setenabled showconfig\n";
    private static final String setCypherCommandString = "setcypher [cyphertype] [keysize]\nCypher Types:\naes_cbc\n";
    
    private static final String setFileValidationCommandString = "setvalidation [validationmethod]\nAvailable Methods:\nhash\ndigital_signature\n";
    private static final String setHashCommandString = "sethash [hashtype] \nHash Algorithms:\nsha256\n";
    
    private static final String setCypherInvalidKeySizeErr = " [keysize] \nInsert a valid number for parameter [keysize]\n";
    private static final String setCypherIncalidKeyErr = " \nInsert a valid positive number for parameter [keysize]\n";
    
    public static ArrayList encryptedFolders = new ArrayList<String>();

    public static void main(String[] args) throws IOException {
        //test if conf file exists if not create a default
        if(conf.getProp("cfgexists").isEmpty()){
            System.out.println("cfg created");
            conf.saveProp("cfgexists", "1");
            conf.saveProp("setenabled", "true");
            conf.saveProp("filevalidation", "digital_signature");
            conf.saveProp("hashtype", "sha256");
            conf.saveProp("keysize", "16");
            conf.saveProp("cyphertype", "aes_cbc");
            conf.saveProp("encrypted","");
        }
        if(!conf.getProp("encrypted").equals("")){
            String temp[] = conf.getProp("encrypted").split(",");
            for(int i = 0; i < temp.length; i++){
                encryptedFolders.add(temp[i]);
            }
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
                
        ScheduledExecutorService executorEncrypt = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService executorDetect = Executors.newSingleThreadScheduledExecutor();
        Runnable periodicTaskEncrypt = new Runnable() {
            public void run() {
                // Clears the text on the line where the cursor is
                // deleting the "FallIntoOblivion> " text
                System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
                // This seems to be for debug purposes only
                System.out.println("LOG DEBUG: Encryption work is running");
                boolean setenabled = false;
                String cyphertype;
                String keysize;
                String hashtype;
                String filevalidation;
                propertiesLock.lock();
                try{
                    setenabled = Boolean.parseBoolean(conf.getProp("setenabled"));
                    filevalidation = conf.getProp("filevalidation");
                    hashtype = conf.getProp("hashtype");
                    keysize = conf.getProp("keysize");
                    cyphertype = conf.getProp("cyphertype");
                } finally {
                    propertiesLock.unlock();
                }
                
                /*WatchDir.foldersToEncryptLock.lock();
                try{
                    System.out.println(WatchDir.foldersToEncrypt.toString());
                }   finally {
                    WatchDir.foldersToEncryptLock.unlock();
                }*/
                    
                // the input area desapeared so you make a new one
                System.out.print("FallIntoOblivion> ");
                if (!setenabled) //stopping if setenabled was disabled in the configs
                    return;                   //Everything above this is supposed to run even if it's not setEnabled
                
                //Encypting
                WatchDir.foldersToEncryptLock.lock();
                try{
                    
                    File file;
                    file = new File((String) WatchDir.foldersToEncrypt.get(WatchDir.foldersToEncrypt.size()-1));

                    try {
                        Assinatura fileSigning = new Assinatura();

                        byte[] fBytes = Ler.umFicheiro(file.getAbsolutePath());

                        File folder = new File("Fall_Into_Oblivion/Trashed/" + file.getName());
                        if (!folder.exists()) {
                            folder.mkdir();
                        }

                        fileSigning.gerarChaves(file.getAbsolutePath(), 
                                "Fall_Into_Oblivion/Trashed/" + file.getName() + "/" + file.getName() + ".sig",
                                "Fall_Into_Oblivion/Trashed/" + file.getName() + "/" + file.getName() + ".pk");

                        String zeroHASH = SHA256.calculateStringMAC("0000");
                        System.out.println(zeroHASH.subSequence(0, 16).toString());
                        byte[] encBytes = AES_CBC.encrypt(zeroHASH.subSequence(0, 16).toString(), "0000000000000000", fBytes);
                        Ler.escreverFicheiro("Fall_Into_Oblivion/Trashed/" + file.getName() + "/" + file.getName(), cyphertype.replaceAll("_",""), encBytes);
                        encryptedFolders.add(folder.toString());
                        file.delete();
                        WatchDir.foldersToEncrypt.remove(file);
                    } catch (Exception ex) {
                        WatchDir.foldersToEncrypt.remove(file);
                    }
                            
                       
                    } finally {
                    WatchDir.foldersToEncryptLock.unlock();
                    return;
                }
            }
        };
        WatchDir watcher = new WatchDir(Dir.toPath()); //Instanciation of the new trash monitor
        Runnable TaskDetect = new Runnable() {
            public void run() {
                // Clears the text on the line where the cursor is
                // deleting the "FallIntoOblivion> " text
                System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
                // This seems to be for debug purposes only
              // TIREI  System.out.println("LOG DEBUG: Detection work is now running");
                watcher.processEvents();
                System.out.println("detect Stopping");
            }
        };
        String choice;
        executorEncrypt.scheduleAtFixedRate(periodicTaskEncrypt, 0, 5 , TimeUnit.SECONDS);
        executorDetect.schedule(TaskDetect, 0, TimeUnit.SECONDS);
        while(true){
            System.out.print("FallIntoOblivion> ");
            choice=Ler.umaString();
            String[] words = choice.split("\\s+");
            if(!choice.isEmpty())
                switch (words[0]){
                    case "setcypher":
                        if(words.length == 3)
                            setCypher(words);
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
                    case "setvalidation":
                        if(words.length == 2)
                            setFileValidationMethod(words);
                        else
                            System.out.println(setFileValidationCommandString);
                        break;
                    case "setenabled":
                        if(words.length == 2)
                            setEnabled(words);
                        else
                            System.out.println("setenabled [boolean]\n");
                        break;
                    case "showconfig":
                        System.out.println("Current Configuration");
                        System.out.println("Encryptor Running: " + conf.getProp("setenabled"));
                        System.out.println("File Validation Method: " + conf.getProp("filevalidation"));
                        System.out.println("HASH Algorithm: " + conf.getProp("hashtype"));
                        System.out.println("Cypher Type: " + conf.getProp("cyphertype"));
                        System.out.println("Key Size: " + conf.getProp("keysize"));
                        System.out.println();
                        break;
                    case "exit":
                        System.out.println(listToString(encryptedFolders));
                        conf.saveProp("encrypted", listToString(encryptedFolders));
                        System.exit(0);
                    default:
                        System.out.println(availableCommands);
                        break;
            }
        }
    }

    /**
     * Definimos qual o cypher a ser usado
     * Disponiveis atualmente AES-CBC
     * @param words um array de strings que contem os comandos necessários
     */
    @SuppressWarnings("empty-statement")
    private static void setCypher(String[] words) {
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
                        propertiesLock.lock();
                        try{
                            conf.saveProp("cyphertype", words[1]);
                            conf.saveProp("keysize", words[2]);
                        }finally {
                            propertiesLock.unlock();
                            System.out.println("Uploaded");
                        }
                    }
                    break;
                default:
                    System.out.println(setCypherCommandString);
                    break;
        }
    }

    /**
     * Define o hash a ser usado
     * Dísponiveis atualmente SHA256
     * @param words um array de strings que contem os comandos necessários
     */
    private static void setHash(String[] words) {
        switch (words[1]){
                case "sha256":
                    System.out.println("Uploading to configurations");
                    propertiesLock.lock();
                    try{
                        conf.saveProp("hashtype", words[1]);
                    } finally {
                        propertiesLock.unlock();
                        System.out.println("Uploaded");
                    }
                    break;
                default:
                    System.out.println(setHashCommandString);
        }
    }
    
    /**
     * Define qual o método pelo qual vai ser verificada a integridade dos ficheiros
     * Disponiveis atualmente HASH e Assinatura Digital
     * @param words um array de strings que contem os comandos necessários
     */
    private static void setFileValidationMethod(String[] words) {
        switch (words[1]){
                case "hash":
                    System.out.println("Uploading to configurations");
                    propertiesLock.lock();
                    try{
                        conf.saveProp("filevalidation", words[1]);
                    } finally {
                        propertiesLock.unlock();
                        System.out.println("Uploaded");
                    }
                    break;
                case "digital_signature":
                    System.out.println("Uploading to configurations");
                    propertiesLock.lock();
                    try{
                        conf.saveProp("filevalidation", words[1]);
                    } finally {
                        propertiesLock.unlock();
                        System.out.println("Uploaded");
                    }
                    break;
                default:
                    System.out.println(setFileValidationCommandString);
        }
    }
    
    /**
     * Definimos se o programa está a encriptar os ficheiros
     * @param words um array de strings que contem os comandos necessários
     */
    private static void setEnabled(String[] words) {
        switch (words[1]){
                case "true":
                    System.out.println("Uploading to configurations");
                    propertiesLock.lock();
                    try{
                        conf.saveProp("setenabled", words[1]);
                    } finally {
                        propertiesLock.unlock();
                        System.out.println("Uploaded");
                    }
                    break;
                case "false":
                    System.out.println("Uploading to configurations");
                    propertiesLock.lock();
                    try{
                        conf.saveProp("setenabled", words[1]);
                    } finally {
                        propertiesLock.unlock();
                        System.out.println("Uploaded");
                    }
                    break;
                default:
                    System.out.println("setEnabled [boolean]\n"); //add new types here too
        }
    }
    
    public static String listToString(ArrayList<String> list) {
        String result = "";
        for (int i = 0; i < list.size(); i++) {
            if(i==list.size()-1)
                result += list.get(i);
            else
                result += list.get(i)+",";
        }
        return result;
    }
}

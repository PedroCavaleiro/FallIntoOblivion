/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class FallIntoOblivion {

    private static Configs conf = new Configs();
    private static ReentrantLock propertiesLock = new ReentrantLock();
    
    private static final String availableCommands = "Available commands: setcypher sethash setenabled showconfig\n";
    private static final String setCypherCommandString = "setcypher [cyphertype] [keysize]\nCypher Types:\naes_cbc\n";
    
    private static final String setFileValidationCommandString = "setvalidation [validationmethod]\nAvailable Methods:\nhash\ndigital_signature\n";
    private static final String setHashCommandString = "sethash [hashtype] \nHash Algorithms:\nsha256\n";
    
    private static final String setCypherInvalidKeySizeErr = " [keysize] \nInsert a valid number for parameter [keysize]\n";
    private static final String setCypherIncalidKeyErr = " \nInsert a valid positive number for parameter [keysize]\n";
    
    public static ArrayList encryptedFolders = new ArrayList<String>();

    public static void main(String[] args) throws IOException {

        // Test if the configuration file already exists, if not, creates the new config
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

        // Get all the encrypted files/folders
        if(!conf.getProp("encrypted").equals("")){
            String temp[] = conf.getProp("encrypted").split(",");
            for(int i = 0; i < temp.length; i++){
                encryptedFolders.add(temp[i]);
            }
        }


        //The user must drop the file into the Fall_Into_Oblivion, it's then cyphered and moved to the Trashed folder
        // Test if Fall_Into_Oblivion folder exists and if not create it
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

        //Test if Fall_Into_Oblivion has a trashed folder if not create it
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

        // The watchers that will check for new files/folders and encrypt them
        ScheduledExecutorService executorEncrypt = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService executorDetect = Executors.newSingleThreadScheduledExecutor();

        /**
         * Task that will encrypt the files
         */
        Runnable periodicTaskEncrypt = new Runnable() {
            public void run() {

                // The line below is only for debugging purposes, comment when not testing
                // System.out.println("LOG DEBUG: Encryption work is running");

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

                if (!setenabled) //stopping if setenabled was disabled in the configs
                    return;                   // Everything above this is supposed to run even if it's not setEnabled
                
                // Encrypting Process
                // 1. Lock the detection and encryption tasks running on background
                // 2. If it's a folder we zip it and delete the folder keeping the zip file
                // 3. Sign the file and save a Public Key and Signature in the same folder then the encrypted file will be
                // 4. Encrypt the file
                // 5. Move the file to the Trashed folder
                // 6. Delete the unencrypted file
                WatchDir.foldersToEncryptLock.lock();
                try {
                    File file;
                    for(Object f : WatchDir.foldersToEncrypt) {
                        file = new File((String) f);
                        
                        try {
                            Assinatura fileSigning = new Assinatura();

                            // Check if it's a folder, if so, we zip the folder
                            if (file.isDirectory()) {
                                try {
                                    FolderZiper.zipFolder(file.getAbsolutePath(), file.getAbsolutePath() + ".zip");
                                } catch (Exception ex) {
                                    System.out.println(ex);
                                }
                                Helpers.FileHelpers.deleteDirectory(file);
                                WatchDir.foldersToEncrypt.remove(file);
                                file = new File(file.getAbsolutePath() + ".zip");
                            }  
                            
                            byte[] fBytes = Helpers.FileHelpers.lerFicheiro(file.getAbsolutePath());

                            // Check if the folder that will contain the Signature, Public Key and Encrypted file already exists
                            // If not, creates one
                            File folder = new File("Fall_Into_Oblivion/Trashed/" + file.getName());
                            if (!folder.exists()) {
                                folder.mkdir();
                            }

                            // Sign the unencrypted file and save the Signature and the Public Key
                            fileSigning.assinarFicheiro(file.getAbsolutePath(),
                                 "Fall_Into_Oblivion/Trashed/" + file.getName() + "/" + file.getName() + ".sig",
                                    "Fall_Into_Oblivion/Trashed/" + file.getName() + "/" + file.getName() + ".pk");

                            // TEMPORARY debugging purposes only
                            // Create the hash of the pin 0000
                            // So far we were only able to use 16 Byte key
                            // 24 Byte or 32 Byte will say Invalid Key Size, even though it's a valid key size
                            // TODO: Randomize 3 or 4 digit pin without printing it to the user
                            String zeroHASH = SHA256.calculateStringMAC("0000");
                            zeroHASH = zeroHASH.subSequence(0, 16).toString();

                            // Encrypt the file using the defined cypher type, it defaults to AES-CBC
                            byte[] encBytes;
                            switch (cyphertype) {
                                case "aes_cbc":
                                    encBytes = AES_CBC.encrypt(zeroHASH, "0000000000000000", fBytes);
                                    break;
                                default:
                                    encBytes = AES_CBC.encrypt(zeroHASH, "0000000000000000", fBytes);
                                    break;
                            }

                            // Write the encrypted file
                            Helpers.FileHelpers.escreverFicheiro(
                                    "Fall_Into_Oblivion/Trashed/" + file.getName() + "/" + file.getName(),
                                    cyphertype.replaceAll("_",""),
                                    encBytes);

                            // The file was successfully encrypted, we add it to the encrypted list
                            encryptedFolders.add(folder.toString());

                            // Delete the unencrypted file
                            file.delete();

                            // Remove from the file list
                            WatchDir.foldersToEncrypt.remove(f);
                        } catch (Exception ex) {
                            WatchDir.foldersToEncrypt.remove(f);
                        }
                    }
                } finally {
                    // Resume the background tasks
                    WatchDir.foldersToEncryptLock.unlock();
                    return;
                }
            }
        };

        WatchDir watcher = new WatchDir(Dir.toPath()); // Instantiation of the new trash monitor

        /**
         * Task that will detect changes on the files to encrypt
         */
        Runnable TaskDetect = new Runnable() {
            public void run() {
                watcher.processEvents();
                System.out.println("detect Stopping");
            }
        };

        // Start the background tasks
        executorEncrypt.scheduleAtFixedRate(periodicTaskEncrypt, 0, 100 , TimeUnit.MILLISECONDS);
        executorDetect.schedule(TaskDetect, 0, TimeUnit.SECONDS);

        String choice;
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
                        // Even though this command can be set, it will have no effect on the software behaviour
                        // Digital Signature will always be used
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
     * Atualmente só será usada Assinatura digital
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

    /**
     * Convert an ArrayList of Strings to a String separated by commas
     * @param list ArrayList of strings
     * @return string of all the strings in the ArrayList separated by commas
     */
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

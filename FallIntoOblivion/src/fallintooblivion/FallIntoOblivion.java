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

public class FallIntoOblivion {

    private static Configs conf = new Configs();
    private static ReentrantLock propertiesLock = new ReentrantLock();
    
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

        //Test if Fall_Into_Oblivion has a restored folder if not create it
        File RestoreDir = new File(Dir.getName() + "/Restored");
        if (!RestoreDir.exists()) {
            System.out.println("creating directory: " + RestoreDir.getName());
            boolean result = false;
            try{
                RestoreDir.mkdir();
                result = true;
            }
            catch(SecurityException se){
                System.out.println("You shouln't delete your Restored folder");
            }
            if(result) {
                System.out.println("Restored DIR created");
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

                            // Encrypt the file
                            Helpers.Encryptor.Encrypt(file.getName(), file.getAbsolutePath(), cyphertype);

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


        while(true){

            System.out.print("FallIntoOblivion> ");

            String choice = "";
            Helpers.CommandsHelper.Commands command = null;

            // We try to read the string
            try {
                choice = Reader.readString();
            } catch (Reader.Exception ex) {
                System.out.println(ex.getMessage());
            }

            // Split the string by the spaces
            String[] words = choice.split("\\s+");

            // Convert the first string of the array into a command
            if (!choice.isEmpty()) {
                try {
                    command = Helpers.CommandsHelper.translateToCommand(words[0]);
                } catch (Helpers.CommandsHelper.Exception ex) {
                    System.out.println("The Command " + words[0] + " was not found!\nType help to see all available commands and options\n");
                    System.out.println(Helpers.CommandsHelper.availableCommands);
                    continue;
                }
            }


            switch (command) {

                case restorefile:
                    if (words.length > 1) {
                        try {
                            Helpers.CommandsHelper.RestoreFileOptions option =
                                    Helpers.CommandsHelper.translateToRestoreFileOption(words[1]);
                            switch (option) {
                                case listfiles:
                                    System.out.println(listToString(retreiveFileName(encryptedFolders)).replaceAll(",","\n"));
                                    System.out.println("");
                                    break;
                                case decryptfile:
                                    try {
                                        Helpers.Encryptor.Decrypt(words[2], joinFileName(words, 3));
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    break;
                            }
                        } catch (Helpers.CommandsHelper.Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    } else {
                        System.out.println(Helpers.CommandsHelper.restoreFileOptionsString);
                    }

                    break;

                case validatefile:
                    if (words.length > 1) {
                        Assinatura digitalSignature = new Assinatura();
                        String file = joinFileName(words, 1);
                        String restoredDir = "Fall_Into_Oblivion/Restored/" + file + "/";
                        File f = new File(restoredDir + file);
                        if (f.exists()) {
                            String signatureFile = restoredDir + file + ".sig";
                            String publicKeyFile = restoredDir + file + ".pk";
                            File sf = new File(signatureFile);
                            File pkf = new File(publicKeyFile);
                            if (sf.exists() && pkf.exists()) {
                                try {
                                    boolean valid = digitalSignature.verificarAssinatura(
                                            f.getAbsolutePath(),
                                            sf.getAbsolutePath(),
                                            pkf.getAbsolutePath());
                                    if (valid)
                                        System.out.println("FILE VALIDATOR: The file matches the signature");
                                    else
                                        System.out.println("FILE VALIDATOR: The file does not match the signature");
                                } catch (Exception ex) {
                                    System.out.println(ex.getMessage());
                                }
                            } else {
                                System.out.println("Signature or Public Key files missing\n");
                            }
                        } else {
                            f = new File(file);
                            if (f.exists()) {
                                // The user has a specific location for this file
                            } else {
                                System.out.println("We were unable to find that file\n");
                            }
                        }
                    } else {
                        System.out.println(Helpers.CommandsHelper.validateFileCommandString);
                    }
                    break;

                case setcypher:
                    if(words.length == 3)
                        setCypher(words);
                    else if(words.length == 2)
                       System.out.println(Helpers.CommandsHelper.setCypherCommandString);
                    else
                       System.out.println(Helpers.CommandsHelper.setCypherCommandString);
                    break;

                case sethash:
                    if(words.length == 2)
                        setHash(words);
                    else
                        System.out.println(Helpers.CommandsHelper.setHashCommandString);
                    break;

                case setvalidation:
                    // Even though this command can be set, it will have no effect on the software behaviour
                    // Digital Signature will always be used
                    if(words.length == 2)
                        setFileValidationMethod(words);
                    else
                        System.out.println(Helpers.CommandsHelper.setFileValidationCommandString);
                    break;

                case setenabled:
                    if(words.length == 2)
                        setEnabled(words);
                    else
                        System.out.println("setenabled [boolean]\n");
                    break;

                case showconfig:
                    System.out.println("Current Configuration");
                    System.out.println("Encryptor Running: " + conf.getProp("setenabled"));
                    System.out.println("File Validation Method: " + conf.getProp("filevalidation"));
                    System.out.println("HASH Algorithm: " + conf.getProp("hashtype"));
                    System.out.println("Cypher Type: " + conf.getProp("cyphertype"));
                    System.out.println("Key Size: " + conf.getProp("keysize"));
                    System.out.println();
                    break;

                case help:
                    System.out.println(Helpers.CommandsHelper.helpString);
                    break;

                case exit:
                    System.out.println(listToString(encryptedFolders));
                    conf.saveProp("encrypted", listToString(encryptedFolders));
                    System.exit(0);

                default:
                    System.out.println(Helpers.CommandsHelper.availableCommands);
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
                    System.out.println(Helpers.CommandsHelper.setCypherCommandString);
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
                    System.out.println(Helpers.CommandsHelper.setHashCommandString);
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
                    System.out.println(Helpers.CommandsHelper.setFileValidationCommandString);
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
     * Converter um ArrayList para uma string separada por vírgulas
     * @param list ArrayList de strings
     * @return todas as strings do arraylist separadas por vírgulas
     */
    private static String listToString(ArrayList<String> list) {
        String result = "";
        for (int i = 0; i < list.size(); i++) {
            if(i==list.size()-1)
                result += list.get(i);
            else
                result += list.get(i)+",";
        }
        return result;
    }

    /**
     * Converter um arraylist com strings de caminhos de ficheiros para um arraylist com os nomes dos ficheiros
     * @param list ArrayList com os caminhos
     * @return ArrayList com os nomes dos ficheiros
     */
    private static ArrayList<String> retreiveFileName(ArrayList<String> list) {
        ArrayList<String> r = new ArrayList<>();
        for (String s : list) {
            File f = new File(s);
            r.add(f.getName());
        }
        return r;
    }

    /**
     * Alguns ficheiros podem conter espaços, aqui juntamos o restante nome do ficheiro
     * @param words array que contem o nome do ficheiro
     * @return nome do ficheiro numa unica string
     */
    private static String joinFileName(String[] words, int offset) {
        String r = "";
        for (int i = offset; i < words.length; i++) {
            if (i == offset)
                r += words[i];
            else
                r += " " + words[i];
        }
        return r;
    }
}

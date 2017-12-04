/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;


public class Helpers {
    
    public static class FileHelpers {

        /**
         * Apaga um diretório recursivamente
         * @param directory diretório a ser apagado
         * @return verdadeiro se foi apagado caso contrario falso
         */
        public static void deleteDirectory(File directory) {
            if(directory.exists()){
                File[] files = directory.listFiles();
                if(null!=files){
                    for(int i=0; i<files.length; i++) {
                        if(files[i].isDirectory()) {
                            deleteDirectory(files[i]);
                        }
                        else {
                            files[i].delete();
                        }
                    }
                }
            }
            directory.delete();
        }

        /**
         * Lê um ficheiro inteiro para a memoria. Não recomendado para ficheiros
         * grandes
         * @param filePath caminho completo para o ficheiro
         * @return         devolve os bytes do ficheiro
         * @throws Exception
         */
        public static byte[] readFile(String filePath) throws Exception {
            File file = new File(filePath);
            byte[] getBytes = new byte[(int) file.length()];
            FileInputStream fsIn = new FileInputStream(file);
            fsIn.read(getBytes);
            fsIn.close();
            return getBytes;
        }

        /**
         * Escreve um ficheiro para o disco.
         * @param filepath   Ficheiro onde serão escritos os bytes
         * @param extension  Extensão do ficheiro
         * @param bytes      Bytes para serem escritos
         * @throws Exception Excepção generica
         */
        public static void writeFile(String filepath, String extension, byte[] bytes) throws Exception {
            FileOutputStream fsOut = new FileOutputStream(filepath + extension);
            fsOut.write(bytes);
            fsOut.close();
        }

    }

    public static class CommandsHelper {

        public static class Exception extends java.lang.Exception {

            public CommandsHelper.Exception.Exceptions getFlag() {
                return flag;
            }

            private CommandsHelper.Exception.Exceptions flag;

            public enum Exceptions {
                unknownCommand,
                unknownOption
            }

            public Exception (String message, CommandsHelper.Exception.Exceptions flag) {
                super (message);
                this.flag = flag;
            }

        }

        public static final String availableCommands =
                "Available commands:\n" +
                "restorefile validatefile setcypher sethash setenabled showconfig help exit\n";

        public static final String restoreFileOptionsString =
                "restorefile [options]\n" +
                "-l  Lists all the files in the trash\n" +
                "-p  <File> The pin to decrypt the file\n";

        public static final String validateFileCommandString = "validate [file]\n" +
                "INFO: If the file is in the restored directory you will only need to type the filename\n" +
                "NOTE: The public key and the signature file must be in the same directory\n";

        public static final String setCypherCommandString =
                "setcypher [cyphertype] [keysize]\n" +
                "Cypher Types:\naes_cbc\n";

        // Não irá ser mostrada, apenas a verificação via Assinatura digital se encontra
        // totalmente implementada e funcional
        public static final String setFileValidationCommandString =
                "setvalidation [validationmethod]\n" +
                "Available Methods:\nhash\ndigital_signature\n";

        public static final String setHashCommandString =
                "sethash [hashtype]\n" +
                "Hash Algorithms:\nsha256\n";


        public static final String helpString =
                "\nFile Restore\n" + restoreFileOptionsString +
                "\n\nFile Validation\n" + validateFileCommandString +
                "\n\nSettings Manager\n" + setCypherCommandString + "\n" + setHashCommandString + "\n" +
                "setenabled [boolean]\n";


        /**
         * All the commands supported by the program
         */
        public enum Commands {
            help,
            restorefile,
            validatefile,
            setcypher,
            setvalidation,
            sethash,
            setenabled,
            showconfig,
            exit,
            nocommand
        }

        /**
         * All the options available for the restorefile command
         */
        public enum RestoreFileOptions {
            listfiles,
            decryptfile
        }

        /**
         * Traduz uma string para o comando correto
         * @param command string que contem o comando
         * @return enum do comando para facilitar a utilização
         * @throws CommandsHelper.Exception excepção de comando não encontrado
         */
        public static Commands translateToCommand(String command) throws CommandsHelper.Exception {
            switch (command) {
                case "restorefile":
                    return Commands.restorefile;
                case "validatefile":
                    return Commands.validatefile;
                case "setcypher":
                    return Commands.setcypher;
                case "setvalidation":
                    return Commands.setvalidation;
                case "sethash":
                    return Commands.sethash;
                case "setenabled":
                    return Commands.setenabled;
                case "showconfig":
                    return Commands.showconfig;
                case "exit":
                    return Commands.exit;
                case "help":
                    return Commands.help;
                default:
                    throw new CommandsHelper.Exception("Unknown Command", Exception.Exceptions.unknownCommand);
            }
        }

        /**
         * Traduz uma string para a opção correta
         * @param string opção introduzida
         * @return enum da opção para facilitar a utilização
         * @throws CommandsHelper.Exception excepção de opção não encontrada
         */
        public static RestoreFileOptions translateToRestoreFileOption(String string) throws CommandsHelper.Exception {
            switch (string) {
                case "-l":
                    return RestoreFileOptions.listfiles;
                case "-p":
                    return RestoreFileOptions.decryptfile;
                default:
                    throw new CommandsHelper.Exception("Unknown option" + string + "for command restorefile",
                                Exception.Exceptions.unknownOption);

            }
        }

    }

    public static class Encryptor {

        /**
         * Encrypta um ficheiro dado um nome do ficheiro e caminho e o cypher a ser usado
         * @param fileName  nome do ficheiro
         * @param filePath  caminho para o ficheiro
         * @param cypher    cypher a ser usado
         */
        public static void Encrypt(String fileName, String filePath, String cypher, String hash) {
            try {

                String outFile = "Fall_Into_Oblivion/Trashed/" + fileName + "/" + fileName;

                // TEMPORARY debugging purposes only
                // Create the hash of the pin 0000
                // So far we were only able to use 16 Byte key
                // 24 Byte or 32 Byte will say Invalid Key Size, even though it's a valid key size
                // TODO: Randomize 3 or 4 digit pin without printing it to the user
                String pinHASH = SHA256.calculateStringMAC("0000");
                pinHASH = pinHASH.subSequence(0, 16).toString();

                // Encrypt the file using the defined cypher type, it defaults to AES-CBC
                switch (cypher) {
                    case "aes_cbc":
                        outFile += ".aescbc";
                        AES_CBC.copy(Cipher.ENCRYPT_MODE, filePath, outFile , pinHASH,"0000000000000000");
                        break;
                    default:
                        outFile += ".aescbc";
                        AES_CBC.copy(Cipher.ENCRYPT_MODE, filePath, outFile , pinHASH,"0000000000000000");
                        break;
                }

                // Calculates the hash of the encrypted file with the defined hash algorithm
                // it defaults to SHA256
                switch (hash) {
                    case "sha256":
                        Helpers.FileHelpers.writeFile(outFile, ".hash", SHA256.calculateMACBytes(outFile));
                        break;
                    default:
                        Helpers.FileHelpers.writeFile(outFile, ".hash", SHA256.calculateMACBytes(outFile));
                        break;
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        /**
         * Decripta um ficheiro dado um pin e o caminho para o ficheiro
         * @param pin   Pin para desencriptar
         * @param file  ficheiro para desencriptar
         */
        public static void Decrypt(String pin, String file) {
            boolean deleteFiles = true;
            try {

                // Calcular o HASH usando SHA256 do pin e gerar a chave de 16 bytes
                String pinHASH = SHA256.calculateStringMAC(pin);
                pinHASH = pinHASH.subSequence(0, 16).toString();

                // Carregar o ficheiro encriptado para memoria, desencriptar e guardar
                // TODO: not all files will be aescbc
                File encFile = new File("Fall_Into_Oblivion/Trashed/" + file + "/" + file + ".aescbc");

                // Check if the file is valid
                byte[] checker = SHA256.calculateMACBytes(encFile.getAbsolutePath());
                byte[] storedHash = Helpers.FileHelpers.readFile(encFile.getAbsolutePath() + ".hash");

                if (Arrays.equals(checker, storedHash)) {

                    // Criar a pasta que vai guardar a assinatura e o novo ficheiro
                    File folder = new File("Fall_Into_Oblivion/Restored/" + file);
                    if (!folder.exists()) {
                        folder.mkdir();
                    }

                    String outFile = "Fall_Into_Oblivion/Restored/" + file + "/" + file;
                    try {
                        AES_CBC.copy(Cipher.DECRYPT_MODE, encFile.getAbsolutePath(), outFile,
                            pinHASH, "0000000000000000");
                        System.out.println("The file was unlocked!\n" +
                            "The file is now located in \"Fall_Into_Oblivion/Restored/" + file + "/\"");
                    } catch (Exception ex) {
                        System.out.println("The pin " + pin + " is incorrect");
                        deleteFiles = false;
                    }

                    Helpers.FileHelpers.writeFile(outFile, ".sig",
                            Helpers.FileHelpers.readFile("Fall_Into_Oblivion/Trashed/" + file + "/" + file + ".sig"));
                    Helpers.FileHelpers.writeFile(outFile, ".pk",
                            Helpers.FileHelpers.readFile("Fall_Into_Oblivion/Trashed/" + file + "/" + file + ".pk"));

                } else {
                    System.out.println("CRITYCAL ERROR: The file is corrupted!\n");
                }

                if (deleteFiles) {
                    // We do not want corrupted files in the trash, so we delete them
                    File dir = new File("Fall_Into_Oblivion/Trashed/" + file + "/");
                    Helpers.FileHelpers.deleteDirectory(dir);
                } else {
                    File dir = new File("Fall_Into_Oblivion/Restored/" + file + "/");
                    Helpers.FileHelpers.deleteDirectory(dir);
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

    }
    
}

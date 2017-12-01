/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 *
 * @author pedrocavaleiro
 */
public class Helpers {
    
    public static class FileHelpers {

        /**
         * Apaga um diretório recursivamente
         * @param directory diretório a ser apagado
         * @return verdadeiro se foi apagado caso contrario falso
         */
        public static boolean deleteDirectory(File directory) {
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
            return(directory.delete());
        }

        /**
         * Lê um ficheiro inteiro para a memoria. Não recomendado para ficheiros
         * grandes
         * @param filePath caminho completo para o ficheiro
         * @return         devolve os bytes do ficheiro
         * @throws Exception
         */
        public static byte[] umFicheiro(String filePath) throws Exception{
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
        public static void escreverFicheiro(String filepath, String extension, byte[] bytes) throws Exception {
            FileOutputStream fsOut = new FileOutputStream(filepath + "." + extension);
            fsOut.write(bytes);
            fsOut.close();
        }

    }
    
}

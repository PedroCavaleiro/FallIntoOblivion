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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        List<String> lines = Arrays.asList("The first line", "The second line");
        Path file = Paths.get("config.txt");
        Files.write(file, lines, Charset.forName("UTF-8"));
        List<String> configs = Arrays.asList();
        String choice =new String();
        while(true){
            System.out.print("FallIntoOblivion> ");
            choice=Ler.umaString();
            String[] words = choice.split("\\s+");
            for(int i=0;i<words.length;i++){
                System.out.println(words[i]);
            }
        }
        }
    }
    

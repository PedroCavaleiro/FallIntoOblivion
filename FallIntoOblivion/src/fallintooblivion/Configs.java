package fallintooblivion;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configs {
    public static Properties prop = new Properties();
    
    /**
     * Guardamos uma definição do programa
     * @param title     nome da definição
     * @param property  configuração
     */
    public void saveProp(String title, String property){
        try{
            prop.setProperty(title,property);
            prop.store(new FileOutputStream("config.cfg"), null);
        } catch(IOException e) {
            System.out.println("Something went wrong saving your configs");
        }
    }
    
    /**
     * Lêmos uma definição do programa
     * @param title nome da definição
     * @return valor da definição
     */
    public String getProp(String title){
        String returnMe = "";
        try{
            prop.load(new FileInputStream("config.cfg"));
            returnMe = prop.getProperty(title);
        } catch(IOException e) {
            System.out.println("Something went wrong loading your configs, creating default cfg file");
        }
        return returnMe;
    }
}

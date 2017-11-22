package fallintooblivion;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configs {
    public static Properties prop = new Properties();
    
    public void saveProp(String title, String property){
        try{
            prop.setProperty(title,property);
            prop.store(new FileOutputStream("config.cfg"), null);
        } catch(IOException e) {
            System.out.println("Something went wrong saving your configs");
        }
    }
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

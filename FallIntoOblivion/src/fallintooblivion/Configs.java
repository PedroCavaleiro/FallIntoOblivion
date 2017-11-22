package fallintooblivion;

import java.io.FileOutputStream;
import java.io.IOException;
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
}

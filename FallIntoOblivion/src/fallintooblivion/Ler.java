package fallintooblivion;
import java.io.*;
public class Ler{
    
    /**
     * Lê uma string do buffer
     * @return string lida
     */
    public static String umaString (){
        String s = "";
        try{
            BufferedReader in = new BufferedReader ( new InputStreamReader (System.in));
            s= in.readLine();
        }
        catch (IOException e){
            System.out.println("Opção não válida.");
            System.out.print("");
        }
        return s;
    }
    
    /**
     * Lê um int do buffer
     * @return int lido
     */
    public static int umInt(){
        while(true){
            try{
                return Integer.valueOf(umaString().trim()).intValue();
            }
            catch(Exception e){
                System.out.println("Opção não válida.");
                System.out.print("");
            }
        }
    }
    
    /**
     * Lê um double do buffer
     * @return double lido
     */
    public static double umDouble(){
        while(true){
            try{
                return Double.valueOf(umaString().trim()).doubleValue();
            }
            catch(Exception e){
            System.out.println("Opção não válida.");
            System.out.print("");
            }
        }
    }
}

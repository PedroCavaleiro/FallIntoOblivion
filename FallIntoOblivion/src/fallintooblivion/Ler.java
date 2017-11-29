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
   
    public static void escreverFicheiro(String filepath, String extension, byte[] bytes) throws Exception {
        FileOutputStream fsOut = new FileOutputStream(filepath + "." + extension);
        fsOut.write(bytes);
        fsOut.close();
    }
    
}

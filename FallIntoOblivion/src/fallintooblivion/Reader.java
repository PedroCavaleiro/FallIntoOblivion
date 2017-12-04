package fallintooblivion;
import java.io.*;
public class Reader {

    public static class Exception extends java.lang.Exception {

        public Exceptions getFlag() {
            return flag;
        }

        private Exceptions flag;

        public enum Exceptions {
            stringReaderError,
            intReaderError,
            doubleReaderError,
            booleanReaderError
        }

        public Exception (String message, Exceptions flag) {
            super (message);
            this.flag = flag;
        }

    }
    
    /**
     * Lê uma string do buffer
     * @return string lida
     */
    public static String readString () throws Reader.Exception {
        String s = "";
        try{
            BufferedReader in = new BufferedReader ( new InputStreamReader (System.in));
            s= in.readLine();
        }
        catch (IOException e){
            throw new Reader.Exception("Unable to read the string", Exception.Exceptions.stringReaderError);
        }
        return s;
    }
    
    /**
     * Lê um int do buffer
     * @return int lido
     */
    public static int readInt() throws Reader.Exception {
        while(true){
            try{
                return Integer.valueOf(readString().trim()).intValue();
            }
            catch(Exception e){
                throw new Reader.Exception("Unable to read the integer", Exception.Exceptions.intReaderError);
            }
        }
    }
    
    /**
     * Lê um double do buffer
     * @return double lido
     */
    public static double readDouble() throws Reader.Exception {
        while(true){
            try{
                return Double.valueOf(readString().trim()).doubleValue();
            }
            catch(Exception e){
                throw new Reader.Exception("Unable to read the double", Exception.Exceptions.doubleReaderError);
            }
        }
    }

    /**
     * Lê um boolean do buffer
     * @return
     */
    public static boolean readBool() throws Reader.Exception {
        while(true) {
            try {
                return Boolean.parseBoolean(readString().trim().toLowerCase());
            } catch (Exception ex) {
                throw new Reader.Exception("Unable to read the boolean", Exception.Exceptions.booleanReaderError);
            }
        }
    }

}

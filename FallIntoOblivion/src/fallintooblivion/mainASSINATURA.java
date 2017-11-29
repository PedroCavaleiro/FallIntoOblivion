/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;

import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.System.in;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

/**
 *
 * @author ricar
 */
public class mainASSINATURA {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, FileNotFoundException, SignatureException, InvalidKeyException, Exception {
      Assinatura a= new Assinatura();
      //a.gerarChaves("ola.txt", "signature", "pubKey");
      System.out.println(a.verificaAssinatura("ola.txt", "signature", "pubKey"));
    }
    
}

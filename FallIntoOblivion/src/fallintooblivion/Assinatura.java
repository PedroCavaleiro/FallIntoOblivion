/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;

/**
 *
 * @author ricar
 */
public class Assinatura {
    Signature signature;
    FileInputStream fIn;
    FileOutputStream fOutA;
    PublicKey pub;
    byte[] finalSign;
    
    
    public void gerarChaves() throws NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException, IOException, SignatureException{
    signature = Signature.getInstance("DSA");
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
    SecureRandom secureRan= new SecureRandom();
    kpg.initialize(512, secureRan);
    KeyPair parChaves= kpg.generateKeyPair();
    PrivateKey priv= parChaves.getPrivate();
    pub= parChaves.getPublic();
    
    signature.initSign(priv);
    
    fIn = new FileInputStream("ola.txt"); //ler ficheiro!
    BufferedInputStream buff=new BufferedInputStream(fIn);
    byte[] buffer=new byte[1024];
    int len;
    
    while((len=buff.read(buffer) )>=0){
        signature.update(buffer, 0, len);
    };
    
    buff.close();
    
    finalSign=signature.sign();
    
    //Guardar assinatura num ficheiro
    
    fOutA = new FileOutputStream("sig");
    fOutA.write(finalSign);
    fOutA.close();
    
    //Guardar chave publica num ficheiro
    byte[] key=pub.getEncoded();
    FileOutputStream fOutPK = new FileOutputStream("PubKey");
    fOutPK.write(key);
    fOutPK.close();
    
    
    }

   public void verificaAssinatura() throws NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException, Exception{
       
       FileInputStream keyfis = new FileInputStream("PubKey");
        byte[] encKey = new byte[keyfis.available()]; 
        keyfis.read(encKey);
        keyfis.close();
       
       X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
       
       KeyFactory keyFactory = KeyFactory.getInstance("DSA");
       PublicKey pubKey =keyFactory.generatePublic(pubKeySpec);
       
       FileInputStream sigfis = new FileInputStream("sig");
        byte[] sigToVerify = new byte[sigfis.available()]; 
        sigfis.read(sigToVerify);
        sigfis.close();
       
       
       Signature sign2 = Signature.getInstance("DSA");
       sign2.initVerify(pubKey);
       
       FileInputStream datafis = new FileInputStream("ola.txt");
       BufferedInputStream bufin = new BufferedInputStream(datafis);

        byte[] buffer = new byte[1024];
        int len;
        while (bufin.available() != 0) {
            len = bufin.read(buffer);
            sign2.update(buffer, 0, len);
        };

        bufin.close();
       
       
       if(sign2.verify(sigToVerify)){
           System.out.println("verifica");
       } 
       else {
           System.out.println("nao verifica");
       }
       
       
   }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;


import java.io.BufferedInputStream;
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
    
    /**
     * Gera as chaves e cria uma assinatura para um ficheiro
     * @param inFilePath                   caminho para o ficheiro a ser assinado
     * @param outSignature                 caminho para guardar a assinatura
     * @param outPK                        caminho para guardar a chave publica
     * @throws NoSuchAlgorithmException    Excepção para algoritmo invalido
     * @throws InvalidKeyException         Excepção para chave invalida
     * @throws FileNotFoundException       Excepção para ficheiro não encontrado
     * @throws IOException                 Excepção generica de input / output
     * @throws SignatureException          Excepção de assinatura
     */
    public void gerarChaves(String inFilePath, String outSignature, String outPK) throws NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException, IOException, SignatureException{
        signature = Signature.getInstance("DSA");
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
        SecureRandom secureRan= new SecureRandom();
        kpg.initialize(512, secureRan);
        KeyPair parChaves= kpg.generateKeyPair();
        PrivateKey priv= parChaves.getPrivate();
        pub= parChaves.getPublic();

        signature.initSign(priv);

        fIn = new FileInputStream(inFilePath); //ler ficheiro | MOD: Passamos o nome do ficheiro como parametro
        BufferedInputStream buff=new BufferedInputStream(fIn);
        byte[] buffer=new byte[1024];
        int len;

        while((len=buff.read(buffer) )>=0){
            signature.update(buffer, 0, len);
        };

        buff.close();

        finalSign=signature.sign();

        //Guardar assinatura num ficheiro
        // MOD: Valor passado por parametro
        fOutA = new FileOutputStream(outSignature);
        fOutA.write(finalSign);
        fOutA.close();

        //Guardar chave publica num ficheiro
        // MOD: Valor passado por parametro
        byte[] key=pub.getEncoded();
        FileOutputStream fOutPK = new FileOutputStream(outPK);
        fOutPK.write(key);
        fOutPK.close();
  
    }

    /**
     * 
     * @param inFilePath                 caminho para o ficheiro a ser assinado
     * @param inSignature                caminho para a assinatura
     * @param inPK                       caminho para a chave publica
     * @return                           verdadeiro se a assinatura se verificar caso contrario falso
     * @throws NoSuchAlgorithmException  Excepção para algoritmo inexistente
     * @throws InvalidKeyException       Excepção para chave invalida
     * @throws FileNotFoundException     Excepção para ficheiro não encontrado
     * @throws Exception                 Excepção genérica
     */
   public boolean verificaAssinatura(String inFilePath, String inSignature, String inPK) throws NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException, Exception{
       
       FileInputStream keyfis = new FileInputStream(inPK);
        byte[] encKey = new byte[keyfis.available()]; 
        keyfis.read(encKey);
        keyfis.close();
       
       X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
       
       KeyFactory keyFactory = KeyFactory.getInstance("DSA");
       PublicKey pubKey =keyFactory.generatePublic(pubKeySpec);
       
       FileInputStream sigfis = new FileInputStream(inSignature);
        byte[] sigToVerify = new byte[sigfis.available()]; 
        sigfis.read(sigToVerify);
        sigfis.close();
       
       
       Signature sign2 = Signature.getInstance("DSA");
       sign2.initVerify(pubKey);
       
       FileInputStream datafis = new FileInputStream(inFilePath);
       BufferedInputStream bufin = new BufferedInputStream(datafis);

        byte[] buffer = new byte[1024];
        int len;
        while (bufin.available() != 0) {
            len = bufin.read(buffer);
            sign2.update(buffer, 0, len);
        };

        bufin.close();
       
       
       if(sign2.verify(sigToVerify)){
           return true;
       } 
       else {
           return false;
       }
   }
}

//JOÃO MARCELLO BESSA - 1720539
//RAFAEL RAMOS FELICIANO - 1521772

import java.security.*;
import java.util.Arrays;
import javax.crypto.*;

public class MySignature {
    
    private MessageDigest messageDigest;
    private static Cipher cipher;
    
    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private final String algorithm;
            
            
    public MySignature(String algorithm) throws Exception {
        this.algorithm = algorithm;
	String[] aux = algorithm.split("with");
        
        switch (aux[0])
        {
            case "MD5":
                messageDigest = MessageDigest.getInstance("MD5");
                break;
            case "SHA1":
                messageDigest = MessageDigest.getInstance("SHA1");
                break;
            case "SHA256":
                messageDigest = MessageDigest.getInstance("SHA256");
                break;
            case "SHA512":
                messageDigest = MessageDigest.getInstance("SHA512");
                break;
        }
        
        cipher = Cipher.getInstance(aux[1]);
    }
    

    public static MySignature getInstance(String algorithm) throws Exception{
    	MySignature signature = new MySignature(algorithm);
    	return signature;
    }
    
    public void initSign(PrivateKey _privateKey) throws InvalidKeyException {
        try {
            privateKey = _privateKey;
        } catch (ClassCastException cce) {
            throw new InvalidKeyException("Chave privada está errada!");
        }
    }
    
    public void update(byte[] text) {
    	messageDigest.update(text);
    }
    
    public byte[] sign() throws NoSuchAlgorithmException , BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        byte[] mySign = null;
        
        byte[] digest = messageDigest.digest();
        
        System.out.print("\nDigest a ser gerado: ");
        for (int i = 0; i != digest.length; i++)
            System.out.print(String.format("%02X", digest[i]));
        
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        mySign = cipher.doFinal(digest);
        
        return mySign;
    }
    
    public static void initVerify(PublicKey _publicKey) throws InvalidKeyException {
        try {
            publicKey = _publicKey;
        } catch (ClassCastException cce) {
            throw new InvalidKeyException("Chave pública está errada!");
        }
    }
    
    public boolean verify (byte[] signature) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    	cipher.init(Cipher.DECRYPT_MODE, publicKey);
    	
        byte[] digest = messageDigest.digest();
        byte[] newPlainText = cipher.doFinal(signature);
        
        System.out.print("\nDigest a ser recebido: ");
        for (int i = 0; i != newPlainText.length; i++)
            System.out.print(String.format("%02X", newPlainText[i]));

        return Arrays.equals(newPlainText, digest);
    }
    
}

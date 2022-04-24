import java.security.*;
import javax.crypto.*;
import java.util.Arrays;

public class MySignature {
    
    private MessageDigest messageDigest;
    private Cipher cipher;
    
    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private String algorithm;
            
            
    public MySignature(String algorithm) throws NoSuchAlgorithmException{
        this.algorithm = algorithm;
	String aux = algorithm.split("With")[0];
        this.messageDigest =  MessageDigest.getInstance(aux);
    }
    

    public static MySignature getInstance(String algorithm) throws NoSuchAlgorithmException {
    	MySignature signature = new MySignature(algorithm);
    	return signature;
    }
    
    public static void initSign(PrivateKey _privateKey) throws InvalidKeyException {
        try {
            privateKey = _privateKey;
        } catch (ClassCastException cce) {
            throw new InvalidKeyException("Chave privada está errada");
        }
    }
    
    public void update(byte[] text) {
    	messageDigest.update(text);
    }
    
    public byte[] sign() throws NoSuchAlgorithmException , BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        byte[] mySign = null;
        String[] algorithms = algorithm.split("With");
        
        switch (algorithms[0])
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
        
        byte[] digest = messageDigest.digest();
        System.out.print("\nDigest gerado: ");
        System.out.println(digest.toString());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        mySign = cipher.doFinal(digest);
        
        return mySign;
    }
    
    public static void initVerify(PublicKey _publicKey) throws InvalidKeyException {
        try {
            publicKey = _publicKey;
        } catch (ClassCastException cce) {
            throw new InvalidKeyException("Chave pública está errada");
        }
    }
    
    public boolean verify (byte[] signature) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    	cipher.init(Cipher.DECRYPT_MODE, publicKey);
    	
        byte[] digest = messageDigest.digest();
        byte[] newPlainText = cipher.doFinal(signature);
        
        System.out.print("\nDigest recebido: ");
        for (int i = 0; i != newPlainText.length; i++)
            System.out.print(String.format("%02X", newPlainText[i]));

        if(Arrays.equals(newPlainText, digest)) 
            return true;
        return false;
    }
    
}

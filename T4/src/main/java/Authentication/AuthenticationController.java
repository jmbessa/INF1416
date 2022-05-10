package Authentication;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.Signature;


//
// encripta e desencripta utilizando DES
public class AuthenticationController {
	
	 public static void SymmetricKeyChipher (String pswrd, Path path, String certificate, String message) throws Exception {
	    
		//Inicializa a chave Privada
		PrivateKey usrPrivK = null;
	
		
	    // gera o PRNG
	    System.out.println( "\nStart generating DES key" );
	    SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
	    secureRandom.setSeed(pswrd.getBytes());
	    
	    // Criação da chave DES para decriptar o arquivo
	    KeyGenerator keyGen = KeyGenerator.getInstance("DES");
	    keyGen.init(56, secureRandom);
	    Key key = keyGen.generateKey();
	    System.out.println( "Finish generating DES key" );
	    
	    
	    
	    // Decriptação do arquivo
	    Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	    System.out.println( "\n" + cipher.getProvider().getInfo() );
	   
	    
	    // chave privada em base 64
	    System.out.println( "\nStart symetric decryption\n" );
	    cipher.init(Cipher.DECRYPT_MODE, key);
	    byte[] pathAsBytes = Files.readAllBytes(path);
	    byte[] cipherKey = cipher.doFinal(pathAsBytes);
	    System.out.println( "Finish symetric decryption:\n" );
	    
	    
	    String cipherKeyAsString = new String(cipherKey);
	    
		System.out.println(cipherKeyAsString);
		cipherKeyAsString = cipherKeyAsString.replace("-----BEGIN PRIVATE KEY-----\n","");
		cipherKeyAsString = cipherKeyAsString.replace("-----END PRIVATE KEY-----\n","");
		byte[] decipherKey = Base64.getMimeDecoder().decode(cipherKeyAsString);
		PKCS8EncodedKeySpec pkcs8DecipherKey = new PKCS8EncodedKeySpec(decipherKey);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		
		usrPrivK = keyFactory.generatePrivate(pkcs8DecipherKey);
		
		
		//Pegar a chave pública do certificado
		   
		byte[] certificateAsByte = Files.readAllBytes(Paths.get(certificate));
        CertificateFactory myCertificate = CertificateFactory.getInstance("X.509");
        InputStream certiAsInputStream = new ByteArrayInputStream(certificateAsByte);
        X509Certificate x509Certificate = (X509Certificate) myCertificate.generateCertificate(certiAsInputStream);
		
		PublicKey usrPublK = x509Certificate.getPublicKey();
		
		// Decripta o arquivo	
		byte[] messageAsBytes = message.getBytes("UTF8");
		
		
		// Verifica a assinatura digital
	     Signature sig = Signature.getInstance("SHA1withRSA");
	     sig.initSign(usrPrivK);
	     sig.update(messageAsBytes);
	     byte[] signature= sig.sign();
	     System.out.println( sig.getProvider().getInfo() );
	     System.out.println( "\nSignature:" );
	        
	     StringBuffer buf = new StringBuffer();
	     for(int i = 0; i < signature.length; i++) {
	        String hex = Integer.toHexString(0x0100 + (signature[i] & 0x00FF)).substring(1);
	        buf.append((hex.length() < 2 ? "0" : "") + hex);
	     }
	        
	     System.out.println( buf.toString() );
	        
	     System.out.println( "\nStart signature verification" );
	        
	     sig.initVerify(usrPublK);
	     sig.update(messageAsBytes);
	     try {
	         if (sig.verify(signature)) {
	           System.out.println( "Signature verified" );
	         } else System.out.println( "Signature failed" );
	      } catch (SignatureException se) {
	         System.out.println( "Singature failed" );
	        }        
	}
	       
	    

    public static void main (String[] args) throws Exception {

    	Path file;
    	
   	        if(args.length < 3) {
   	            System.err.println("Err");
   	            System.exit(1);
   	        }
   	        file = Paths.get(args[1]);

   	        SymmetricKeyChipher(args[0],file, args[2], args[3]);

   	}

}

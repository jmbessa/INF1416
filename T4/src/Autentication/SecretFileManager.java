//JOÃO MARCELLO BESSA RODRIGUES - 1720539
//RAFAEL RAMOS FELICIANO - 1521772

package Autentication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class SecretFileManager {
	
	PrivateKey privateKey;
	PublicKey publicKey; 
	Autenticator aut;

	public SecretFileManager( PrivateKey privateK, PublicKey publicK, Autenticator a  ) { 
		privateKey = privateK;
		publicKey = publicK;
		aut = a;
	}
	
	public byte[] listAllFiles(String dir) throws Exception {
		byte[] listFiles = null;

		try {
			// Primeira Etapa pegar seed em arquivo .env e gerar a key.
			Key seedKey = getSeedKey(dir);
			// Em seguida, utiliza a seedKey para decryptar o arquivo .enc e pegar o plainText.
			byte [] decryptedFile = decryptFileWithSeedKey(dir, seedKey);
			if( dir.contains("index") ) {
				aut.insertRegistro(8005, -1, null, true);
			} else {
				aut.insertRegistro(8013, -1, dir, true);
			}
			
			// Agora valida o arquivo decryptado com a assinatura gerada pela keys do usuario
			if( !verifySignature( dir, decryptedFile ) ) {
				if( dir.contains("index") ) {
					aut.insertRegistro(8008, -1, null, true);
				} else {
					aut.insertRegistro(8016, -1, dir, true);
				}
			} else {
				if( dir.contains("index") ) {
					aut.insertRegistro(8006, -1, null, true);
				} else {
					aut.insertRegistro(8014, -1, dir, true);
				}
			}
			//listFiles = treatByteFileToStringList(decryptedFile);
			listFiles = decryptedFile;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}	
		
		return listFiles;
		
	}
	
	public Key getSeedKey(String dir ) throws Exception {
		// Gera a seed a partir do arquivo .env
        Path path = Paths.get(dir + ".env");
        if( !path.toFile().exists() ) {
        	aut.insertRegistro(8004, -1, null, true);
        	throw new Exception("Caminho inválido");
        }
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] fileBytes = Files.readAllBytes(path);
        byte[] seed = cipher.doFinal(fileBytes);
        // Com a seed gera a Key para a próxima etapa
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(seed);
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        keyGenerator.init(56, secureRandom);

        return keyGenerator.generateKey();
    }

	private byte[] decryptFileWithSeedKey(String filePath, Key seedKey) throws Exception {
    	byte[] fileBytes = null;
        try {
        	// Gera o conteudo do arquivo a partir da seedKey e do arquivo .enc
            Path path = Paths.get(filePath + ".enc");
            if( !path.toFile().exists() ) {
            	System.out.println(path.toFile().getName().toString());
            	aut.insertRegistro(8004, -1, null, true);
            	throw new Exception("Caminho inválido. Não possui arquivo .enc");
            }
            fileBytes = Files.readAllBytes(path);
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, seedKey);

            fileBytes = cipher.doFinal(fileBytes);
        } catch (Exception e) {
        	if( filePath.contains("index") ) {
        		aut.insertRegistro(8007, -1, null, true);
        	} else {
        		aut.insertRegistro(8014, -1, filePath, true);
        	}
            throw new Exception(e.getMessage());
        }
        
        return fileBytes; 
    }

    private boolean verifySignature(String dir, byte[] decryptedFile) throws Exception {
    	// A assinatura esta mantida no arquivo .asd - Verificamos se confere com a gerada pela chave publica com o decryptedFile...
        Path path = Paths.get(dir + ".asd");
        if( !path.toFile().exists() ) {
        	aut.insertRegistro(8004, -1, null, true);
        	throw new Exception("Caminho inválido. Não possui arquivo .asd");
        }
        byte[] asdAsBytes = Files.readAllBytes(path);

        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(publicKey);
        signature.update(decryptedFile);

        return signature.verify(asdAsBytes);
    }
    
    public String[][] treatByteFileToStringList(byte [] decryptedFile ) {
         String decryptedFileAsString = new String(decryptedFile);
         String[] dFileAsStringEntry = decryptedFileAsString.split("\n"); // linha do arquivo - auxiliar para preencher a matrix.
         String[][] dFileAsStringMatrix = new String[dFileAsStringEntry.length][]; // arquivo inteiro em matrix (cada elemento eh um dado da linha)

         for(int i = 0; i < dFileAsStringEntry.length; i++)
        	 dFileAsStringMatrix[i] = dFileAsStringEntry[i].split(" ");

         return dFileAsStringMatrix;
    }
}

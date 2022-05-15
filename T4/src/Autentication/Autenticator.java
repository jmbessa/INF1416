package Autentication;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import DatabaseTables.*;

public class Autenticator {
	
	Usuario currentUserVerification;
	private PublicKey currentPublicKey;
	private PrivateKey currentUserPrivateKey;
	
	String wrongEmail = "";

	public Autenticator() {
		
	}
	
	
//	Na primeira etapa de autenticação, deve-se solicitar a identificação do usuário (login name)
//	no sistema, que deve ser um e-mail válido. O e-mail do usuário deve ser coletado do seu
//	respectivo certificado digital no momento do seu cadastramento no sistema. Se a identificação for
//	inválida, o usuário deve ser apropriadamente avisado e o processo deve permanecer na primeira
//	etapa. Se a identificação for válida e o acesso do usuário estiver bloqueado, o mesmo deve ser
//	apropriadamente avisado e o processo deve permanecer na primeira etapa. Caso contrário, o
//	processo deve seguir para a segunda etapa
	
	public boolean firstStepAutentication(String email) {
		boolean ret = false;

		try {
			insertRegistro(2001, -1, null, false );
			Usuario u = Usuario.getInstance(email);
			ret = (u != null );
			
			if( ret ) {
				currentUserVerification = u;
				insertRegistro(2003, -1, null, true );
			} else {
				wrongEmail = email;
			}

		} catch (Exception e) {
			System.out.println("Não foi possível verificar em banco: " + e.toString());
			e.printStackTrace();
		}
		
		return ret;
	}
	
//	Na segunda etapa, deve-se verificar a senha pessoal do usuário (algo que ele conhece)
//	que é fornecida através de um teclado virtual fonético sobrecarregado com seis botões, cada um
//	com três fonemas, que são distribuídos aleatoriamente e sem repetição entre todos os botões. As
//	senhas pessoais são sempre formadas por quatro, cinco ou seis fonemas da Tabela de Fonemas
//	para Autenticação. A cada pressionamento de um botão, os fonemas são redistribuídos
//	aleatoriamente entre os seis botões. Se a verificação da senha for negativa, o usuário deve ser
//	apropriadamente avisado e o processo deve contabilizar um erro de verificação de senha pessoal.
//	Após três erros consecutivos sem que ocorra uma verificação positiva entre os erros, deve-se
//	seguir para a primeira etapa e o acesso do usuário deve ser bloqueado por 2 minutos (outros
//	usuários poderão tentar ter acesso). Se a verificação for positiva, o processo deve seguir para a
//	terceira etapa
	public boolean secondStepAutentication(String [] sequencias) {
		
		boolean ret = false;
		String [][] passwords = new String[sequencias.length][2];
		
		if( sequencias.length < 8 || sequencias.length > 10 ) {
			System.out.println("Senha precisa ter entre 8 e 10 digitos");
			return false;
		}	
		
		for(int i = 0; i<sequencias.length;i++) {
			String[] separator = sequencias[i].split("-");
			passwords[i] = separator;
		}
		
		// Digest do db é DIGEST + SALT como pedido no enunciado
		String digestDB = currentUserVerification.getDigest();
		String salt = currentUserVerification.getSalt();
		
		// Gera uma arvore de possibilidades. Quando a recursão atinge a folha da arvore, efetua a comparacao
		// com o digestDB. Se for equivalente, retorne true, caso contrario, false.
		try {
			ret = checkPossibilityTree( passwords[0][0], passwords, salt, digestDB, 1 ) || 
				  checkPossibilityTree( passwords[0][1], passwords, salt, digestDB, 1 );
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if( ret ) {
				insertRegistro(3003, -1, null, true);
			}
			insertRegistro(3002, -1, null, true);
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return ret;
	}
	
	private boolean checkPossibilityTree( String currentComp, String [][] password, String salt, String digestDB, int node ) throws NoSuchAlgorithmException {
        boolean ret1;
        boolean ret2;
        
        if( node == password.length ) {
            // Por conta do digestDB ser DIGEST + SALT, precisamos
            // gerar DIGEST + SALT a partir do password encontrado
            // e do salt armazenado no banco.
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update((currentComp + salt).getBytes());
            String digest = byteArrayToHex(messageDigest.digest());
            
            if( digest.equals(digestDB)) {
                System.out.println("Comp correta:" + currentComp);
                return true;
            } else {
                return false;
            }
        } else {
            ret1 = checkPossibilityTree( currentComp + password[node][0], password, salt, digestDB, node+1 );
            ret2 = checkPossibilityTree( currentComp + password[node][1], password, salt, digestDB, node+1 );
        }

        return ret1 || ret2;
    }
	
	
//	Na terceira e última etapa de autenticação, deve-se verificar a chave privada do usuário
//	(algo que ele possui) fornecida para o sistema através de um arquivo binário que armazena o
//	resultado da criptografia da chave privada com o algoritmo simétrico DES/ECB/PKCS5Padding e
//	uma chave secreta. A chave privada não criptografada é representada no padrão PKCS8 e se
//	encontra codificada em BASE64, no formato PEM (Privacy Enhanced Mail). O sistema deve
//	receber a frase secreta de decriptação da chave privada, que deve ser utilizada como semente do
//	SHA1PRNG para recuperar a chave secreta. Depois de decriptar o arquivo binário, deve-se gerar
//	uma assinatura digital no padrão RSA (SHA1withRSA) para um array aleatório de 2048 bytes e,
//	em seguida, verificar a assinatura digital com a chave pública do usuário. Se a verificação for
//	negativa, o usuário deve ser apropriadamente avisado e o processo deve contabilizar um erro de
//	verificação da chave privada, retornando para o início da terceira etapa. Após três erros
//	consecutivos sem que ocorra uma verificação válida da chave privada, deve-se seguir para a
//	primeira etapa e o acesso do usuário deve ser bloqueado por 2 minutos (outros usuários poderão
//	tentar ter acesso). Se a verificação for positiva, o processo deve permitir acesso ao sistema.

	public boolean thirdStepAutentication(String password, Path validationFile) throws Exception {
		boolean ret = false;
		
		if( validationFile.toFile().exists() == false ) {
			insertRegistro(4004, -1, null, true); // caminho invalido de arquivo
			throw new Exception("Caminho invalido de arquivo");
		}
		// Primeiro precisamos pegar a privateKey dentro do validationFile -> de acordo com o enunciado:
		// "um arquivo binário que armazena o resultado da criptografia da chave privada com o algoritmo simétrico DES/ECB/PKCS5Padding"
		// Então geramos um secure random em SHA1PRNG com a frase secreta para recuperar a chave secreta.
		PrivateKey userPrivateKey = null;
		try {
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
	        secureRandom.setSeed(password.getBytes());
	        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
	        keyGenerator.init(56, secureRandom); // 56, pois é DES
	        Key key = keyGenerator.generateKey(); 
	        
	        
	        // Instaciamos o Cipher com os parametros de decryptacao e com a chave gerada pela frase secreta.
	        // Em seguida, decryptamos o arquivo obtendo a chave privada, porém nesse momento ela ainda está codificada 
	        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        byte[] validationFileAsBytes = Files.readAllBytes(validationFile);
	        byte[] codifiedKey = cipher.doFinal(validationFileAsBytes);
	
	        // Precisamos iniciar o processo de decodificacao da chave secreta, que está codificada em BASE64 e representada no padrão PKCS8.
	        // Além disso, tiramos textos de begin key e end key para poder decodificar.
	        String codifiedKeyAsString = new String(codifiedKey);
	//	        System.out.println(codifiedKeyAsString);
	//	        System.out.println("\n\n DEPOIS \n\n");
	        codifiedKeyAsString = codifiedKeyAsString.replace("-----BEGIN PRIVATE KEY-----\n","");
	        codifiedKeyAsString = codifiedKeyAsString.replace("-----END PRIVATE KEY-----\n","");
	        
	//	        System.out.println(codifiedKeyAsString);
	        
	        // Primeiro decodificamos da base64 e em seguida interpretamos do padrao PKCS8
	        byte[] decodifiedKey = Base64.getMimeDecoder().decode(codifiedKeyAsString);
	        PKCS8EncodedKeySpec pkcs8DecodifiedKey = new PKCS8EncodedKeySpec(decodifiedKey);
	        
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        // Nesse momento, temos a chave privada pronta.
	        userPrivateKey = keyFactory.generatePrivate(pkcs8DecodifiedKey);
		} catch ( Exception e ) {
			insertRegistro(4005, -1, null, true);
			throw new Exception("Problema na frase secreta ou no arquivo de validacao");
		}
		
		insertRegistro(4003, -1, null, true);
	
        // Agora precisamos pegar a chave publica do certificado vindo do DB.
        // Criamos um CertificateFactory com o padrão X509 para busca a informacao
        // e transformamos o byteArray em um ByteArrayInputStream para gerar a 
        // instancia de X509Certificate
        byte[] certificateAsByte = currentUserVerification.getCertificate();
        CertificateFactory certiFactory = CertificateFactory.getInstance("X.509");
        InputStream certiAsInputStream = new ByteArrayInputStream(certificateAsByte);
        X509Certificate x509Certificate = (X509Certificate) certiFactory.generateCertificate(certiAsInputStream);

        // Com o certificado instanciado, basta utilizar getPublic.
        PublicKey publicKey = x509Certificate.getPublicKey();
        	        	 
        // Agora, devo gerar uma assinatura digital no padrão RSA (SHA1withRSA) para um array aleatório de 2048 bytes e,
        // em seguida, verificar a assinatura digital com a chave pública do usuário
        byte[] message = new byte[2048];
        (new SecureRandom()).nextBytes(message);

        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(userPrivateKey);
        signature.update(message);
        byte[] signatureAsBytes= signature.sign();

        signature.initVerify(publicKey);
        signature.update(message);
        ret = signature.verify(signatureAsBytes); 
        
        if( ret ) {
        	currentPublicKey = publicKey;
        	currentUserPrivateKey = userPrivateKey;
        	Database db = Database.getInstance();
        	db.incrementUsuarioAcessCount(currentUserVerification.getEmail());
        	db.connection.close();
        	System.out.println("Senha verificada com sucesso!");
        } else {
        	insertRegistro(4006, -1, null, true);
        	throw new Exception("Erro de validacao de assinatura digital");
        }
        
        insertRegistro(4002, -1, null, true);

		return ret;
	}
	
	private static String byteArrayToHex(byte[] b) {
        StringBuffer aux = new StringBuffer();
       
       // ByteArray para Hexa
       for( int byteIdx = 0; byteIdx < b.length; byteIdx++ ) {
           String hex = Integer.toHexString(0x0100 + (b[byteIdx] & 0x00FF)).substring(1);
           aux.append((hex.length() < 2 ? "0" : "") + hex); 
       }
       
       String ret = aux.toString();
       
       return ret;
   }
	
    public void insertRegistro(int id, int data, String arq, boolean registerEmail) throws Exception {
    	try {
    		Registro reg;
    		if( id == 2005 ) {
    			reg = Registro.getInstance(id, data, arq, wrongEmail);
    		} else {
		       	if( registerEmail ) {
		    		reg = Registro.getInstance(id, data, arq, currentUserVerification.getEmail());
		    	} else {
		    		reg = Registro.getInstance(id, data, arq, null);
		    	}
    		}
	       	reg.insertRegistro();
    	} catch( Exception e ) {
			System.out.println("Problema na insercao de registro: " + e.toString());
    	}
	}
    
    public boolean checkIfCurrentUserIfBlocked() {

    	try {
        	Database db = Database.getInstance();
			boolean ret = db.checkIfIsBlocked(currentUserVerification.getEmail());
			System.out.println("Usuario :" + currentUserVerification.getEmail() + " bloqueado? " + ret);
			db.connection.close();
			return ret;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Nao conseguiu verificar se esta bloqueado: " + e.toString());
			closesConnectionIfSomethingWentWrong();
		}
    	
    	return false;
    }
    
    public int applyFailure() throws Exception {
    	Database db = Database.getInstance();
    	int failCount = db.applyFailure(currentUserVerification.getEmail());
    	System.out.println("\nMais uma falha de: " + currentUserVerification.getEmail() + "\nNumero de falhas: " + failCount);
		
    	db.connection.close();
    	return failCount;
    }
    
    public void zeroFailure() throws Exception {
    	Database db = Database.getInstance();
    	db.zeroFails(currentUserVerification.getEmail());
    	db.connection.close();
    }
    
    private void closesConnectionIfSomethingWentWrong() {
    	Database db;
		try {
			db = Database.getInstance();
			db.connection.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public boolean updateCurrentUserCert( Object dado ) { 	
    	Database db;
    	boolean ret = false;
		try {
			db = Database.getInstance();
			ret = db.updateUsuario(currentUserVerification.getEmail(), "certificate", dado);
			db.connection.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
    	return ret;
    }
    
    public boolean updateCurrentUserSenha( Object dado ) { 	
    	Database db;
    	boolean ret = false;
		try {
			db = Database.getInstance();
			ret = db.updateUsuario(currentUserVerification.getEmail(), "senha" , dado);
			db.connection.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
    	return ret;
    }
    
    public int getAllUsersCount() {
    	Database db;
    	int ret = 0;
		try {
			db = Database.getInstance();
			ret = db.getUsuarioCount();
			db.connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	return ret;
    }
    
    public List<Grupo> getAllGrupos() {
    	Database db;
    	ArrayList<Grupo> ret = new ArrayList<Grupo>();
		try {
			db = Database.getInstance();
			ResultSet grupos = db.getAllGrupos();
			boolean aux = true;
			while(grupos.next()) {
				ret.add(Grupo.getInstance(grupos.getInt("idGrupo")));
			}
			db.connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return ret;
    }
    
    public byte[] listSecretFiles(String dir) throws Exception {
    	SecretFileManager sFM = new SecretFileManager(currentUserPrivateKey, currentPublicKey, this);
    	return sFM.listAllFiles(dir);
    }
    
    public String[][] listSecretFilesAsStringMatrix(String dir) throws Exception {
    	SecretFileManager sFM = new SecretFileManager(currentUserPrivateKey, currentPublicKey, this);
    	return sFM.treatByteFileToStringList(sFM.listAllFiles(dir));
    }
	
    public Usuario getCurrentUserVerification() {
		return currentUserVerification;
	}
}

package DatabaseTables;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;

import Autentication.CertificateHelper;


public class Database {

    public Connection connection = null;

    private static Database database = null;

    private Database() {

    }

    public static Database getInstance() throws Exception {
    
        try {
            database = new Database();
            Class.forName("org.sqlite.JDBC");

            String url = "jdbc:sqlite:banco.db";
            database.connection = DriverManager.getConnection(url);

        } catch(ClassNotFoundException e) {
            System.out.println("Nao consegui criar " + e.getMessage());
        }
        
        return database;
    }
    
    public ResultSet getUsuario(String email) throws Exception {
    	System.out.println("Buscando usuario de email: " + email);
		
		String selectUsuario = "SELECT * FROM Usuarios WHERE email = '" + email +"';";
		
        PreparedStatement pS = this.connection.prepareStatement(selectUsuario);
        
        return pS.executeQuery();
    }
    
    public boolean updateUsuario( String email, String campo, Object cam ) throws Exception {
    	String updtUsuario = null;
    	
    	if( campo.equals("senha") ) {
    		String caracteresValidos = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    		SecureRandom secureRandomUsr = new SecureRandom();
    	    byte[] saltUsr = new byte[10];
    	
    	    for(int i = 0; i < saltUsr.length; i++) {
    	    	saltUsr[i] = (byte)caracteresValidos.charAt(
    	    			secureRandomUsr.nextInt(caracteresValidos.length()));
    	    }
    	    
    	    MessageDigest messageDigestUser = MessageDigest.getInstance("SHA1");
    	    messageDigestUser.update((cam + new String(saltUsr)).getBytes());
    	    byte[] digestUser = messageDigestUser.digest();
    	    
    	    updtUsuario = "UPDATE Usuarios SET digest = '" +  byteArrayToHex(digestUser) + "', salt = '" + (new String(saltUsr)) + "'  WHERE email = '" + email +"';";
    	    
    	} else {
    		String certiHelper = ((CertificateHelper) cam).convertToPem();
    		updtUsuario = "UPDATE Usuarios SET '" + campo + "' = '" + certiHelper + "' WHERE email = '" + email +"';";
    	}
		
        PreparedStatement pS = this.connection.prepareStatement(updtUsuario);
      
        return  ( pS.executeUpdate() != 0 );
    }
    
    public boolean insertUsuario( String email, String senha, String filePath, int grupo, String nome ) throws Exception {
        String caracteresValidos = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
        CertificateHelper certiHelper = new CertificateHelper(filePath);
    	
	    SecureRandom secureRandomUsr = new SecureRandom();
	    byte[] saltUsr = new byte[10];
	
	    for(int i = 0; i < saltUsr.length; i++) {
	    	saltUsr[i] = (byte)caracteresValidos.charAt(
	    			secureRandomUsr.nextInt(caracteresValidos.length()));
	    }
	    
	    MessageDigest messageDigestUser = MessageDigest.getInstance("SHA1");
	    messageDigestUser.update((senha + new String(saltUsr)).getBytes());
	    byte[] digestUser = messageDigestUser.digest();
	
	    String usrQuery = "INSERT INTO Usuarios(email, digest, salt, certificate, groupID, accessCount, failCount, nome) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
	    PreparedStatement pS = database.connection.prepareStatement(usrQuery);
	    pS.setString(1, email);
	    pS.setString(2, byteArrayToHex(digestUser));
	    pS.setString(3, new String(saltUsr));
	    pS.setString(4, certiHelper.convertToPem());
	    pS.setInt(5, grupo);
	    pS.setInt(6, 0);
	    pS.setInt(7, 0);
	    pS.setString(8, nome);
	    
	    return ( pS.executeUpdate() != 0 ) ;
    }
    
    public int getUsuarioCount() throws Exception {
    	System.out.println("Contando todos os usuarios...");
		
		String selectUsuario = "SELECT COUNT(*) as total FROM Usuarios ;";
		
        PreparedStatement pS = this.connection.prepareStatement(selectUsuario);
        
        return pS.executeQuery().getInt("total");
    }
    
    public int incrementUsuarioAcessCount( String email ) throws Exception{
    	System.out.println("Incrementando um acesso para: " + email);
		
		String incUsuario = "UPDATE Usuarios SET accessCount = accessCount + 1 WHERE email = '" + email +"';";
		
        PreparedStatement pS = this.connection.prepareStatement(incUsuario);
        
        return pS.executeUpdate();
    }
    
    public int applyFailure( String email ) throws Exception{
		String incUsuario = "UPDATE Usuarios SET failCount = failCount + 1 WHERE email = '" + email +"';";
		
        PreparedStatement pS = this.connection.prepareStatement(incUsuario);
        pS.executeUpdate();
                
        return getUsuario(email).getInt("failCount");
    }
    
    public void zeroFails( String email ) throws Exception {
    	System.out.println("Zerando numero de falhas para: " + email);
		
		String incUsuario = "UPDATE Usuarios SET failCount = 0 WHERE email = '" + email +"';";
		
        PreparedStatement pS = this.connection.prepareStatement(incUsuario);
        pS.executeUpdate();
    }
    
    public ResultSet getGroup(int id) throws Exception {
    	System.out.println("Buscando Grupo de id: " + id);
		
		String selectGrupo = "SELECT * FROM Grupos WHERE idGrupo = '" + id +"';";
		
        PreparedStatement pS = this.connection.prepareStatement(selectGrupo);
        
        return pS.executeQuery();
    }
    
    public ResultSet getAllGrupos() throws Exception {
		String selectGrupo = "SELECT * FROM Grupos ;";
		
        PreparedStatement pS = this.connection.prepareStatement(selectGrupo);
        
        return pS.executeQuery();
    }
    
    public ResultSet getRegistro(int id) throws Exception {
    	System.out.println("Buscando Registro de id: " + id);
		
		String selectGrupo = "SELECT * FROM Registros WHERE id = '" + id +"';";
		
        PreparedStatement pS = this.connection.prepareStatement(selectGrupo);
        
        return pS.executeQuery();
    }
    
    public ResultSet getTodosRegistros() throws Exception {
		String selectGrupo = "SELECT * FROM Registros ORDER BY data ASC;";
		
        PreparedStatement pS = this.connection.prepareStatement(selectGrupo);
        
        return pS.executeQuery();
    }
    
    public boolean checkIfIsBlocked(String email) throws Exception {		
		String selectGrupo = "SELECT * FROM Registros WHERE data >= DATETIME(CURRENT_TIMESTAMP, '-15 seconds') AND (registro = '3007' OR registro = '4007')" +
							 "AND usuario = '" + email + "';";
		
        PreparedStatement pS = this.connection.prepareStatement(selectGrupo);
        
        ResultSet rst = pS.executeQuery();
        
        return !rst.isClosed();
    }
    
    public boolean insertRegistro(int id, int data, String arq, String usuario) throws Exception {
    	
    	String sql = "INSERT INTO Registros(registro";
    	String sqlValues = " VALUES (?";
    	
    	int currentPos = 1;

        if( data != -1 ) {
        	sql = sql.concat(", data");
        	sqlValues = sqlValues.concat(",?");
        }
        
        if( arq != null ) {
        	sql = sql.concat(", arquivo");
        	sqlValues = sqlValues.concat(",?");
        }
        
        if( usuario != null ) {
        	sql = sql.concat(", usuario");
        	sqlValues = sqlValues.concat(",?");
        }
        sql = sql.concat(")");
    	sqlValues = sqlValues.concat(");");
    	
    	PreparedStatement pS = database.connection.prepareStatement(sql+sqlValues);

        pS.setInt(currentPos, id);
        currentPos = currentPos + 1;
        if( data != -1 ) {
            pS.setInt(currentPos, data);
            currentPos = currentPos + 1;
        }
        
        if( arq != null ) {
            pS.setString(currentPos, new String(arq));
            currentPos = currentPos + 1;
        }
        
        if( usuario != null ) {
            pS.setString(currentPos, new String(usuario));
            currentPos = currentPos + 1;
        }
        
        return ( pS.executeUpdate() > 0 );
    }
    
    public ResultSet getMensagem(int registro ) throws Exception {
		String selectMensagem = "SELECT * FROM Mensagens WHERE registro = '" + registro +"';";
		
        PreparedStatement pS = this.connection.prepareStatement(selectMensagem);
        
        return pS.executeQuery();
    }
    
    public boolean insertMensagem(int registro, String mensagem ) throws Exception {
    	String sql = "INSERT INTO Mensagens(registro, mensagem) VALUES (?,?);";
    	PreparedStatement pS = database.connection.prepareStatement(sql);
    	pS.setInt(1,registro);
    	pS.setString(2,mensagem);
    	
    	return (pS.executeUpdate() > 0);
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
}
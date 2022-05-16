//JOÃO MARCELLO BESSA RODRIGUES - 1720539
//RAFAEL RAMOS FELICIANO - 1521772

package DatabaseTables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Usuario {
	
	String email;
	String nome;
	String digest;
	String salt;
	byte[] certificate;
	int group = -1;
	
	int accessCount = 0;
	int failCount = 0;
	

	public Usuario () {
	}
	
	public static Usuario getInstance(String email ) {
		
		try {
				
			Database database = Database.getInstance();
	        ResultSet resultSet = database.getUsuario(email);
	        if( resultSet.next() == false ) {
		        System.out.println("Email inexistente no banco\n");
		        return null;
	        }
	        System.out.println("Finalizou Busca \n");

	        Usuario usuario = new Usuario();
	        usuario.setEmail(resultSet.getString("email"));
	        usuario.setDigest(resultSet.getString("digest"));
	        usuario.setSalt(resultSet.getString("salt"));
	        usuario.setCertificate(resultSet.getBytes("certificate"));
	        usuario.setGroup(resultSet.getInt("groupID"));
	        usuario.setNome(resultSet.getString("nome"));
	        
	       
	        database.connection.close();
	        
	        if( usuario.isUsuarioValid() ) {
	        	return usuario;
	        }
	        System.out.println("Algum campo desse usuario nao pode ser lido\n");
	        return null;
		} catch(Exception e) {
			System.out.print("Nao foi possivel criar Usuario" + e.toString());
		}
		return null;	        
	}
	
	public boolean isUsuarioValid() {
		if( this.email != null &&
			this.digest != null &&
			this.certificate != null &&
			this.salt != null &&
			this.group != -1 ) 
		{	
			return true;
		} else {
			return false;
		}
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public byte[] getCertificate() {
		return certificate;
	}

	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}
	
	public int getAccessCount() {
		int accessCount = 0;
		try {
			Database db = Database.getInstance();
			accessCount = db.getUsuario(email).getInt("accessCount");
			db.connection.close();
			return accessCount;
		} catch( Exception e ) {
			return accessCount;
		}
	}

	public int getFailCount() {
		return failCount;
	}
		
	public int incAccessCount()  {		
		try {		
			Database database = Database.getInstance();
			database.incrementUsuarioAcessCount(email);
	        database.connection.close();
			accessCount = accessCount + 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return accessCount;
	}
	
	public int incFailCount()  {		
		Database database;
		try {
			database = Database.getInstance();
			failCount = database.applyFailure(email);
	        database.connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return failCount;
	}
	
	public void failCountToZero()  {
		try {
			Database database = Database.getInstance();
			database.zeroFails(email);
			database.connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	        
	}
	
	public void isBlocked() {
		
	}
}

package DatabaseTables;

import java.sql.ResultSet;

public class Grupo {
	
	String nome;
	int groupId = -1;
	

	public Grupo () {
	}
	
	public static Grupo getInstance(int id ) {
		
		try {
				
			Database database = Database.getInstance();
	        ResultSet resultSet = database.getGroup(id);
	        if( resultSet.next() == false ) {
		        System.out.println("Id inexistente no banco\n");
		        return null;
	        }
	        System.out.println("Finalizou Busca \n");

	        Grupo grp = new Grupo();
	        grp.setNome(resultSet.getString("nome"));
	        grp.setGroupId(resultSet.getInt("idGrupo"));
	        
	        database.connection.close();
	        
	        if( grp.isGrupoValid() ) {
	        	return grp;
	        }
	        System.out.println("Algum campo desse grupo nao pode ser lido\n");
	        return null;
		} catch(Exception e) {
			System.out.print("Nao foi possivel criar grupo: " + e.toString());
		}
		return null;	        
	}
	
	public boolean isGrupoValid() {
		if( this.nome != null &&
			this.groupId != -1 ) 
		{	
			return true;
		} else {
			return false;
		}
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}


	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
}

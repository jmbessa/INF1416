package Autentication;

import DatabaseTables.Database;

public class NewUsuario {
	public static boolean createNewUsuario(String filePath, String nome, String senha, String email, String grupo) throws Exception {
		int grupoAsInt = 0;
		System.out.println("Grupo: " + grupo);
		if( grupo.equals("Usuario") ) {
			grupoAsInt = 1;
		}
		Database db = Database.getInstance();
		boolean ret = db.insertUsuario(email, senha, filePath, grupoAsInt, nome);
		db.connection.close();
		return ret;
	}
}

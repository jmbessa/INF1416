//JOÃO MARCELLO BESSA RODRIGUES - 1720539
//RAFAEL RAMOS FELICIANO - 1521772

package Autentication;

import DatabaseTables.Database;

public class NewUsuario {
	public static boolean createNewUsuario(String filePath, String nome, String senha, String email, String grupo) throws Exception {
		int grupoAsInt = 0;
		System.out.println("Grupo: " + grupo);
		if( grupo.equals("usuario") ) {
			grupoAsInt = 1;
		}
		Database db = Database.getInstance();
		boolean ret = db.insertUsuario(email, senha, filePath, grupoAsInt, nome);
		db.connection.close();
		return ret;
	}
}

//JOÃO MARCELLO BESSA RODRIGUES - 1720539
//RAFAEL RAMOS FELICIANO - 1521772

package LogView;

import java.sql.ResultSet;

import DatabaseTables.Database;

public class LogView {
	public static void getRegistros() {
		try {
			Database db = Database.getInstance();
			
			ResultSet todosRegistros = db.getTodosRegistros();
			
			while( todosRegistros.next() ) {
				String finalText  =  "";
				String dataRegistro = todosRegistros.getString("data");
				finalText = finalText + dataRegistro + " ";
				
				ResultSet mensagemReferente = db.getMensagem(todosRegistros.getInt("registro"));
				String mensagemText = mensagemReferente.getString("mensagem");
				
				finalText = finalText + "("+ todosRegistros.getInt("registro") + ") "+ mensagemText;
				
				if( finalText.contains("<login_name>") ) {
					String[] aux = finalText.split("<login_name>");
					finalText = aux[0] + " " + todosRegistros.getString("usuario") + " ";
					if( aux.length > 1 ) {
						finalText = finalText + aux[1];
					}
				}
				
				if( finalText.contains("<arq_name>") ) {
					String[] aux = finalText.split("<arq_name>");
					finalText = finalText + " " + aux[0] + " " + todosRegistros.getString("arquivo") + " " + aux[1];
				}
								
				System.out.println(finalText);
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

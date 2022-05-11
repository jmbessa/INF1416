package DatabaseTables;


public class Registro {
	
	int registro = -1;
	int dataTs = -1;
	String arquivo;
	String usuario;

	public Registro () {

	}
	
	public static Registro getInstance(int reg, int data, String arquivo, String usuario) {
		Registro r = new Registro();
		r.setRegistro(reg);
		r.setDataTs(data);
		r.setArquivo(arquivo);
		r.setUsuario(usuario);
		
		if( r.isRegistroValid() ) {
			return r;
		} else {
			System.out.println("Registro não contido na lista de mensagens\n");
			return null;
		}
	}
	
	public boolean insertRegistro() {
		try {
			
			Database database = Database.getInstance(); 
	        if( !database.insertRegistro(registro,dataTs,arquivo,usuario) ) {
		        System.out.println("Insercao de registro falhou ou registro inicializado com parametros errados\n");
		        database.connection.close();
		        return false;
	        }
	        database.connection.close();
	        return true;
		} catch(Exception e) {
			System.out.print("Nao foi possivel criar registro: " + e.toString());
		}
		return false;	        
	}
	
	public boolean isRegistroValid() {
		
		Mensagem m = Mensagem.getInstance();
		if( m.getRegistrosDict().containsKey(registro) ) {	
			return true;
		} else {
			return false;
		}
	}
	
	public int getRegistro() {
		return registro;
	}

	public void setRegistro(int registro) {
		this.registro = registro;
	}

	public int getDataTs() {
		return dataTs;
	}

	public void setDataTs(int dataTs) {
		this.dataTs = dataTs;
	}

	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
}

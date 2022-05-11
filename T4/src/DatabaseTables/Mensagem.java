package DatabaseTables;

import java.util.HashMap;
import java.util.Map;


public class Mensagem {
	
	Map<Integer,String> registrosDict =  new HashMap<Integer, String>();
	
	public Mensagem() {}
	
	public static Mensagem getInstance() {
		Mensagem m = new Mensagem();
		m.fillRegistrosDict();
		return m;
	}
	
	public Map<Integer, String> getRegistrosDict() {
		return registrosDict;
	}

	private void fillRegistrosDict() {
		registrosDict.put(1001, "Sistema iniciado.");
		registrosDict.put(1002, "Sistema encerrado.");
		registrosDict.put(2001, "Autentica��o etapa 1 iniciada.");
		registrosDict.put(2002, "Autentica��o etapa 1 encerrada.");
		registrosDict.put(2003, "Login name <login_name> identificado com acesso liberado.");
		registrosDict.put(2004, "Login name <login_name> identificado com acesso bloqueado.");
		registrosDict.put(2005, "Login name <login_name> n�o identificado.");
		registrosDict.put(3001, "Autentica��o etapa 2 iniciada para <login_name>.");
		registrosDict.put(3002, "Autentica��o etapa 2 encerrada para <login_name>.");
		registrosDict.put(3003, "Senha pessoal verificada positivamente para <login_name>.");
		registrosDict.put(3004, "Primeiro erro da senha pessoal contabilizado para <login_name>.");
		registrosDict.put(3005, "Segundo erro da senha pessoal contabilizado para <login_name>.");
		registrosDict.put(3006, "Terceiro erro da senha pessoal contabilizado para <login_name>.");
		registrosDict.put(3007, "Acesso do usuario <login_name> bloqueado pela autentica��o etapa 2.");
		registrosDict.put(4001, "Autentica��o etapa 3 iniciada para <login_name>.");
		registrosDict.put(4002, "Autentica��o etapa 3 encerrada para <login_name>.");
		registrosDict.put(4003, "Chave privada verificada positivamente para <login_name>.");
		registrosDict.put(4004, "Chave privada verificada negativamente para <login_name> (caminho inv�lido).");
		registrosDict.put(4005, "Chave privada verificada negativamente para <login_name> (frase secreta inv�lida).");
		registrosDict.put(4006, "Chave privada verificada negativamente para <login_name> (assinatura digital inv�lida).");
		registrosDict.put(4007, "Acesso do usuario <login_name> bloqueado pela autentica��o etapa 3.");
		registrosDict.put(5001, "Tela principal apresentada para <login_name>.");
		registrosDict.put(5002, "Op��o 1 do menu principal selecionada por <login_name>.");
		registrosDict.put(5003, "Op��o 2 do menu principal selecionada por <login_name>.");
		registrosDict.put(5004, "Op��o 3 do menu principal selecionada por <login_name>.");
		registrosDict.put(5005, "Op��o 4 do menu principal selecionada por <login_name>.");
		registrosDict.put(6001, "Tela de cadastro apresentada para <login_name>.");
		registrosDict.put(6002, "Bot�o cadastrar pressionado por <login_name>.");
		registrosDict.put(6003, "Senha pessoal inv�lida fornecida por <login_name>.");
		registrosDict.put(6004, "Caminho do certificado digital inv�lido fornecido por <login_name>.");
		registrosDict.put(6005, "Confirma��o de dados aceita por <login_name>.");
		registrosDict.put(6006, "Confirma��o de dados rejeitada por <login_name>.");
		registrosDict.put(6007, "Bot�o voltar de cadastro para o menu principal pressionado por <login_name>.");
		registrosDict.put(7001, "Tela de altera��o da senha pessoal e certificado apresentada para <login_name>.");
		registrosDict.put(7002, "Senha pessoal inv�lida fornecida por <login_name>.");
		registrosDict.put(7003, "Caminho do certificado digital inv�lido fornecido por <login_name>.");
		registrosDict.put(7004, "Confirma��o de dados aceita por <login_name>.");
		registrosDict.put(7005, "Confirma��o de dados rejeitada por <login_name>.");
		registrosDict.put(7006, "Bot�o voltar de carregamento para o menu principal pressionado por <login_name>.");
		registrosDict.put(8001, "Tela de consulta de arquivos secretos apresentada para <login_name>.");
		registrosDict.put(8002, "Bot�o voltar de consulta para o menu principal pressionado por <login_name>.");
		registrosDict.put(8003, "Bot�o Listar de consulta pressionado por <login_name>.");
		registrosDict.put(8004, "Caminho de pasta inv�lido fornecido por <login_name>.");
		registrosDict.put(8005, "Arquivo de �ndice decriptado com sucesso para <login_name>.");
		registrosDict.put(8006, "Arquivo de �ndice verificado (integridade e autenticidade) com sucesso para <login_name>.");
		registrosDict.put(8007, "Falha na decripta��o do arquivo de �ndice para <login_name>.");
		registrosDict.put(8008, "Falha na verifica��o (integridade e autenticidade) do arquivo de �ndice para <login_name>.");
		registrosDict.put(8009, "Lista de arquivos presentes no �ndice apresentada para <login_name>.");
		registrosDict.put(8010, "Arquivo <arq_name> selecionado por <login_name> para decripta��o.");
		registrosDict.put(8011, "Acesso permitido ao arquivo <arq_name> para <login_name>.");
		registrosDict.put(8012, "Acesso negado ao arquivo <arq_name> para <login_name>.");
		registrosDict.put(8013, "Arquivo <arq_name> decriptado com sucesso para <login_name>.");
		registrosDict.put(8014, "Arquivo <arq_name> verificado (integridade e autenticidade) com sucesso para <login_name>.");
		registrosDict.put(8015, "Falha na decripta��o do arquivo <arq_name> para <login_name>.");
		registrosDict.put(8016, "Falha na verifica��o (integridade e autenticidade) do arquivo <arq_name> para <login_name>.");
		registrosDict.put(9001, "Tela de sa�da apresentada para <login_name>.");
		registrosDict.put(9002, "Sa�da n�o liberada por falta de one-time password para <login_name>.");
		registrosDict.put(9003, "Bot�o sair pressionado por <login_name>.");
		registrosDict.put(9004, "Bot�o voltar de sair para o menu principal pressionado por <login_name>.");
	}
}

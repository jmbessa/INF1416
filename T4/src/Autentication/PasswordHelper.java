package Autentication;

public class PasswordHelper {
	private static int LETTERS_PER_FONEM = 2;
	public static void checkPassword( String senha , String confirmacaoSenha ) throws Exception {
		
		if( !senha.equals(confirmacaoSenha) ) {
			throw new Exception("Senha e Confirmação Senha precisam ser iguais");
		}
		
		if( senha.length() < 4*LETTERS_PER_FONEM ) {
			throw new Exception("Senha curta demais. Precisa ter entre 4 a 6 fonemas");
		}

		if( senha.length() > 6*LETTERS_PER_FONEM ) {
			throw new Exception("Senha grande demais. Precisa ter entre 4 a 6 fonemas");
		}
		
		for( int i = 0; i < senha.length()-2; i = i+2) {
			char first_fonema1 = senha.charAt(i);
			char end_fonema1  = senha.charAt(i+1);
			String fonema1 = new String("" + first_fonema1 + end_fonema1); 
			char first_fonema2 = senha.charAt(i+2);
			char end_fonema2  = senha.charAt(i+3);
			String fonema2 = new String("" + first_fonema2 + end_fonema2);
			
			if( fonema1.equals(fonema2) ) {
				throw new Exception("Senha não pode ter dois fonemas iguais seguidos");
			}
		}
		
	}
}

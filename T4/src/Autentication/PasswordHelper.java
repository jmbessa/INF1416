package Autentication;

public class PasswordHelper {
    private static int DIGITS = 1;
    public static void checkPassword( String senha , String confirmacaoSenha ) throws Exception {
        
        if( !senha.equals(confirmacaoSenha) ) {
            throw new Exception("Senha e Confirmação Senha precisam ser iguais");
        }
        
        if( senha.length() < 8*DIGITS ) {
            throw new Exception("Senha curta demais. Precisa ter entre 8 e 10 digitos");
        }

        if( senha.length() > 10*DIGITS ) {
            throw new Exception("Senha grande demais. Precisa ter entre 8 e 10 digitos");
        }
        
        for( int i = 0; i < senha.length()-2; i = i+2) {
            char first_digit = senha.charAt(i);
            char end_digit  = senha.charAt(i+1);
            String fonema1 = new String("" + first_digit + end_digit); 
            char first_digit2 = senha.charAt(i+2);
            char end_digit2  = senha.charAt(i+3);
            String fonema2 = new String("" + first_digit2 + end_digit2);
            
            if( fonema1.equals(fonema2) ) {
                throw new Exception("Senha não pode ter dois fonemas iguais seguidos");
            }
        }
        
    }
}
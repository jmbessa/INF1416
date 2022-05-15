package System;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import Autentication.Autenticator;

public class AuthSystem {
	static String senha;
	static List<String> sequenciasList = new ArrayList<String>();
	static Scanner sc = new Scanner(System.in);
	
	public static void firstAtenticator(Autenticator autenticator) throws Exception {
		
		Autenticator aut;
	
		aut = autenticator;
		aut.insertRegistro(1001, -1, null, false );
		
		System.out.println("Favor insira o email de login:");
		String usrEmail = sc.nextLine();
		
		if(aut.firstStepAutentication(usrEmail)  ) { // Checa se email existe
			if( !aut.checkIfCurrentUserIfBlocked() ) { // Checa se email não está bloqueado
				try {
					aut.zeroFailure();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SecondAutenticator(aut);
		
			} else {
				System.out.println("identificado com acesso bloqueado bloqueado");
				try {
					aut.insertRegistro(2004, -1, null, true); // Usuario com acesso bloqueado
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
		else {
			try {
				aut.insertRegistro(2005, -1, null, true);  // Usuario não existe
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("email Incorreto");
		}
		
	
	}
	
	public static void SecondAutenticator(Autenticator aut) {
		int usrPswd = 0;
		
		try {
			aut.insertRegistro(2002, -1, null, false);
			aut.insertRegistro(3001, -1, null, true);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(usrPswd != 6) {
		
			String [] buttonText = buttonText();  
			
			System.out.println("1 - " +buttonText[0]);
			System.out.println("2 - " +buttonText[1]);
			System.out.println("3 - " +buttonText[2]);
			System.out.println("4 - " +buttonText[3]);
			System.out.println("5 - " +buttonText[4]);
			System.out.println("6 - OK\n");
			
			System.out.println("Favor insira a opcao desejada:");
			usrPswd = sc.nextInt();
			
			
			if(usrPswd == 1) {
				sequenciasList.add(buttonText[0]);
			}
			if(usrPswd == 2){
				sequenciasList.add(buttonText[1]);
			}
			if(usrPswd == 3) {
				sequenciasList.add(buttonText[2]);
			}
			if(usrPswd == 4) {
				sequenciasList.add(buttonText[3]);
			}
			if(usrPswd == 5) {
				sequenciasList.add(buttonText[4]);
			}
		}
		if(usrPswd == 6) {
			 String[] sequencias = sequenciasList.toArray(new String[0]);

		        if( !aut.checkIfCurrentUserIfBlocked() ) {
					if(aut.secondStepAutentication(sequencias) ) {
		    			try {
							aut.zeroFailure();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
		    			ThirdAutenticator(aut);	
					}
						try {
							int failsCount = aut.applyFailure();
							if( failsCount == 3 ) {
								System.out.println("Senha incorreta 3 - email bloqueado\n");
								aut.insertRegistro(3006, -1, null, true);
								aut.insertRegistro(3007, -1, null, true);
								aut.insertRegistro(3002, -1, null, true);
								firstAtenticator(aut);
								
							} else if( failsCount == 2 ) {
								System.out.println("Senha incorreta 2\n");
								aut.insertRegistro(3005, -1, null, true);
							} else if( failsCount == 1 ) {
								System.out.println("Senha incorreta 1\n");
								aut.insertRegistro(3004, -1, null, true);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
		        	else {
						System.out.println("Várias tentativas falhas- Tente novamente em alguns minutos");
					}
		}
	}
	
	private static void ThirdAutenticator(Autenticator aut) {
		
		try {
			aut.insertRegistro(4001, -1, null, true); // Inicio 3 etapa de verificacao	
		} catch( Exception e ) {
			e.printStackTrace();
		}
	    		
	    	    if( !aut.checkIfCurrentUserIfBlocked()) {
	    	    	
	    	    	sc = new Scanner(System.in);
	    			System.out.println("Favor entre com a chave privada:\n");
	    			String usrprivK= sc.nextLine();
	    			
	    			sc = new Scanner(System.in);
	    			System.out.println("Favor entre com a frase secreta:\n");
	    			String usrsecretF = sc.nextLine();
	    			
	    			Path p = Paths.get(usrprivK);
	    			
	    	    	try  {
	    	    		aut.thirdStepAutentication(usrsecretF,p);
						try {
							aut.zeroFailure();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// CHAMA O MENU
						mainMenu(aut);
	    			} catch( Exception e ) {
						try {
							int failsCount = aut.applyFailure();
							int remaningTries =  (3 - failsCount);
							String dialogText = e.getMessage();
							if( remaningTries > 0) {
								dialogText = dialogText + ". Mais " + remaningTries + " tentativas.";
							}
							
							System.out.printf("%s", dialogText);
							if( failsCount == 3 ) {
								aut.insertRegistro(4007, -1, null, true);			
								firstAtenticator(aut);
							}
						} catch (Exception e2) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	    			}
	    	    } else {
					System.out.println("Email Bloqueado - Tente Novamente Mais Tarde");
	    		}   	   
	    	}
	
	
	
	private static void mainMenu (Autenticator aut) {
		System.out.println("NAO FEZ!!!!!!");
		// TODO Auto-generated method stub
		
	}

	private static String[] buttonText() {
		
		String seq;
		String result[] = new String[5];
		
		ArrayList<String> list = new ArrayList<String>();
	    list.add("0");
	    list.add("1");
	    list.add("2");
	    list.add("3");
	    list.add("4");
	    list.add("5");	    
	    list.add("6");
	    list.add("7");
	    list.add("8");	    
	    list.add("9");    
	    Collections.shuffle(list);
		int j = 0;
		for(int i=0;i<list.size();i=i+2){
			seq = list.get(i) + "-" + list.get(i+1);
			result[j] = seq;
			j++;
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		
		Autenticator aut = new Autenticator();
		try {
			firstAtenticator(aut);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sc.close();
	}

}



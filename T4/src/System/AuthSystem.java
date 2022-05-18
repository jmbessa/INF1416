//JOÃO MARCELLO BESSA RODRIGUES - 1720539
//RAFAEL RAMOS FELICIANO - 1521772

package System;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.sqlite.SQLiteException;

import Autentication.*;
import DatabaseTables.Database;
import DatabaseTables.Grupo;
import DatabaseTables.Usuario;
import WrittingFileTools.DocxHelper;


public class AuthSystem {
	static String senha;
	static List<String> sequenciasList = new ArrayList<String>();
	static Scanner sc = new Scanner(System.in);
	static Autenticator aut;
	protected static int lastUserscount;
	public void Cabecalho() {
		
	}
	
	public static void firstAutenticator(Autenticator autenticator) throws Exception {
		
		aut = autenticator;
		
		Scanner sc = new Scanner(System.in);
		System.out.println("\nFavor insira o email de login:");
		String usrEmail = sc.nextLine();
		
		while(aut.firstStepAutentication(usrEmail) == false || aut.checkIfCurrentUserIfBlocked()) {
			System.out.println("\nFavor insira o email de login:");
			usrEmail = sc.nextLine();
		}
		
		try {
			aut.zeroFailure();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public static int SecondAutenticator(Autenticator aut) {
		int usrPswd = 0;
		int failsCount = 1;
		
		try {
			aut.insertRegistro(2002, -1, null, false);
			aut.insertRegistro(3001, -1, null, true);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(failsCount != 0) {
			failsCount = 0;
			while(usrPswd != 6) {
			
				String [] buttonText = buttonText();  
				System.out.println("\nSenha\n");
				System.out.println("1. " +buttonText[0]);
				System.out.println("2. " +buttonText[1]);
				System.out.println("3. " +buttonText[2]);
				System.out.println("4. " +buttonText[3]);
				System.out.println("5. " +buttonText[4]);
				System.out.println("6. OK\n");
				
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
				if(usrPswd > 6 || usrPswd < 0) {
					System.out.println("Valor invalido!\n");
				}
			}
		
			String[] sequencias = sequenciasList.toArray(new String[0]);
	
			if(aut.secondStepAutentication(sequencias) ) {
				try {
					aut.zeroFailure();
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}							
			}
			else {
				try {
					failsCount = aut.applyFailure();
					sequenciasList.clear();
					if( failsCount == 3 ) {
						System.out.println("Senha incorreta 3 - email bloqueado\n");
						aut.insertRegistro(3006, -1, null, true);
						aut.insertRegistro(3007, -1, null, true);
						aut.insertRegistro(3002, -1, null, true);
						return failsCount;
						
					} else if( failsCount == 2 ) {
						System.out.println("Senha Incorreta - Mais uma tentativa\n");
						aut.insertRegistro(3005, -1, null, true);
					} else if( failsCount == 1 ) {
						System.out.println("Senha Incorreta - Mais duas tentativas\n");
						aut.insertRegistro(3004, -1, null, true);
					}
					usrPswd = 0;
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return failsCount;
	}
	
	private static int ThirdAutenticator(Autenticator aut) {
		int ret;
		int failsCount = 1;
		
		try {
			aut.insertRegistro(4001, -1, null, true); // Inicio 3 etapa de verificacao	
		} catch( Exception e ) {
			e.printStackTrace();
		}
	    		
	    if( !aut.checkIfCurrentUserIfBlocked()) {
	    	
	    	while (failsCount != 0) {
		    	sc = new Scanner(System.in);
				System.out.println("\nFavor entre com a chave privada:");
				String usrprivK= sc.nextLine();
				
				sc = new Scanner(System.in);
				System.out.println("\nFavor entre com a frase secreta:\n");
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
					ret = 0;
					return ret;
					// CHAMA O MENU
					
				} catch( Exception e ) {
					try {
						failsCount = aut.applyFailure();
						int remaningTries =  (3 - failsCount);
						String dialogText = e.getMessage();
						if( remaningTries > 0) {
							dialogText = dialogText + ". Mais " + remaningTries + " tentativas.";
						}
						
						System.out.printf("%s", dialogText);
						if( failsCount == 3 ) {
							aut.insertRegistro(4007, -1, null, true);
							System.out.println("Email bloqueado\n");
							firstAutenticator(aut);
						}
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	    	}
	    }
	    ret = 3;
	    return ret;
	}
	
	
	
private static void mainMenu (Autenticator aut) throws Exception {
		
		Usuario user = aut.getCurrentUserVerification();
		Grupo grupoInstance = Grupo.getInstance(user.getGroup());
		
		try {
			aut.insertRegistro(5001, -1, null, true);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// CHAMA CABECALHO
		cabecalho(aut);
		
		//INICIO MENU
		System.out.println("Menu Principal:");
		
		System.out.println("1 - Cadastrar um novo usuario");
		System.out.println("2 - Alterar senha pessoal e certificado digital do usuario");
		System.out.println("3 - Consultar pasta de arquivos secretos do usuario");
		System.out.println("4 - Sair do sistema");
		
		sc = new Scanner(System.in);
		System.out.println("Favor insira a opcao desejada:");
		int menuOption  = sc.nextInt();
		
		if(menuOption == 1 ) {
			if(!(grupoInstance.getNome().equals("Admin"))) {
				System.out.println("Usuario nao tem permicao para cadastrar!");
				mainMenu(aut);
			}
			
			try {
				aut.insertRegistro(5002, -1, null, true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			cadastroUser (aut);
		}
		if(menuOption == 2){
			try {
				aut.insertRegistro(5003, -1, null, true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			alteraSenhaCert(aut);
			
		}
		if(menuOption == 3) {
			try {
				aut.insertRegistro(5004, -1, null, true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			consultaArq(aut, user);
			
		}
		if(menuOption == 4) {
			try {
				aut.insertRegistro(5005, -1, null, true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sairPrograma(aut);
			
		}
	}
	
	private static void cabecalho (Autenticator aut) {
		
		Usuario user = aut.getCurrentUserVerification();
		Grupo grupoInstance = Grupo.getInstance(user.getGroup());
		String AcessCount = new String();
		AcessCount = String.valueOf(user.getAccessCount());
		
		// INICIO CABECALHO
		System.out.println("\n\n**************************************");
		System.out.println("Login Name: " +user.getEmail());
				
		System.out.println("Grupo do usuario: " +grupoInstance.getNome());
				
		System.out.println("Nome do usuario: " +user.getNome());
				
		System.out.println("Total de acessos do usuario: " +AcessCount);
		System.out.println("**************************************\n");
		// FIM CABECALHO
	}
	
	public static void Confirmacao(Autenticator aut, String grupo, String senha, String confirmacaoSenha, String caminhoCert) throws Exception {
		
		if( grupo == null ) {
			throw new Exception("Selecione um grupo");
		}
		
		CertificateHelper certiHelper = new CertificateHelper(caminhoCert);
		PasswordHelper.checkPassword(senha,confirmacaoSenha);
		
		System.out.println("\nGrupo: " +grupo);
		System.out.println("Senha: " +senha);
		System.out.println("Caminho do certificado digital: " +caminhoCert);
		System.out.println("Versao: " +certiHelper.getCertificateVersion());
		System.out.println("Serie: " + certiHelper.getCertificateSerialNumber());
		System.out.println("Validade: " +certiHelper.getCertificateValidate().toString());
		System.out.println("Tipo de Assinatura: " +certiHelper.getCertificateSignatureType());
		System.out.println("Emissor: " +certiHelper.getCertificateEmissor());
		System.out.println("Sujeito: " +certiHelper.getCertificateUserName());
		System.out.println("Email: " +certiHelper.getCertificateEmail());
		
		sc = new Scanner(System.in);
		System.out.println("0 - Confirmar / 1 - Voltar");
		int tmp = sc.nextInt();
		
		if (tmp == 0) {
			try {
	    		aut.insertRegistro(6005, -1, null, true);		
	    	} catch( Exception e ) {
	    		e.printStackTrace();
	    	}
			try {
				NewUsuario.createNewUsuario(caminhoCert, certiHelper.getCertificateUserName(), confirmacaoSenha, certiHelper.getCertificateEmail(), grupo);
				
	    	}  catch (SQLiteException e) {
	    		System.out.println("Email presente no certificado ja esta cadastrado.");
			}  catch( Exception e ) {
	    		System.out.println("Nao foi possivel cadastrar." + e.toString());
	    	} 
		}
		if (tmp == 1) {
			try {
	    		aut.insertRegistro(6006, -1, null, true);		
	    	} catch( Exception e ) {
	    		e.printStackTrace();
	    	}
		}
		mainMenu(aut);
	}
	
	private static void cadastroUser (Autenticator aut) throws Exception {
		
		try {
			aut.insertRegistro(6001, -1, null, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// CHAMA CABECALHO
		cabecalho(aut);
		
		// INICIO CABECALHO 0P1
		String UsersCount = new String();
		System.out.println("Contando todos os usuarios...");
		UsersCount = String.valueOf(aut.getAllUsersCount());
		System.out.println("Total de usuarios do sistema: " +UsersCount);
		// FIM CABECALHO 0P1
		
		if(lastUserscount != aut.getAllUsersCount()) {
			System.out.println("\n------- Formulario de Cadastro -------");
			
			
			sc = new Scanner(System.in);
			System.out.println("Caminho do arquivo do certificado digital:");
			String caminhoCertificado = sc.nextLine();
			
			String [] lista = new String[] {"Admin","Usuario"};
			List<Grupo> grupos = aut.getAllGrupos();
			int i =0;
			String [] list = new String [grupos.size()];
			for (Grupo grupo : grupos) {
				list[i] = grupo.getNome();
				i++;
			}
			sc = new Scanner(System.in);
			System.out.println("Lista de opcoes: 0 - " + lista[0] + "/ 1 - " + lista[1]);
			System.out.println("Selecione uma opcao: ");
			int newG = sc.nextInt();
			
			String newGroup = lista[newG];
			
			int check = 0;

			sc = new Scanner(System.in);
			System.out.println("Entre com a Senha pessoal: (Entre 8 a 10 numeros)");
			String newpswd = sc.nextLine();
		
			sc = new Scanner(System.in);
			System.out.println("Confirmacao Senha pessoal: ");
			String newpswdC = sc.nextLine();
			
			PasswordHelper.checkPassword(newpswd, newpswdC);
			
			System.out.println("1 - Cadastrar");
			System.out.println("2 - Voltar para o Menu");
			sc = new Scanner(System.in);
			System.out.println("Selecione uma opção:");
			int opt = sc.nextInt();
			
			if(opt == 1) {
				try {
					aut.insertRegistro(6002, -1, null, true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					lastUserscount = aut.getAllUsersCount();
					Confirmacao(aut, newGroup, newpswd, newpswdC, caminhoCertificado);
					
					
				} catch (Exception e) {
					if(e.getMessage().equals("Usuario criado")) {
						
					}
					try {
						if( e.getMessage().contains("Senha") ) {
							aut.insertRegistro(6003, -1, null, true);
						} else if( e.getMessage().contains("Certificado") ) {
							aut.insertRegistro(6004, -1, null, true);
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
					
			if(opt == 2) {
				try {
		    		aut.insertRegistro(6007, -1, null, true);		
		    	} catch( Exception e ) {
		    		e.printStackTrace();
		    	}
				mainMenu(aut);
			}	
		}
	}
	
	private static void sairPrograma(Autenticator aut) throws Exception {
		try {
			aut.insertRegistro(9001, -1, null, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// CHAMA CABECALHO
		cabecalho(aut);
		
		//INICIO SAIDA
		System.out.println("Saida do Sistema");
		
		sc = new Scanner(System.in);
		System.out.println("Deseja mesmo sair? (0 - Sim / 1 - Nao)");
		int sair = sc.nextInt();
		
		if(sair == 0) {
			try {
				aut.insertRegistro(1002, -1, null, false);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.exit(0);
		}
		if (sair == 1) {
			try {
				aut.insertRegistro(9004, -1, null, true);
			} catch (Exception e1) {
			// TODO Auto-generated catch block
				e1.printStackTrace();
		    }
			mainMenu(aut);
		}
	}
	

	
	private static void alteraSenhaCert (Autenticator aut) throws Exception {
		
		try {
			aut.insertRegistro(7001, -1, null, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// CHAMA CABECALHO
		cabecalho(aut);
		
		sc = new Scanner(System.in);
		System.out.println(" Deseja alterar o Caminho do arquivo do certificado digital?: ( 0-SIM - 1-NAO)");
		int alt = sc.nextInt();
		if(alt == 0) {
			sc = new Scanner(System.in);
			System.out.println("Caminho do arquivo do certificado digital:");
			String caminhoArq = sc.nextLine();
			
			try {
				CertificateHelper certiHelper = new CertificateHelper(caminhoArq);
				aut.updateCurrentUserCert(certiHelper);
				
				try {
					aut.insertRegistro(7004, -1, null, true);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	
			} catch( Exception e ) {
				try {
					aut.insertRegistro(7003, -1, null, true);
					aut.insertRegistro(7005, -1, null, true);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		sc = new Scanner(System.in);
		System.out.println(" Deseja alterar a senha pessoal?: ( 0 - SIM / 1 - NAO)");
		int alt2 = sc.nextInt();
		if(alt2 == 0) {
			
			try {
				sc = new Scanner(System.in);
				System.out.println("Entre com a Senha pessoal: (Entre 8 a 10 numeros)");
				String newpswd = sc.nextLine();
				
				sc = new Scanner(System.in);
				System.out.println("Confirmacao Senha pessoal:");
				String newpswdC = sc.nextLine();
					
				PasswordHelper.checkPassword(newpswd,newpswdC);
				aut.updateCurrentUserSenha(newpswd);
				
				try {
					aut.insertRegistro(7004, -1, null, true);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch( Exception e ) {
				try {
					aut.insertRegistro(7002, -1, null, true);
					aut.insertRegistro(7005, -1, null, true);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		try {
			aut.insertRegistro(7006, -1, null, true);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mainMenu(aut);
		
	}
	
	private static void consultaArq (Autenticator aut, Usuario user) {
		
		
		String[] TabelaNomeArqSecretos = new String[] {"NOME__ARQUIVO", "NOME_SECRETO","DONO_ARQUIVO","GRUPO_ARQUIVO"};
		
		
		String storedPath = "";
		
		try {
			aut.insertRegistro(8001, -1, null, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// CHAMA CABECALHO
		cabecalho(aut);
		
		try {
			aut.insertRegistro(8003, -1, null, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sc = new Scanner(System.in);
		System.out.println("Caminho do arquivo do certificado digital:");
		String camArq = sc.nextLine();
		
		String [] listedPath = camArq.split("/");
		
		for( int i = 0; i < listedPath.length; i++ ) {
			storedPath = storedPath + listedPath[i] + "/";
		}
		String currentPath = storedPath + "index";
		
		System.out.println(currentPath);
		byte[] listFiles = null;
		try {
			listFiles = aut.listSecretFiles(currentPath);
		} catch( Exception e ) {
			System.out.println("erro");
		}
		
		if( listFiles != null ) {
			
			try {
		    	  aut.insertRegistro(8010, -1, TabelaNomeArqSecretos[1], true);
		      } catch (Exception e1) {
				// TODO Auto-generated catch block
		    	  e1.printStackTrace();
		      }
		      
		      int group = 0;
		      if( TabelaNomeArqSecretos[3].toString().equals("usuario")  ) {
		    	  group = 1;
		      }

		      System.out.println(storedPath + TabelaNomeArqSecretos[1].toString());
		      if(	  user.getEmail().equals(TabelaNomeArqSecretos[2].toString()) || 
		    		  user.getGroup() ==  group ) {
		    	  
		    	  try {
			    	  aut.insertRegistro(8011, -1, storedPath + TabelaNomeArqSecretos[1].toString(), true);
			      } catch (Exception e1) {
					// TODO Auto-generated catch block
			    	  e1.printStackTrace();
			      }
		    	  
		    	  try {
		    		  byte[] listFiles1 = aut.listSecretFiles(storedPath + TabelaNomeArqSecretos[0].toString()); 
		    		  DocxHelper.writeFile(storedPath + TabelaNomeArqSecretos[1].toString(), listFiles1);
		    	  } catch( Exception e1 ) {
		    	  }
		    	  
		      }  else {
		    	  try {
			    	  aut.insertRegistro(8012, -1, storedPath + TabelaNomeArqSecretos[1].toString(), true);
			      } catch (Exception e1) {
					// TODO Auto-generated catch block
			    	  e1.printStackTrace();
			      }
		    	  System.out.println("Usuario nao possui acesso"); 
		      }
			
			try {
				aut.insertRegistro(8009, -1, null, true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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
	
	public static void main(String[] args) throws Exception {
		
		Autenticator aut = new Autenticator();

        try {
        	Database db = Database.getInstance();
            if (db.getUsuarioCount() == 0)
                db.insertUsuario("admin@inf1416.puc-rio.br", "12345678", "./Pacote-T4/Keys/admin-x509.crt", 0, "Admin");
            db.connection.close();
        	
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        aut.insertRegistro(1001, -1, null, false );
        int ret = 3;
		try {
			while(ret == 3) {
				firstAutenticator(aut);
				ret = SecondAutenticator(aut);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ThirdAutenticator(aut);
		mainMenu(aut);
		sc.close();
	}

}



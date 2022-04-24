//JOÃO MARCELLO BESSA - 1720539
//RAFAEL RAMOS FELICIANO - 1521772

import java.security.KeyPair;
import java.security.KeyPairGenerator;

class MySignatureTest {

    public static void main(String[] args) throws Exception {

        if(args.length < 2) {
            System.err.println("Erro no recebimento de parametros");
            System.exit(1);
        }
        System.out.println("\n------------------------------");
        System.out.println("\nTexto plano: " + args[0]);

        String text = args[0];
        byte[] planText = text.getBytes("UTF8");

        System.out.print("\nTexto plano (hexadecimal): ");
        for(int i = 0; i < planText.length; i++)
            System.out.print(String.format("%02X", planText[i]));

        System.out.println("\n\n------------------------------");
        System.out.println("\nGerando chave RSA");

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair key = keyGen.generateKeyPair();

        System.out.println("Geracao de chave RSA concluida");

        System.out.println("\n------------------------------");

        System.out.println("\nGerando assinatura...");
        MySignature mySignature = MySignature.getInstance(args[1]);
        mySignature.initSign(key.getPrivate());
        mySignature.update(planText);

        System.out.println("\n\nGeracao de assinatura concluida");

        System.out.println("\n------------------------------");

        byte[] signature = mySignature.sign();
        System.out.print("\n\nAssinatura (hexadecimal): ");
        for(int i = 0; i != signature.length; i++)
            System.out.print(String.format("%02x", signature[i]));

        MySignature.initVerify(key.getPublic());
        mySignature.update(planText);

        System.out.println("\n\n------------------------------");

        if(mySignature.verify(signature))
            System.out.println("\nAssinatura valida");
        else
            System.err.println("\nAssinatura invalida");

        System.out.println("\n------------------------------");

    }

}
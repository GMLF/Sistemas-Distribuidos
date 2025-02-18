/** 

Guilherme Moreira (2207192) e Pedro Parra (2207249)

*/

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ClienteRMI {
    
	public void iniciarCliente(){
		
		List<PeerLista> listaPeers = new ArrayList<>();
        for(PeerLista peer : PeerLista.values()) {
            listaPeers.add(peer);
        }
		
        try {
            Registry registro = LocateRegistry.getRegistry("127.0.0.1", 1099);

            // Escolhe um peer aleatório para conectar
            SecureRandom sr = new SecureRandom();
            IMensagem stub = null;
            PeerLista peer = null;
            boolean conectou = false;

            while(!conectou){
                peer = listaPeers.get(sr.nextInt(listaPeers.size()));
                try {    
                    stub = (IMensagem) registro.lookup(peer.getNome());
                    conectou = true;
                } catch(java.rmi.ConnectException | java.rmi.NotBoundException e) {
                    System.out.println(peer.getNome() + " indisponível. Tentando o próximo...");
                }
            }

            System.out.println("Conectado ao peer: " + peer.getNome());            

            Scanner leitura = new Scanner(System.in);
            String opcao = "";
            do {
                System.out.println("1) Read");
                System.out.println("2) Write");
                System.out.println("x) Exit");
                System.out.print(">> ");
                opcao = leitura.next();
                
                switch(opcao){
                    case "1": {
                        Mensagem mensagem = new Mensagem("", "read");
                        Mensagem resposta = stub.enviar(mensagem);
                        System.out.println(resposta.getMensagem());
                        break;
                    }
                    case "2": {
                        System.out.print("Add fortune: ");
                        String fortune = leitura.next();
                        Mensagem mensagem = new Mensagem(fortune, "write");
                        Mensagem resposta = stub.enviar(mensagem);
                        System.out.println(resposta.getMensagem());
                        break;
                    }
                    case "x": {
                        System.out.println("Encerrando conexão...");
                        try {
                            stub.desconectarPeer(peer.getNome());
                        } catch (RemoteException e) {
                            System.err.println("Erro ao desconectar o peer.");
                            e.printStackTrace();
                        }
                        System.exit(0);
                        break;
                    }
                    default:
                        System.out.println("Opção inválida!");
                }
            } while(!opcao.equals("x"));
            
        } catch(Exception e) {
            e.printStackTrace();
        }

	}

    public static void main(String[] args) {
        new ClienteRMI().iniciarCliente();
    }
    
}

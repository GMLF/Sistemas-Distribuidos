/** 

Guilherme Moreira (2207192) e Pedro Parra (2207249)

*/

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Peer implements IMensagem{
    
	ArrayList<PeerLista> alocados;
	
    public Peer() {
          alocados = new ArrayList<>();
    }
    
    //Cliente: invoca o metodo remoto 'enviar'
    //Servidor: invoca o metodo local 'enviar'
    @Override
    public Mensagem enviar(Mensagem mensagem) throws RemoteException {
        Mensagem resposta;
        try {
        	System.out.println("Mensagem recebida: " + mensagem.getMensagem());
			resposta = new Mensagem(parserJSON(mensagem.getMensagem()));
		} catch (Exception e) {
			e.printStackTrace();
			resposta = new Mensagem("{\n" + "\"result\": false\n" + "}");
		}
        return resposta;
    }    
    
    public String parserJSON(String json) {
		String result = "false";

		String fortune = "-1";		
		
		String[] v = json.split(":");
		System.out.println(">>>" + v[1]);
		String[] v1 = v[1].split("\"");
		System.out.println(">>>" + v1[1]);
		if (v1[1].equals("write")) {
			String [] p = json.split("\\["); 
			 System.out.println(p[1]); 
			 String [] p1 = p[1].split("]"); 
			 System.out.println(p1[0]); 
			 String [] p2 = p1[0].split("\""); 
			 System.out.println(p2[1]); 
			 fortune = p2[1];
			 
			// Write in file
			Principal pv2 = new Principal();
			pv2.write(fortune);
		} else if (v1[1].equals("read")) {
			// Read file
			Principal pv2 = new Principal();
			fortune = pv2.read();
		}

		result = "{\n" + "\"result\": \"" + fortune + "\"" + "}";
		System.out.println(result);

		return result;
	}
    
    public void iniciar(){
		int option=0;
    try {
    		List<PeerLista> listaPeers = new ArrayList<>();
    		for( PeerLista peer : PeerLista.values())
    			listaPeers.add(peer);
    		
    		Registry servidorRegistro;
    		try {
    			servidorRegistro = LocateRegistry.createRegistry(1099);
    		} catch (java.rmi.server.ExportException e){ //Registro jah iniciado 
    			System.out.print("Registro jah iniciado. Usar o ativo.\n");
    		}
    		servidorRegistro = LocateRegistry.getRegistry(); //Registro eh unico para todos os peers
    		String [] listaAlocados = servidorRegistro.list();
    		
			for(int i=0; i<listaAlocados.length;i++)
    			System.out.println(listaAlocados[i]+" ativo.");
    		Scanner entrada = new Scanner(System.in);
    		
    		System.out.println("Escolha um peer de 1 a 4:");

		

    		while(option < 1 || option > 5) {
    			option = entrada.nextInt() - 1;
    		}
    		
    		PeerLista peer = listaPeers.get(option);
    		
            IMensagem skeleton  = (IMensagem) UnicastRemoteObject.exportObject(this, 0);             
            	
            servidorRegistro.rebind(peer.getNome(), skeleton);
            System.out.print(peer.getNome() +" Servidor RMI: Aguardando conexoes...");
                        
            //---Cliente RMI
            new ClienteRMI().iniciarCliente();
            
            
            
        } catch(Exception e) {
            e.printStackTrace();
        }       

    }

	// método de desconexão (unbind)
	public void desconectar(String identificador) {
		try {
			Registry registro = LocateRegistry.getRegistry();

			String[] nomesRegistrados = registro.list();
			if (nomesRegistrados.length > 0) {
				boolean encontrado = Arrays.asList(nomesRegistrados).contains(identificador);

				if (encontrado) {
					try {
						registro.unbind(identificador);
						UnicastRemoteObject.unexportObject(this, true);
						System.out.println(identificador + " foi desconectado com sucesso.");
					} catch (Exception e) {
						System.err.println("Erro ao tentar desconectar " + identificador + ": " + e.getMessage());
					}
				} else {
					System.out.println("Nenhuma associação encontrada para o identificador: " + identificador);
				}
			} else {
				System.out.println("Nenhum serviço está atualmente ativo no registro.");
			}
		} catch (Exception e) {
			System.err.println("Falha ao acessar o registro RMI: " + e.getMessage());
		}
	}

	@Override
	public void desconectarPeer(String identificador) throws RemoteException {
		desconectar(identificador);
	}

    
    public static void main(String[] args) {
        Peer servidor = new Peer();
        servidor.iniciar();
    }    
}
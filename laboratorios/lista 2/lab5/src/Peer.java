/** 

Guilherme Moreira (2207192) e Pedro Parra (2207249)

*/

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Peer implements IMensagem {

	private ArrayList<PeerLista> alocados;

    public Peer() {
        alocados = new ArrayList<>();
    }

    // Cliente: invoca o método remoto 'enviar'
    // Servidor: invoca o método local 'enviar'
    @Override
    public Mensagem enviar(Mensagem mensagem) throws RemoteException {
        Mensagem resposta;
        try {
            System.out.println("Mensagem recebida: " + mensagem.getMensagem());
            resposta = new Mensagem(parserJSON(mensagem.getMensagem()));
        } catch (Exception e) {
            e.printStackTrace();
            resposta = new Mensagem("{\n\"result\": false\n}");
        }
        return resposta;
    }

    public String parserJSON(String json) {
        String result = "false";
        String fortune = "-1";

        try {
            String[] v = json.split(":");
            String[] v1 = v[1].split("\"");

            if (v1[1].equals("write")) {
                String[] p = json.split("\\[");
                String[] p1 = p[1].split("]");
                String[] p2 = p1[0].split("\"");
                fortune = p2[1];

                Principal pv2 = new Principal();
                pv2.write(fortune);
            } else if (v1[1].equals("read")) {
                Principal pv2 = new Principal();
                fortune = pv2.read();
            }

            result = "{\n\"result\": \"" + fortune + "\"\n}";
        } catch (Exception e) {
            System.err.println("Erro ao analisar JSON: " + e.getMessage());
        }

        System.out.println(result);
        return result;
    }

    public void iniciar() {
        try {
            List<PeerLista> listaPeers = Arrays.asList(PeerLista.values());
            Registry servidorRegistro;

            try {
                servidorRegistro = LocateRegistry.createRegistry(1099);
            } catch (java.rmi.server.ExportException e) {
                System.out.println("Registro já iniciado. Usando o ativo.");
                servidorRegistro = LocateRegistry.getRegistry();
            }

            String[] listaAlocados = servidorRegistro.list();
            for (String nome : listaAlocados) {
                System.out.println(nome + " ativo.");
            }

            Scanner entrada = new Scanner(System.in);
            int option = -1;

            while (option < 1 || option > 4) {
                System.out.println("Escolha um peer de 1 a 4:");
                option = entrada.nextInt();
            }
            PeerLista peer = listaPeers.get(option - 1);

            IMensagem skeleton = (IMensagem) UnicastRemoteObject.exportObject(this, 0);
            servidorRegistro.rebind(peer.getNome(), skeleton);
            System.out.println(peer.getNome() + " Servidor RMI: Aguardando conexões...");

            new ClienteRMI().iniciarCliente();
        } catch (Exception e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void desconectar(String identificador) {
        try {
            Registry registro = LocateRegistry.getRegistry();
            String[] nomesRegistrados = registro.list();
            if (Arrays.asList(nomesRegistrados).contains(identificador)) {
                registro.unbind(identificador);
                UnicastRemoteObject.unexportObject(this, true);
                System.out.println(identificador + " foi desconectado com sucesso.");
            } else {
                System.out.println("Nenhuma associação encontrada para o identificador: " + identificador);
            }
        } catch (Exception e) {
            System.err.println("Erro ao desconectar " + identificador + ": " + e.getMessage());
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

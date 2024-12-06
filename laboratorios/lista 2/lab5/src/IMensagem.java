/** 

Guilherme Moreira (2207192) e Pedro Parra (2207249)

*/

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IMensagem extends Remote {
    public Mensagem enviar(Mensagem mensagem) throws RemoteException;
    void desconectarPeer(String nome) throws RemoteException;
}

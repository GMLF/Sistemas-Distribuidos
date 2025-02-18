import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HOR extends Remote {
    public String getHOR() throws RemoteException;

    public void setHOR(String valor) throws RemoteException;

}

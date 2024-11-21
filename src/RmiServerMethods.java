import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiServerMethods extends UnicastRemoteObject implements RmiMethodsInterface {
    protected RmiServerMethods() throws RemoteException {
        super();
    }

    @Override
    public void getOutgoingTransferRequests() throws RemoteException {

    }

    @Override
    public void getIncomingTransferRequests() throws RemoteException {

    }

    @Override
    public void getCurrentUserInfo() throws RemoteException {

    }

    @Override
    public void getCurrentExchangeRates() throws RemoteException {

    }

    @Override
    public void getOnlineUsers() throws RemoteException {

    }

    @Override
    public void sendTransferRequest() throws RemoteException {

    }

    @Override
    public void sendTransferRequestResponse() throws RemoteException {

    }

    @Override
    public void sendNewAccountToServer() throws RemoteException {

    }
}

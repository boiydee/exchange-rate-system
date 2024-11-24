//package client;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import server.Server;
//
//import java.rmi.RemoteException;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class ClientTest {
//    RmiServerMethods rmiServerMethods;
//    Server testServer;
//
//    @BeforeEach
//    public void instantiateServer() throws RemoteException {
//        testServer = new Server();
//        rmiServerMethods = new RmiServerMethods(testServer);
//    }
//
//    @Test
//    public void canInstantiate() {
//        Client underTest = new Client(rmiServerMethods);
//    }
//
//    @Test
//    void userCanLoginIfAccountExists() throws RemoteException {
//        Client underTest = new Client(rmiServerMethods);
//        assertTrue(underTest.login("test", "test2"));
//    }
//
//    @Test
//    void userCantLoginIfAccountDoesntExists() throws RemoteException {
//        Client underTest = new Client(rmiServerMethods);
//        assertFalse(underTest.login("nonexistantaccount", "test2"));
//    }
//
//    @Test
//    void duplicateUsernamesCantOccur() throws RemoteException {
//        Client underTest = new Client(rmiServerMethods);
//        assertFalse(underTest.setupNewAccount("test", "test2"));
//    }
//
//    @Test
//    @Disabled
//    void userCanCreateAccount() throws RemoteException {
//        // Must Delete the account from the account file to run
//        Client underTest = new Client(rmiServerMethods);
//        assertTrue(underTest.setupNewAccount("deleteBeforeTestRun", "test2"));
//    }
//}

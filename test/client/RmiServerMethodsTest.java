package client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

import java.rmi.RemoteException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RmiServerMethodsTest {

    Server testServer;
    @BeforeEach
    public void instantiateServer(){
        testServer = new Server();
    }

    @Test
    public void canCallServerLoginMethod() throws RemoteException {
        RmiServerMethods underTest = new RmiServerMethods(testServer);
        ArrayList<String> expected = new ArrayList<>();
        assertEquals(testServer.getOnlineUsers(), expected);
        underTest.login("test", "test2");
        expected.add("test");
        assertEquals(testServer.getOnlineUsers(), expected);
    }

    @Test
    public void canGetUserInfo() throws RemoteException {
        RmiServerMethods underTest = new RmiServerMethods(testServer);
        String expected = String.format("Username: %s\nGBP: £%.2f\nUSD: $%.2f\nEUR: €%.2f\nJPY: ¥%.2f\n", "test", 0.00, 0.00, 0.00, 0.00);
        underTest.login("test", "test2");
        assertEquals(underTest.getCurrentUserInfo("test"), expected);
    }

    @Test
    public void canLogoutUser() throws RemoteException {
        RmiServerMethods underTest = new RmiServerMethods(testServer);
        ArrayList<String> expected = new ArrayList<>();
        assertEquals(testServer.getOnlineUsers(), expected);
        underTest.login("test", "test2");
        expected.add("test");
        assertEquals(testServer.getOnlineUsers(), expected);
        expected.remove(0);
        underTest.logout("test");
        assertEquals(testServer.getOnlineUsers(), expected);
    }

    @Test
    public void canCallServerExchangeRateServiceWithoutCrashing() throws RemoteException {
        RmiServerMethods underTest = new RmiServerMethods(testServer);
        underTest.getCurrentExchangeRates("JPY");
    }
}

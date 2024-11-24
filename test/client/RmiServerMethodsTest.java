package client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ServerLogic;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RmiServerMethodsTest {
    private ServerLogic testServerLogic;
    private RmiServerMethods underTest;

    @BeforeEach
    public void setup() throws RemoteException {
        testServerLogic = new ServerLogic();
        underTest = new RmiServerMethods(testServerLogic);
    }

    @Test
    public void canCallServerLoginMethod() throws RemoteException {
        List<String> expected = new ArrayList<>();
        assertEquals(expected, underTest.getOnlineUsers(), "Online users list should initially be empty");

        underTest.addOnlineUser("test");
        expected.add("test");
        assertEquals(expected, underTest.getOnlineUsers(), "User should be added to the online users list");
    }

    @Test
    public void canGetUserInfo() throws RemoteException {
        String username = "test";
        String password = "testPass";
        try {
            testServerLogic.verifyAccount(username, password);
        } catch (Exception e) {
            fail("Exception during account creation: " + e.getMessage());
        }

        List<String> userInfo = underTest.getCurrentUserInfo(username);
        String expectedInfo = String.format(
                "User: %s, GBP: 0.0, USD: 0.0, EUR: 0.0, YEN: 0.0",
                username
        );
        assertTrue(userInfo.get(0).contains(expectedInfo), "User info should match expected format and values");
    }

    @Test
    public void canLogoutUser() throws RemoteException {
        List<String> expected = new ArrayList<>();
        assertEquals(expected, underTest.getOnlineUsers(), "Online users list should initially be empty");

        underTest.addOnlineUser("test");
        expected.add("test");
        assertEquals(expected, underTest.getOnlineUsers(), "User should be added to the online users list");

        underTest.removeOnlineUser("test");
        expected.remove("test");
        assertEquals(expected, underTest.getOnlineUsers(), "User should be removed from the online users list");
    }

    @Test
    public void canCallServerExchangeRateServiceWithoutCrashing() throws RemoteException {
        Map<String, Double> rates = underTest.getCurrentExchangeRates();
        assertNotNull(rates, "Exchange rates should not be null");
        assertTrue(rates.containsKey("USD-USD"), "Exchange rates should include USD");
        assertTrue(rates.containsKey("GBP-USD"), "Exchange rates should include GBP");
    }
}

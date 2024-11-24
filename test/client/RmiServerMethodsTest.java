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
        String expectedInfo = String.format("Username: %s\nGBP: £%.2f\nUSD: $%.2f\nEUR: €%.2f\nJPY: ¥%.2f\n", "test", 0.0, 0.0, 0.0, 0.0);
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

        assertTrue(rates.containsKey("USD -> USD"), "Exchange rates should include USD -> USD");
        assertTrue(rates.containsKey("USD -> GBP"), "Exchange rates should include USD -> GBP");
        assertTrue(rates.containsKey("USD -> JPY"), "Exchange rates should include USD -> JPY");
        assertTrue(rates.containsKey("USD -> EUR"), "Exchange rates should include USD -> EUR");

        assertTrue(rates.containsKey("GBP -> USD"), "Exchange rates should include GBP -> USD");
        assertTrue(rates.containsKey("GBP -> GBP"), "Exchange rates should include GBP -> GBP");
        assertTrue(rates.containsKey("GBP -> JPY"), "Exchange rates should include GBP -> JPY");
        assertTrue(rates.containsKey("GBP -> EUR"), "Exchange rates should include GBP -> EUR");

        assertTrue(rates.containsKey("JPY -> USD"), "Exchange rates should include JPY -> USD");
        assertTrue(rates.containsKey("JPY -> GBP"), "Exchange rates should include JPY -> GBP");
        assertTrue(rates.containsKey("JPY -> JPY"), "Exchange rates should include JPY -> JPY");
        assertTrue(rates.containsKey("JPY -> EUR"), "Exchange rates should include JPY -> EUR");

        assertTrue(rates.containsKey("JPY -> USD"), "Exchange rates should include EUR -> USD");
        assertTrue(rates.containsKey("JPY -> GBP"), "Exchange rates should include EUR -> GBP");
        assertTrue(rates.containsKey("JPY -> JPY"), "Exchange rates should include EUR -> JPY");
        assertTrue(rates.containsKey("JPY -> EUR"), "Exchange rates should include EUR -> EUR");
    }
}

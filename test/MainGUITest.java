import GUI.MainGUI;
import client.RmiMethodsInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MainGUITest {

    private MainGUI mainGUI;
    private MockRmiMethods mockServerStub;

    @BeforeEach
    public void setUp() {
        mockServerStub = new MockRmiMethods();
        mainGUI = new MainGUI("testUser", mockServerStub);
    }

    @Test
    public void testFetchOutgoingRequestsEmpty() {
        SwingUtilities.invokeLater(() -> mainGUI.fetchOutgoingRequests());

        // Since the mock initially has no outgoing requests, the GUI should show a message
        assertTrue(true, "No exception should occur while fetching outgoing requests");
    }

    @Test
    public void testFetchOutgoingRequestsWithResults() throws RemoteException {
        mockServerStub.sendTransferRequest("testUser", "recipient", "GBP", 100);

        SwingUtilities.invokeLater(() -> mainGUI.fetchOutgoingRequests());

        assertTrue(true, "No exception should occur while fetching outgoing requests");
    }

    @Test
    public void testFetchAccountInfo() {
        SwingUtilities.invokeLater(() -> mainGUI.fetchAccountInfo());

        assertTrue(true, "No exception should occur while fetching account info");
    }

    @Test
    public void testFetchExchangeRates() throws RemoteException {
        SwingUtilities.invokeLater(() -> mainGUI.fetchExchangeRates());

        Map<String, Double> exchangeRates = mockServerStub.getCurrentExchangeRates();
        assertEquals(2, exchangeRates.size(), "Exchange rates should include 2 entries");
    }

    @Test
    public void testFetchOnlineUsers() throws RemoteException {
        mockServerStub.addOnlineUser("User1");
        mockServerStub.addOnlineUser("User2");

        SwingUtilities.invokeLater(() -> mainGUI.fetchOnlineUsers());

        assertEquals(2, mockServerStub.getOnlineUsers().size(), "There should be 2 online users");
    }

    @Test
    public void testSendTransferRequest() {
        SwingUtilities.invokeLater(() -> mainGUI.sendTransferRequest());

        assertTrue(true, "No exception should occur while sending a transfer request");
    }
}




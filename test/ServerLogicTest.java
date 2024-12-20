import attributes.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import server.ServerLogic;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class ServerLogicTest {
    private ServerLogic serverLogic;

    @BeforeEach
    void setUp() {
        serverLogic = new ServerLogic();
    }

    @Test @Disabled
    void testCreateAccount() {
        String username = "testUser";
        String password = "testPass";

        boolean created = serverLogic.createAccount(username, password);
        assertTrue(created, "Account should be created successfully");

        boolean duplicateCreation = serverLogic.createAccount(username, password);
        assertFalse(duplicateCreation, "Duplicate account creation should not be allowed");
    }

    @Test
    void testVerifyAccount() throws IOException {
        String username = "verifyUser";
        String password = "verifyPass";

        boolean verified = serverLogic.verifyAccount(username, password);
        assertTrue(verified, "New account should be created and verified successfully");

        boolean incorrectPassword = serverLogic.verifyAccount(username, "wrongPass");
        assertFalse(incorrectPassword, "Verification should fail for incorrect password");

        boolean existingAccount = serverLogic.verifyAccount(username, password);
        assertTrue(existingAccount, "Existing account should be verified successfully");
    }

    @Test
    void testGetOutgoingRequests() {

        List<String> sender1OutgoingRequests = serverLogic.getOutgoingRequests("sender1");
        List<String> sender2OutgoingRequests = serverLogic.getOutgoingRequests("sender2");
        assertEquals(1, sender1OutgoingRequests.size(), "There should be 1 outgoing request for sender 1");
        assertEquals(1, sender2OutgoingRequests.size(), "There should be 1 outgoing request for sender 2");
        assertTrue(sender1OutgoingRequests.get(0).contains("USD"), "First request should involve USD");
        assertTrue(sender2OutgoingRequests.get(0).contains("GBP"), "First request should involve USD");
    }

    @Test
    void testGetIncomingRequests() {
        List<String> incomingRequests = serverLogic.getIncomingRequests("recipient1");
        assertEquals(1, incomingRequests.size(), "There should be one incoming request for recipient1");
        assertTrue(incomingRequests.get(0).contains("USD"), "Request should involve USD");
    }

    @Test
    void testUpdateExchangeRates() {
        serverLogic.updateExchangeRates();
        Map<String, Double> rates = serverLogic.getExchangeRates();

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

    @Test
    void testTransferWithinAccount() throws IOException {
        String username = "testUser";
        String password = "testPass";

        serverLogic.createAccount(username, password);
        serverLogic.updateAccountBalance(username, "USD", 100.0);

        boolean success = serverLogic.transferWithinAccount(username, "USD", "GBP", 50);
        assertTrue(success, "Transfer within account should succeed");

        List<String> userInfo = serverLogic.getAllUserInfo(username);
        assertNotNull(userInfo, "User info should not be null");
        assertTrue(userInfo.get(0).contains("GBP"), "Account should now include GBP balance");
    }



    @Test
    void testConcurrency() throws InterruptedException, IOException {
        String username = "concurrentUser";
        String password = "concurrentPass";

        serverLogic.createAccount(username, password);
        serverLogic.updateAccountBalance(username, "USD", 100.0);

        Runnable task = () -> {
            try {
                serverLogic.transferWithinAccount(username, "USD", "GBP", 10);
            } catch (Exception e) {
                fail("Concurrency task failed");
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        List<String> userInfo = serverLogic.getAllUserInfo(username);
        assertTrue(userInfo.get(0).contains("GBP"), "User should have GBP balance after concurrent transfers");
    }

    @Timeout(120)
    @Disabled //Disabled due to how long it takes to run not because it doesnt work
    @Test
    void largeScaleConcurrencyTest(){
        // This does not hit a deadlock.
        // It takes roughly 100 seconds to run 2000 iterations as each iteration also locks and saves the accounts
        int x = 2000;

        CountDownLatch cdl = new CountDownLatch(3);

        new Thread(() -> {
            for (int i = 0; i < x; i++){
                serverLogic.transferCurrency("concurentUser1","concurentUser2", "GBP", 1);
            }
            cdl.countDown();
        }).start();

        new Thread(() -> {
            for (int i = 0; i < x; i++){
                serverLogic.transferCurrency("concurentUser2","concurentUser3", "GBP", 1);
            }
            cdl.countDown();
        }).start();

        new Thread(() -> {
            for (int i = 0; i < x; i++){
                serverLogic.transferCurrency("concurentUser3","concurentUser1", "GBP", 1);
            }
            cdl.countDown();
        }).start();

        Map<String, Account> accounts = serverLogic.getAccounts();
        try {
            cdl.await();
            assertEquals(20000, accounts.get("concurentUser1").getGbp_balance());
            assertEquals(20000, accounts.get("concurentUser2").getGbp_balance());
            assertEquals(20000, accounts.get("concurentUser3").getGbp_balance());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

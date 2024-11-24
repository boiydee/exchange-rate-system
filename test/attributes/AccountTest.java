package attributes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    Account testAcc = new Account("user", "123");

    // Tests increment of funds to USD balance
    @Test
    public void testIncrementUsdBalance() {
        testAcc.addToUsdBalance(5.0f);
        assertEquals(5.0f, testAcc.getUsd_balance(), "USD balance should increment correctly");
    }

    // Tests decrement of funds from Yen balance
    @Test
    public void testDecrementYenBalance() {
        testAcc.setYen_balance(8.0f); // Initialize with 8
        testAcc.decreaseYenBalance(8.0f);
        assertEquals(0.0f, testAcc.getYen_balance(), "YEN balance should decrement correctly");
    }

    // Tests that GBP balance should not fall below 0
    @Test
    public void testDecrementBalanceNotFallUnder0() {
        testAcc.setGbp_balance(15.0f); // Initialize with 15
        testAcc.decreaseGbpBalance(16.0f);
        assertEquals(15.0f, testAcc.getGbp_balance(), "GBP balance should not decrease when funds are insufficient");
    }

    // Tests increment of funds to GBP balance
    @Test
    public void testIncrementGbpBalance() {
        testAcc.addToGbpBalance(10.0f);
        assertEquals(10.0f, testAcc.getGbp_balance(), "GBP balance should increment correctly");
    }

    // Tests that balances for all currencies are initialized to 0
    @Test
    public void testBalancesInitializedToZero() {
        assertEquals(0.0f, testAcc.getGbp_balance(), "GBP balance should initialize to 0");
        assertEquals(0.0f, testAcc.getUsd_balance(), "USD balance should initialize to 0");
        assertEquals(0.0f, testAcc.getEuro_balance(), "EUR balance should initialize to 0");
        assertEquals(0.0f, testAcc.getYen_balance(), "YEN balance should initialize to 0");
    }

    // Tests the toString method
    @Test
    public void testToString() {
        testAcc.setGbp_balance(15.0f);
        testAcc.setUsd_balance(10.0f);
        testAcc.setEuro_balance(5.0f);
        testAcc.setYen_balance(20.0f);

        String expected = "attributes.Account{" +
                "username='user', " +
                "gbp_balance=15.0, " +
                "usd_balance=10.0, " +
                "euro_balance=5.0, " +
                "yen_balance=20.0" +
                "}";
        assertEquals(expected, testAcc.toString(), "toString method should return the correct representation of the account");
    }
}

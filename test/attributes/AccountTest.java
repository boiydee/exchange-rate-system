package attributes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    Account testAcc = new Account("user", "123", 15.0, 14.0, 7.0, 8.0);

    // Tests increment of funds to balance
    @Test
    public void testIncrementUsdBalance() {
        testAcc.addToUsdBalance(5.0);
        assertEquals(19.0, testAcc.getUsdBalance());
    }

    // Tests decrement of funds from balance
    @Test
    public void testDecrementYenBalance() {
        testAcc.decreaseYenBalance(8);
        assertEquals(0.0, testAcc.getYenBalance());
    }

    // Will check that amount shouldn't decrease as amount is greater than the actual number of funds
    @Test
    public void testDecrementBalanceNotFallUnder0(){
        testAcc.decreaseGbpBalance(16.0);
        assertEquals(15.0, testAcc.getGbpBalance());
    }

}

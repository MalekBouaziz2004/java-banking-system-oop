package bank;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    Payment p1;
    Payment p2;

    @BeforeEach
    void init() {
        p1 = new Payment("01.01.2020", 1000, "Deposit", 0.02, 0.05);
        p2 = new Payment("02.01.2020", -200, "Withdraw", 0.02, 0.05);
    }

    @AfterEach
    void cleanup() {
        // nichts zu löschen hier
    }

    @Test
    void testConstructor() {
        assertEquals("01.01.2020", p1.getDate());
        assertEquals(1000, p1.getAmount());
        assertEquals("Deposit", p1.getDescription());
        assertEquals(0.02, p1.getIncomingInterest());
        assertEquals(0.05, p1.getOutgoingInterest());
    }

    @Test
    void testCopyConstructor() {
        Payment copy = new Payment(p1);

        assertEquals(p1, copy);
        assertNotSame(p1, copy);
    }

    @Test
    void testCalculateIncoming() {
        double expected = 1000 * (1 - 0.02);
        assertEquals(expected, p1.calculate());
    }

    @Test
    void testCalculateOutgoing() {
        double expected = -200 * (1 + 0.05);
        assertEquals(expected, p2.calculate());
    }

    @Test
    void testEquals() {
        Payment a = new Payment("01.01.2020", 1000, "Deposit", 0.02, 0.05);
        assertEquals(p1, a);

    }

    @Test
    void testToString() {
        String text = p1.toString();
        assertTrue(text.contains("01.01.2020"));
        assertTrue(text.contains("Deposit"));
    }

    @ParameterizedTest
    @ValueSource(doubles = {10, 50, 100, 250})
    void testCalculateParameterized(double value) {
        Payment p = new Payment("01.01.2020", value, "test", 0.02, 0.05);
        assertTrue(p.calculate() > 0);
    }
}


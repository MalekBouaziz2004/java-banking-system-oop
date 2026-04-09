package bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferTest {

    Transfer t1;       // normale Überweisung
    IncomingTransfer in;
    OutgoingTransfer out;

    @BeforeEach
    void init() {
        t1 = new Transfer("01.01.2021", 200, "Test Transfer", "A", "B");
        in = new IncomingTransfer("02.01.2021", 300, "Incoming", "C", "D");
        out = new OutgoingTransfer("03.01.2021", 100, "Outgoing", "D", "C");
    }

    //  Konstruktor
    @Test
    void testConstructor() {
        assertEquals("01.01.2021", t1.getDate());
        assertEquals(200, t1.getAmount());
        assertEquals("Test Transfer", t1.getDescription());
        assertEquals("A", t1.getSender());
        assertEquals("B", t1.getRecipient());
    }

    //  Copy-Konstruktor
    @Test
    void testCopyConstructor() {
        Transfer copy = new Transfer(t1);
        assertEquals(t1, copy);
        assertNotSame(t1, copy);
    }

    // calculate() — Transfer ändert Betrag NICHT
    @Test
    void testCalculate() {
        assertEquals(200, t1.calculate()); // keine Zinsen, keine Gebühren
    }

    //  IncomingTransfer: Betrag positiv
    @Test
    void testIncomingTransfer() {
        assertEquals(300, in.calculate());
    }

    //  OutgoingTransfer: Betrag negativ
    @Test
    void testOutgoingTransfer() {
        assertEquals(-100, out.calculate());
    }

    //  equals()
    @Test
    void testEquals() {
        Transfer t2 = new Transfer("01.01.2021", 200, "Test Transfer", "A", "B");
        assertEquals(t1, t2);
    }

    //  toString()
    @Test
    void testToString() {
        String s = t1.toString();
        assertTrue(s.contains("Sender"));
        assertTrue(s.contains("Recipient"));
        assertTrue(s.contains("01.01.2021"));
    }
}

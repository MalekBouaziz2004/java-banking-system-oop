package bank;

import bank.exceptions.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrivateBankTest {

    PrivateBank bank;
    Payment p1, p2;
    IncomingTransfer in;
    OutgoingTransfer out;
    final String DIR = "data_test";

    @BeforeEach
    void init() throws Exception {
        bank = new PrivateBank("MyBank", 0.02, 0.03, DIR);

        p1 = new Payment("01.01.2020", 1000, "Pay1", 0.02, 0.03);
        p2 = new Payment("02.01.2020", -500, "Pay2", 0.02, 0.03);

        in = new IncomingTransfer("03.01.2020", 300, "Inc", "A", "B");
        out = new OutgoingTransfer("04.01.2020", 200, "Out", "B", "A");
    }

    @AfterEach
    void cleanup() throws Exception {
        File dir = new File(DIR);
        if (dir.exists()) {
            for (File f : dir.listFiles()) {
                f.delete();
            }
            dir.delete();
        }
    }

    // ===================== Konto erstellen =====================

    @Test
    void testCreateAccount() {
        assertDoesNotThrow(() -> bank.createAccount("A"));
        assertEquals(0, bank.getTransactions("A").size());
    }

    @Test
    void testCreateAccountAlreadyExists() throws Exception {
        bank.createAccount("A");
        assertThrows(AccountAlreadyExistsException.class, () -> bank.createAccount("A"));
    }

    // ===================== addTransaction =====================

    @Test
    void testAddTransaction() throws Exception {
        bank.createAccount("A");
        assertDoesNotThrow(() -> bank.addTransaction("A", p1));
        assertTrue(bank.containsTransaction("A", p1));
    }

    @Test
    void testAddTransactionNoAccount() {
        assertThrows(AccountDoesNotExistException.class,
                () -> bank.addTransaction("X", p1));
    }

    @Test
    void testAddTransactionDuplicate() throws Exception {
        bank.createAccount("A");
        bank.addTransaction("A", p1);

        assertThrows(TransactionAlreadyExistException.class,
                () -> bank.addTransaction("A", p1));
    }

    // ===================== removeTransaction =====================

    @Test
    void testRemoveTransaction() throws Exception {
        bank.createAccount("A");
        bank.addTransaction("A", p1);

        assertDoesNotThrow(() -> bank.removeTransaction("A", p1));
    }

    @Test
    void testRemoveTransactionDoesNotExist() throws Exception {
        bank.createAccount("A");

        assertThrows(TransactionDoesNotExistException.class,
                () -> bank.removeTransaction("A", p2));
    }

    // ===================== containsTransaction =====================

    @Test
    void testContainsTransaction() throws Exception {
        bank.createAccount("A");
        bank.addTransaction("A", p1);

        assertTrue(bank.containsTransaction("A", p1));
    }

    // ===================== getAccountBalance =====================

    @Test
    void testAccountBalance() throws Exception {
        bank.createAccount("A");
        bank.addTransaction("A", p1);
        bank.addTransaction("A", p2);

        double expected = p1.calculate() + p2.calculate();
        assertEquals(expected, bank.getAccountBalance("A"));
    }

    // ===================== getTransactions (Kopie!) =====================

    @Test
    void testGetTransactionsReturnsCopy() throws Exception {
        bank.createAccount("A");
        bank.addTransaction("A", p1);

        List<Transaction> list = bank.getTransactions("A");
        list.clear();

        assertEquals(1, bank.getTransactions("A").size());
    }

    // ===================== getTransactionsSorted =====================

    @Test
    void testGetTransactionsSorted() throws Exception {
        bank.createAccount("A");
        bank.addTransaction("A", p1);
        bank.addTransaction("A", p2);

        List<Transaction> sorted = bank.getTransactionsSorted("A", true);

        assertEquals(p2, sorted.get(0));
        assertEquals(p1, sorted.get(1));
    }

    // ===================== getTransactionsByType =====================

    @Test
    void testGetTransactionsByType() throws Exception {
        bank.createAccount("A");
        bank.addTransaction("A", p1);
        bank.addTransaction("A", p2);

        List<Transaction> pos = bank.getTransactionsByType("A", true);
        assertEquals(1, pos.size());
        assertTrue(pos.contains(p1));
    }

    // ===================== equals =====================

    @Test
    void testEquals() throws Exception {
        PrivateBank b1 = new PrivateBank("MyBank", 0.02, 0.03, DIR);
        PrivateBank b2 = new PrivateBank("MyBank", 0.02, 0.03, DIR);

        assertEquals(b1, b2);
    }

    // ===================== toString =====================

    @Test
    void testToStringContainsName() {
        assertTrue(bank.toString().contains("MyBank"));
    }

    // ===================== Persistierung (WRITE + READ) =====================

    @Test
    void testPersistence() throws Exception {

        bank.createAccount("A");
        bank.addTransaction("A", p1);

        // Neue Bank → liest JSON
        PrivateBank bank2 = new PrivateBank("MyBank", 0.02, 0.03, DIR);

        assertTrue(bank2.containsTransaction("A", p1));
    }
}

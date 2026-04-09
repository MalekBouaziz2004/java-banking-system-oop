package bank;

import bank.exceptions.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;

/**
 * PrivateBank implementing {@link Bank}.
 * <p>
 * Attributes:
 * <ul>
 *   <li>name – bank name</li>
 *   <li>incomingInterest – interest (0..1) for deposits, like in {@code Payment}</li>
 *   <li>outgoingInterest – interest/fee (0..1) for withdrawals, like in {@code Payment}</li>
 *   <li>accountsToTransactions – maps account names to their transactions</li>
 * </ul>
 * The map is created at field declaration (not in the constructor).
 */
public class PrivateBank implements Bank {

    private String name;
    private double incomingInterest;
    private double outgoingInterest;
    private String directoryName;
    private Gson gson;

    public PrivateBank(String name, double incomingInterest, double outgoingInterest, String directoryName)
            throws IOException {

        this.name = name;
        this.incomingInterest = incomingInterest;
        this.outgoingInterest = outgoingInterest;
        this.directoryName = directoryName;

        // Gson wird hier initialisiert mit dem TransactionAdapter
        this.gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Transaction.class, new TransactionAdapter())
                .setPrettyPrinting()
                .create();

        // danach werden die gespeicherten Konten eingelesen
        readAccounts();
    }


    @Override
    public List<String> getAllAccounts() {
        List<String> accounts = new ArrayList<>(accountsToTransactions.keySet());
        Collections.sort(accounts); // alphabetisch sortieren
        return accounts;
    }

    @Override
    public void deleteAccount(String account)
            throws AccountDoesNotExistException, IOException {

        // 1) Prüfen, ob Konto existiert
        if (!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Account does not exist: " + account);
        }

        // 2) Aus der Map entfernen
        accountsToTransactions.remove(account);

        // 3) JSON-Datei löschen, falls directoryName genutzt wird
        if (directoryName != null) {
            Path path = Path.of(directoryName, account + ".json");
            if (Files.exists(path)) {
                Files.delete(path);
            }
        }
    }


    private void writeAccount(String account) throws IOException {
        if (directoryName == null) return;
        Path path = Path.of(directoryName, account + ".json");
        List<Transaction> list = accountsToTransactions.get(account);

        //sicher, keine Nebenwirkungen Gson dort hinein-modifizieren
        List<Transaction> copy = new ArrayList<>(list);

        String json = gson.toJson(copy, new TypeToken<List<Transaction>>(){}.getType());//Java zur Laufzeit Informationen über Klassen und Typen herausfinden lassen

        Files.createDirectories(path.getParent());
        Files.writeString(path, json);
    }


    /**
     * Liest alle gespeicherten JSON-Dateien ein und baut
     * die Konten und deren Transaktionen wieder auf.
     * Wir verwenden "throws IOException", um keine try/catch-Blöcke zu brauchen.
     */
    private void readAccounts() throws IOException {
        if (directoryName == null) return;

        Path dir = Path.of(directoryName);
        if (!Files.exists(dir)) return;

        for (Path file : (Iterable<Path>) Files.list(dir)::iterator) {

            if (!file.getFileName().toString().endsWith(".json"))
                continue;

            String accountName = file.getFileName().toString().replace(".json", "");

            try {

                String json = Files.readString(file);

                List<Transaction> list = gson.fromJson(
                        json,
                        new TypeToken<List<Transaction>>(){}.getType()
                );

                if (list == null) {
                    System.err.println("WARNUNG: leere oder ungültige Datei: " + file);
                    list = new ArrayList<>();
                }

                accountsToTransactions.put(accountName, list);

            } catch (Exception ex) {
                System.err.println("FEHLER beim Laden von " + file + ": " + ex.getMessage());
                System.err.println("→ Datei wird übersprungen, Programm läuft weiter.");

                // Account trotzdem anlegen, aber leer
                accountsToTransactions.put(accountName, new ArrayList<>());
            }
        }
    }




    /** Key: account name; Value: list of its transactions. */
    private  Map<String, List<Transaction>> accountsToTransactions = new HashMap<>();




    /**
     * Constructs a bank with the first three attributes.
     *
     * @param name bank name
     * @param incomingInterest interest (0..1) for deposits
     * @param outgoingInterest interest/fee (0..1) for withdrawals
     */
    public PrivateBank(String name, double incomingInterest, double outgoingInterest) {
        this.name = name;
        this.incomingInterest = incomingInterest;
        this.outgoingInterest = outgoingInterest;
        this.directoryName = null;
    }

    /**
     * Copy constructor. Copies only the first three attributes.
     *
     * @param other other bank
     */
    public PrivateBank(PrivateBank other) throws IOException {
        this(other.name, other.incomingInterest, other.outgoingInterest, other.directoryName);
    }

    // ---------------- Getters / Setters (first three attributes) ----------------

    /** @return bank name */
    public String getName() { return name; }
    /** @param name sets bank name */
    public void setName(String name) { this.name = name; }

    /** @return incoming interest (0..1) */
    public double getIncomingInterest() { return incomingInterest; }

    /** @param incomingInterest sets incoming interest (0..1) */
    public void setIncomingInterest(double incomingInterest) {
        if (incomingInterest > 0.0 && incomingInterest <= 1.0) {
            this.incomingInterest = incomingInterest;
        } else {
            System.out.println("Invalid incoming interest");
        }
    }

    /** @return outgoing interest (0..1) */
    public double getOutgoingInterest() { return outgoingInterest; }

    /** @param outgoingInterest sets outgoing interest (0..1) */
    public void setOutgoingInterest(double outgoingInterest) {
        if (outgoingInterest > 0.0 && outgoingInterest <= 1.0) {
            this.outgoingInterest = outgoingInterest;
        } else {
            System.out.println("Invalid outgoing interest");
        }
    }

    // ---------------- Object overrides ----------------

    @Override
    public String toString() {
        return "PrivateBank{" +
                "name='" + name + '\'' +
                ", incomingInterest=" + incomingInterest +
                ", outgoingInterest=" + outgoingInterest +
                ", directoryName='" + directoryName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;                              // same reference
        if (obj == null || getClass() != obj.getClass()) return false; // null or different class
        PrivateBank other = (PrivateBank) obj;
        return Double.compare(this.incomingInterest, other.incomingInterest) == 0
                && Double.compare(this.outgoingInterest, other.outgoingInterest) == 0
                && java.util.Objects.equals(this.name, other.name)
                && java.util.Objects.equals(this.accountsToTransactions, other.accountsToTransactions)
                 &&Objects.equals(this.directoryName, other.directoryName);
    }



    // ---------------- Bank methods ----------------

    /** {@inheritDoc} */
    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException, IOException  {
        if (accountsToTransactions.containsKey(account)) {
            throw new AccountAlreadyExistsException("Account already exists: " + account);
        }
        accountsToTransactions.put(account, new ArrayList<>());
        writeAccount(account);

    }

    /** {@inheritDoc} */
    @Override
    //Method Overloading (Überladen von Methoden)
    public void createAccount(String account, List<Transaction> transactions)
            throws AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException, IOException {

        if (accountsToTransactions.containsKey(account)) {
            throw new AccountAlreadyExistsException("Account already exists: " + account);
        }

        List<Transaction> list = new ArrayList<>();

        for (Transaction t : transactions) {

            // --- validation (inlined instead of validateAttributesOrThrow) ---
            if (t instanceof Transfer) {
                double a = ((Transfer) t).getAmount();
                if (a <= 0) {
                    throw new TransactionAttributeException("Transfer amount must be > 0");
                }
            } else if (t instanceof Payment) {
                if (incomingInterest < 0 || incomingInterest > 1
                        || outgoingInterest < 0 || outgoingInterest > 1) {
                    throw new TransactionAttributeException("Payment interest must be in [0,1]");
                }
            }

            // --- duplicate check within the initial list ---
            if (list.contains(t)) {
                throw new TransactionAlreadyExistException("Duplicate in initial list: " + t);
            }

            // --- overwrite interests for Payment with bank values ---
            if (t instanceof Payment) {
                Payment p = (Payment) t;
                p.setIncomingInterest(incomingInterest);
                p.setOutgoingInterest(outgoingInterest);
            }

            list.add(t);
        }

        accountsToTransactions.put(account, list);
        writeAccount(account);
    }

    /** {@inheritDoc} */
    @Override
    public void addTransaction(String account, Transaction transaction)
            throws TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {

        List<Transaction> list = accountsToTransactions.get(account);
        if (list == null) {
            throw new AccountDoesNotExistException("Unknown account: " + account);
        }

        // --- validation (inlined) ---
        if (transaction instanceof Transfer) {
            double a = ((Transfer) transaction).getAmount();
            if (a <= 0) {
                throw new TransactionAttributeException("Transfer amount must be > 0");
            }
        } else if (transaction instanceof Payment) {
            if (incomingInterest < 0 || incomingInterest > 1
                    || outgoingInterest < 0 || outgoingInterest > 1) {
                throw new TransactionAttributeException("Payment interest must be in [0,1]");
            }
        }

        // --- duplicate check ---
        if (list.contains(transaction)) {
            throw new TransactionAlreadyExistException("Transaction already exists: " + transaction);
        }

        // --- overwrite interests for Payment with bank values ---
        if (transaction instanceof Payment) {
            Payment p = (Payment) transaction;
            p.setIncomingInterest(incomingInterest);
            p.setOutgoingInterest(outgoingInterest);
        }

        list.add(transaction);
        writeAccount(account);
    }


    /** {@inheritDoc} */
    @Override
    public void removeTransaction(String account, Transaction transaction)
            throws AccountDoesNotExistException, TransactionDoesNotExistException, IOException {
        List<Transaction> list = accountsToTransactions.get(account);
        if (list == null) throw new AccountDoesNotExistException("Unknown account: " + account);
        if (!list.remove(transaction)) {
            throw new TransactionDoesNotExistException("Transaction not found: " + transaction);
        }
        writeAccount(account);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsTransaction(String account, Transaction transaction) {
        List<Transaction> list = accountsToTransactions.get(account);
        return list != null && list.contains(transaction);
    }

    /**
     * {@inheritDoc}
     * <p>Variante&nbsp;1: We can sum {@code calculate()} directly because
     * {@link IncomingTransfer} returns +amount and {@link OutgoingTransfer} returns −amount.</p>
     */
    @Override
    public double getAccountBalance(String account) {
        List<Transaction> list = accountsToTransactions.get(account);
        if (list == null) return 0.0;
        double sum = 0.0;
        for (Transaction t : list) {
            sum += t.calculate();
        }
        return sum;
    }

    /** {@inheritDoc} */
    @Override
    public List<Transaction> getTransactions(String account) {
        List<Transaction> list = accountsToTransactions.get(account);
        return (list == null) ? Collections.emptyList() : new ArrayList<>(list);
    }

    /** {@inheritDoc} */
    @Override
    public List<Transaction> getTransactionsSorted(String account, boolean asc) {
        List<Transaction> list = new ArrayList<>(getTransactions(account));
        list.sort(Comparator.comparingDouble(Transaction::calculate));
        if (!asc) Collections.reverse(list);
        return list;
    }

    /** {@inheritDoc} */
    @Override
    public List<Transaction> getTransactionsByType(String account, boolean positive) {
        List<Transaction> out = new ArrayList<>();
        for (Transaction t : getTransactions(account)) {
            double v = t.calculate();
            if (positive && v > 0) out.add(t);
            if (!positive && v < 0) out.add(t);
        }
        return out;
    }



}


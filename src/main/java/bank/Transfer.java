package bank;

import java.util.Objects;

/**
 * Überweisung zwischen zwei Parteien.
 * Hier fallen keine Zinsen/Gebühren an.
 */
public class Transfer extends Transaction  {

    /** Absender. */
    private String sender;

    /** Empfänger. */
    private String recipient;

    /**
     * Setzt den Betrag. Bei Überweisungen muss er > 0 sein.
     *
     * @param amount positiver Betrag
     */
    @Override
    public void setAmount(double amount) {
        if (amount <= 0) {
            System.out.println("Fehler: amount für Transfer muss > 0 sein.");
            return;
        }
        super.setAmount(amount);
    }



    /**
     * Erzeugt eine Überweisung mit Absender/Empfänger.
     *
     * @param date Datum (DD.MM.YYYY)
     * @param amount positiver Betrag
     * @param description Beschreibung
     * @param sender Absender
     * @param recipient Empfänger
     */
    public Transfer(String date, double amount, String description,
                    String sender, String recipient) {
        super(date, amount, description);
        this.sender = sender;
        this.recipient = recipient;
    }

    /** Kopierkonstruktor. */
    public Transfer(Transfer other) {
        super(other);
        this.sender = other.sender;
        this.recipient = other.recipient;
    }

    /** @return Absender */
    public String getSender() { return sender; }

    /** Setzt den Absender. */
    public void setSender(String sender) { this.sender = sender; }

    /** @return Empfänger */
    public String getRecipient() { return recipient; }

    /** Setzt den Empfänger. */
    public void setRecipient(String recipient) { this.recipient = recipient; }

    /**
     * Bei Überweisungen keine Gebühren:
     *
     * @return nomineller Betrag unverändert
     */
    @Override
    public double calculate() { return getAmount(); }

    /** Textausgabe inkl. Absender/Empfänger. */
    @Override
    public String toString() {
        return super.toString() + ", Sender: " + sender + ", Recipient: " + recipient;
    }


    /** Vergleich inkl. Absender/Empfänger. */
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        Transfer other = (Transfer) obj;
        return sender.equals(other.sender) &&
                recipient.equals(other.recipient);
    }


}

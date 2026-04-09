package bank;

import java.util.Objects;
/**
 * Abstrakte Oberklasse für alle Transaktionen.
 * Enthält gemeinsame Attribute und Grundfunktionen.
 */

 public abstract class Transaction implements CalculateBill{
    /** Datum im Format DD.MM.YYYY (hier nicht geprüft). */
    protected String date;

    /** Nomineller Betrag (ohne Gebühren-Logik). */
    protected double amount;

    /** Kurze Beschreibung. */
    protected String description;


    /**
     * Konstruktor für gemeinsame Felder.
     *
     * @param date Datum (DD.MM.YYYY)
     * @param description Beschreibungstext
     * @param amount Amount
     */
    public Transaction(String date, double amount, String description){
        this.date = date;
        setAmount(amount);
        this.description = description;
    }
    public Transaction(Transaction other){
        this.date = other.date;
        this.amount = other.amount;
        this.description = other.description;
    }
    /** @return Datum der Transaktion */
    public String getDate() { return date; }

    /** @return nomineller Betrag (nicht der berechnete) */
    public double getAmount() { return amount; }

    /** @return Beschreibungstext */
    public String getDescription() { return description; }

    /** Setzt das Datum (ohne Formatprüfung). */
    public void setDate(String date) { this.date = date; }

    /** Setzt den nominellen Betrag (Prüfung macht die Unterklasse). */
    public void setAmount(double amount) { this.amount = amount; }

    /** Setzt die Beschreibung. */
    public void setDescription(String description) { this.description = description; }
    /** Textausgabe  */

    @Override
    public String toString() {
        return "Date: " + getDate()
                + ", Amount: " + calculate()
                + ", Description: " + getDescription();
    }

    /** Vergleich */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction other = (Transaction) obj;
        return Double.compare(amount, other.amount) == 0 &&
                date.equals(other.date) &&
                description.equals(other.description);
    }



}





//Double.compare
//java.util.Objects.equals


















/*
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;                         // same reference
    if (obj == null || getClass() != obj.getClass())      // null or different class
        return false;

    Transaction other = (Transaction) obj;
    return Double.compare(this.amount, other.amount) == 0
        && java.util.Objects.equals(this.date, other.date)
        && java.util.Objects.equals(this.description, other.description);
}
 */
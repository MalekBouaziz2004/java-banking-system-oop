package bank;

/**
 * Zahlung (Einzahlung oder Auszahlung) mit einfachen Gebühren.
 *
 * Regel für {@link #calculate()}:
 * - amount > 0 (Einzahlung): amount * (1 - incomingInterest)
 * - amount < 0 (Auszahlung): amount * (1 + outgoingInterest)
 * amount selbst wird nicht verändert.
 */
public class Payment extends Transaction  {

    /** Gebühr bei Einzahlung, Wert zwischen 0 und 1. */
    private double incomingInterest;

    /** Gebühr bei Auszahlung, Wert zwischen 0 und 1. */
    private double outgoingInterest;

    /**
     * Erzeugt eine Zahlung ohne Gebühren.
     *
     * @param date Datum (DD.MM.YYYY)
     * @param amount positiver oder negativer Betrag
     * @param description Beschreibung
     */

    public Payment(String date, double amount, String description) {
        super(date, amount, description);
    }


    /**
     * Erzeugt eine Zahlung mit Gebühren.
     *
     * @param date Datum (DD.MM.YYYY)
     * @param amount Betrag
     * @param description Beschreibung
     * @param incomingInterest Gebühr bei Einzahlung (0..1)
     * @param outgoingInterest Gebühr bei Auszahlung (0..1)
     */
    public Payment(String date, double amount, String description,
                   double incomingInterest, double outgoingInterest) {
        super(date, amount, description);
        setIncomingInterest(incomingInterest);
        setOutgoingInterest(outgoingInterest);
    }

    /** Kopierkonstruktor. */
    public Payment(Payment other) {
        super(other);
        this.incomingInterest = other.incomingInterest;
        this.outgoingInterest = other.outgoingInterest;
    }

    /** @return Gebühr bei Einzahlung (0..1) */
    public double getIncomingInterest() { return incomingInterest; }

    /**
     * Setzt die Gebühr bei Einzahlung.
     *
     * @param v Wert 0..1, sonst bleibt alter Wert
     */
    public void setIncomingInterest(double v) {
        if (v < 0 || v > 1) { System.out.println("incomingInterest muss 0..1 sein."); return; }
        this.incomingInterest = v;
    }

    /** @return Gebühr bei Auszahlung (0..1) */
    public double getOutgoingInterest() { return outgoingInterest; }

    /**
     * Setzt die Gebühr bei Auszahlung.
     *
     * @param v Wert 0..1, sonst bleibt alter Wert
     */
    public void setOutgoingInterest(double v) {
        if (v < 0 || v > 1) { System.out.println("outgoingInterest muss 0..1 sein."); return; }
        this.outgoingInterest = v;
    }

    /**
     * Rechnet den effektiven Betrag aus Sicht der Bank.
     *
     * @return berechneter Betrag (mit Gebühren)
     */
   @Override
    public double calculate() {
        double a = getAmount();
        if (a > 0)  return a * (1.0 - incomingInterest);
        if (a < 0)  return a * (1.0 + outgoingInterest);
        return 0.0;
    }



    /** Textausgabe inkl. Gebühren. */
    @Override
    public String toString() {
        return super.toString()
                + ", incomingInterest: " + getIncomingInterest()
                + ", outgoingInterest: " + getOutgoingInterest();
    }

    /** Vergleich inkl. Gebühren-Felder. */
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        Payment other = (Payment) obj;
        return Double.compare(incomingInterest, other.incomingInterest) == 0 &&
                Double.compare(outgoingInterest, other.outgoingInterest) == 0;
    }



}





























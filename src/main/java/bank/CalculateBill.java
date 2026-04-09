package bank;

/**
 * Einfache Schnittstelle zum Berechnen des effektiven Betrags einer Transaktion.
 * Die Methode soll nur rechnen und keine Felder verändern.
 */
public interface CalculateBill {
    /**
     * Berechnet den effektiven Betrag.
     *
     * @return berechneter Betrag (kann vom gespeicherten amount abweichen)
     */
    double calculate();
}

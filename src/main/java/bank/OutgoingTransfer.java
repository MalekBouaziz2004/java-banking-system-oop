package bank;// src/bank/OutgoingTransfer.java

public class OutgoingTransfer extends Transfer {

    // match Transfer(String date, double amount, String description, String sender, String recipient)
    public OutgoingTransfer(String date, double amount, String description,
                            String sender, String recipient) {
        super(date, amount, description, sender, recipient);
    }


    @Override
    public double calculate() {
        return -getAmount(); // outgoing is negative
    }
}

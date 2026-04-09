package bank;// src/bank/IncomingTransfer.java

public class IncomingTransfer extends Transfer {

    // match Transfer(String date, double amount, String description, String sender, String recipient)
    public IncomingTransfer(String date, double amount, String description,
                            String sender, String recipient) {
        super(date, amount, description, sender, recipient);
    }


}

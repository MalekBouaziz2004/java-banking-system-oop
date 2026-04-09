package bank.exceptions;

public class TransactionDoesNotExistException extends Exception {
    public TransactionDoesNotExistException() { }
    public TransactionDoesNotExistException(String message) { super(message); }
}

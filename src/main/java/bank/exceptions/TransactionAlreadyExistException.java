package bank.exceptions;

public class TransactionAlreadyExistException extends Exception {
    public TransactionAlreadyExistException() { }
    public TransactionAlreadyExistException(String message) { super(message); }
}
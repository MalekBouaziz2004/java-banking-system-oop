package bank.exceptions;

public class AccountAlreadyExistsException extends Exception {
    public AccountAlreadyExistsException() { }
    public AccountAlreadyExistsException(String message) { super(message); }
}
package ui;

import bank.*;
import bank.exceptions.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;

public class AccountViewController {

    @FXML
    private Button backButton;

    @FXML
    private Label accountNameLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private ListView<Transaction> transactionListView;

    @FXML
    private Button sortAscButton;

    @FXML
    private Button sortDescButton;

    @FXML
    private Button filterPosButton;

    @FXML
    private Button filterNegButton;

    @FXML
    private Button newTransactionButton;


    private PrivateBank bank;
    private String accountName;
    private Stage mainStage;
    private Scene mainScene;

    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    public void setMainScene(Scene scene) {
        this.mainScene = scene;
    }


    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();


    // Wird aus MainPageController aufgerufen!
    public void setAccount(PrivateBank bank, String accountName) {
        this.bank = bank;
        this.accountName = accountName;

        accountNameLabel.setText("Account: " + accountName);

        loadTransactions();
        updateBalance();
        setupContextMenu();
        setupButtons();
    }


    /** Transaktionen in ListView laden */
    private void loadTransactions() {
        transactionList.setAll(bank.getTransactions(accountName));
        transactionListView.setItems(transactionList);
    }

    /** Kontostand aktualisieren */
    private void updateBalance() {
        double bal = bank.getAccountBalance(accountName);
        balanceLabel.setText("Kontostand: " + bal + " €");
    }


    /** Rechtsklick → Transaktion löschen */
    private void setupContextMenu() {

        ContextMenu menu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Löschen");
        menu.getItems().add(deleteItem);

        transactionListView.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                Transaction t = transactionListView.getSelectionModel().getSelectedItem();
                if (t != null)
                    menu.show(transactionListView, e.getScreenX(), e.getScreenY());
            }
        });

        deleteItem.setOnAction(a -> deleteTransaction());
    }

    private void deleteTransaction() {

        Transaction t = transactionListView.getSelectionModel().getSelectedItem();
        if (t == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Transaktion wirklich löschen?",
                ButtonType.YES, ButtonType.NO);

        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                bank.removeTransaction(accountName, t);
                loadTransactions();
                updateBalance();
            } catch (Exception ex) {
                showError("Fehler beim Löschen: " + ex.getMessage());
            }
        }
    }


    /** Sortier- & Filter-Buttons */
    private void setupButtons() {

        sortAscButton.setOnAction(e -> {
            transactionList.setAll(bank.getTransactionsSorted(accountName, true));
        });

        sortDescButton.setOnAction(e -> {
            transactionList.setAll(bank.getTransactionsSorted(accountName, false));
        });

        filterPosButton.setOnAction(e -> {
            transactionList.setAll(bank.getTransactionsByType(accountName, true));
        });

        filterNegButton.setOnAction(e -> {
            transactionList.setAll(bank.getTransactionsByType(accountName, false));
        });

        newTransactionButton.setOnAction(e -> showTransactionDialog());

        backButton.setOnAction(e -> closeWindow());
    }


    /** Fenster schließen (zurück zur MainView) */
    private void closeWindow() {
        mainStage.setScene(mainScene);
    }




    /** Dialog zum Erstellen von Payment / Transfer */
    private void showTransactionDialog() {

        ChoiceDialog<String> typeDialog =
                new ChoiceDialog<>("Payment", "Payment", "Transfer");

        typeDialog.setHeaderText("Neue Transaktion");
        typeDialog.setContentText("Typ wählen:");

        String type = typeDialog.showAndWait().orElse(null);
        if (type == null) return;


        if (type.equals("Payment")) {
            createPayment();
        } else {
            createTransfer();
        }
    }


    /** Payment erzeugen */
    private void createPayment() {

        Dialog<Payment> dialog = new Dialog<>();
        dialog.setTitle("Neue Zahlung (Payment)");

        // Felder
        TextField date = new TextField();
        TextField amount = new TextField();
        TextField desc = new TextField();

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);

        gp.add(new Label("Datum:"), 0, 0);
        gp.add(date, 1, 0);

        gp.add(new Label("Betrag:"), 0, 1);
        gp.add(amount, 1, 1);

        gp.add(new Label("Beschreibung:"), 0, 2);
        gp.add(desc, 1, 2);

        dialog.getDialogPane().setContent(gp);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    return new Payment(
                            date.getText(),
                            Double.parseDouble(amount.getText()),
                            desc.getText(),
                            bank.getIncomingInterest(),
                            bank.getOutgoingInterest()
                    );
                } catch (Exception e) {
                    showError("Ungültige Eingabe!");
                }
            }
            return null;
        });

        Payment p = dialog.showAndWait().orElse(null);
        if (p != null) {
            try {
                bank.addTransaction(accountName, p);
                loadTransactions();
                updateBalance();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }


    /** Transfer erzeugen */
    private void createTransfer() {

        Dialog<Transfer> dialog = new Dialog<>();
        dialog.setTitle("Neue Überweisung (Transfer)");

        TextField date = new TextField();
        TextField amount = new TextField();
        TextField desc = new TextField();
        TextField sender = new TextField();
        TextField recipient = new TextField();

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);

        gp.add(new Label("Datum:"), 0, 0);
        gp.add(date, 1, 0);

        gp.add(new Label("Betrag:"), 0, 1);
        gp.add(amount, 1, 1);

        gp.add(new Label("Beschreibung:"), 0, 2);
        gp.add(desc, 1, 2);

        gp.add(new Label("Sender:"), 0, 3);
        gp.add(sender, 1, 3);

        gp.add(new Label("Empfänger:"), 0, 4);
        gp.add(recipient, 1, 4);

        dialog.getDialogPane().setContent(gp);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);


        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    double amountValue = Double.parseDouble(amount.getText());

                    String d = date.getText();
                    String des = desc.getText();
                    String s = sender.getText();
                    String r = recipient.getText();

                    // Wenn aktueller Account der Sender → OutgoingTransfer
                    if (s.equals(accountName)) {
                        return new OutgoingTransfer(d, amountValue, des, s, r);
                    }

                    // Wenn aktueller Account der Empfänger → IncomingTransfer
                    if (r.equals(accountName)) {
                        return new IncomingTransfer(d, amountValue, des, s, r);
                    }

                    // Falls beide nicht passen → normale Transfer (selten)
                    return new Transfer(d, amountValue, des, s, r);

                } catch (Exception e) {
                    showError("Ungültige Eingabe!");
                }
            }
            return null;
        });


        Transfer t = dialog.showAndWait().orElse(null);

        if (t != null) {
            try {
                bank.addTransaction(accountName, t);
                loadTransactions();
                updateBalance();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }


    /** Fehlerdialog */
    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }

}

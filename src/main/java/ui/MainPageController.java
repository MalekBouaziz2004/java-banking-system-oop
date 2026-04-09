package ui;

import bank.PrivateBank;
import bank.exceptions.AccountAlreadyExistsException;
import bank.exceptions.AccountDoesNotExistException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPageController {

    @FXML//verbindet FXML-Komponenten mit Java-Code.
    private ListView<String> accountListView;

    @FXML
    private Button addAccountButton;

    private PrivateBank bank;
    private Stage mainStage;
    private Scene mainScene;

    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    public void setMainScene(Scene scene) {
        this.mainScene = scene;
    }


    /** Wird vom FxApplication aufgerufen */
    public void init(PrivateBank bank) {
        this.bank = bank;
        updateAccountList();
        setupContextMenu();
    }//javaFX-Controller KEINEN Konstruktor benutzen können

    /** Accounts in ListView laden */
    private void updateAccountList() {
        accountListView.getItems().setAll(bank.getAllAccounts());
    }

    /** Rechtsklick-Menü hinzufügen */
    private void setupContextMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem openItem = new MenuItem("Auswählen");
        MenuItem deleteItem = new MenuItem("Löschen");

        menu.getItems().addAll(openItem, deleteItem);

        accountListView.setContextMenu(menu);

        // Öffnen
        openItem.setOnAction(e -> {
            String selected = accountListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openAccountView(selected);
            }
        });

        // Löschen
        deleteItem.setOnAction(e -> {
            String selected = accountListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                deleteAccount(selected);
            }
        });
    }

    private void openAccountView(String account) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/accountview.fxml"));
            Scene accountScene = new Scene(loader.load());

            AccountViewController controller = loader.getController();
            controller.setAccount(bank, account);
            controller.setMainStage(mainStage);
            controller.setMainScene(mainScene);

            // WECHSELT Szene im selben Fenster
            mainStage.setScene(accountScene);

        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Fehler beim Laden der AccountView:\n" + ex.getMessage());
            alert.showAndWait();
        }
    }



    /** Account löschen */
    private void deleteAccount(String name) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Account löschen?");
        confirm.setContentText("Soll der Account \"" + name + "\" wirklich gelöscht werden?");

        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            bank.deleteAccount(name);
            updateAccountList();
        } catch (AccountDoesNotExistException | IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Fehler: " + ex.getMessage()).show();
        }
    }

    /** Klick auf „Account hinzufügen“ */
    @FXML//automatisch von JavaFX aufgerufen
    private void initialize() {
        addAccountButton.setOnAction(e -> createNewAccount());
    }

    private void createNewAccount() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Neuen Account anlegen");
        dialog.setContentText("Kontoname:");

        String name = dialog.showAndWait().orElse(null);
        if (name == null || name.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ungültiger Kontoname");
            alert.setHeaderText("Leerer Kontoname");
            alert.setContentText("Der Kontoname darf nicht leer sein!");
            alert.showAndWait();
            return;
        }

        try {
            bank.createAccount(name);
            updateAccountList();
        } catch (AccountAlreadyExistsException | IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Fehler: " + ex.getMessage()).show();
        }
    }
}

package ui;

import bank.PrivateBank;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxApplication extends Application {

    public void start(Stage stage) throws Exception {

        PrivateBank bank = new PrivateBank("MyBank", 0.02, 0.05, "bankdata");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainpage.fxml"));
        Scene mainScene = new Scene(loader.load());

        MainPageController controller = loader.getController();
        controller.init(bank);
        controller.setMainStage(stage);
        controller.setMainScene(mainScene);

        stage.setTitle("PrivateBank");
        stage.setScene(mainScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

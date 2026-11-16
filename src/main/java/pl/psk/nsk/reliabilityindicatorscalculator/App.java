package pl.psk.nsk.reliabilityindicatorscalculator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.psk.nsk.reliabilityindicatorscalculator.ui.MainView;


import java.util.Objects;


public class App extends Application {

    @Override
    public void start(Stage stage) {
        MainView view = new MainView();
        Scene scene = new Scene(view.getRoot(), 640, 640);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        stage.setTitle("NSK – wskaźniki niezawodności struktur");
        stage.setScene(scene);
        stage.setMinWidth(600);
        stage.setMinHeight(800);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

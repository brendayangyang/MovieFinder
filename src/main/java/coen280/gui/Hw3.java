package coen280.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Hw3 extends Application implements EventHandler<ActionEvent>{


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/hw3.fxml"));
        primaryStage.setTitle("Movie Search");

        primaryStage.setScene(new Scene(root, 1000, 750));
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void handle(ActionEvent event) {

    }

}

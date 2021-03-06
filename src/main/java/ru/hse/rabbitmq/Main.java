package ru.hse.rabbitmq;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        HBox hbButtons = new HBox();
        hbButtons.setSpacing(10.0);

        Label lblName = new Label("User name:");
        TextField tfName = new TextField();
        tfName.setFocusTraversable(false);

        Label lblServer = new Label("Address");
        TextField tfServer = new TextField();

        Label lbChannel = new Label("channel name");
        TextField tfChannel = new TextField();

        Button btnBecomeClient = new Button("Start");
        btnBecomeClient.setOnMouseClicked(event -> startAsClient(
                tfName.getCharacters().toString(),
                tfServer.getCharacters().toString(),
                tfChannel.getCharacters().toString()
        ));


        Button btnExit = new Button("Exit");
        btnExit.setOnMouseClicked(event -> Platform.exit());

        hbButtons.getChildren().addAll(btnBecomeClient, btnExit);
        grid.add(lblName, 0, 0);
        grid.add(tfName, 1, 0);
        grid.add(lblServer, 0, 1);
        grid.add(tfServer, 1, 1);
        grid.add(lbChannel, 0, 2);
        grid.add(tfChannel, 1, 2);
        grid.add(hbButtons, 0, 3, 2, 1);

        Scene scene = new Scene(grid);
        stage.setTitle("memessenger");
        stage.setScene(scene);
        stage.setMinHeight(500);
        stage.setMinWidth(500);
        stage.setResizable(false);
        stage.show();
    }

    private void startAsClient(String userName, String address, String channel) {
        Chat chat = new Chat(channel, userName);
        new Publisher().init(address, chat);
        new Receiver().init(address, chat);
        chat.start(channel, new Stage());
    }
}

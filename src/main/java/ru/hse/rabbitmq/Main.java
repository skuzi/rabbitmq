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
    public static void printUsage() {
        System.err.println("Usage: ./gradlew run --args='username [host] port'"); //TODO
    }

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

        Button btnBecomeClient = new Button("Start as client");
        btnBecomeClient.setOnMouseClicked(event -> startAsClient(tfName.getCharacters().toString(), stage));

        Button btnBecomeServer = new Button("Start as server");
        btnBecomeServer.setOnMouseClicked(event -> startAsServer(tfName.getCharacters().toString(), stage));

        Button btnExit = new Button("Exit");
        btnExit.setOnMouseClicked(event -> Platform.exit());


        hbButtons.getChildren().addAll(btnBecomeClient, btnBecomeServer, btnExit);
        grid.add(lblName, 0, 0);
        grid.add(tfName, 1, 0);
        grid.add(hbButtons, 0, 2, 2, 1);


        Scene scene = new Scene(grid);
        stage.setTitle("memessenger");
        stage.setScene(scene);
        stage.setMinHeight(500);
        stage.setMinWidth(500);
        stage.setResizable(false);
        stage.show();
    }

    private void startAsClient(String userName, Stage stage) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        Label lblServer = new Label("Address");
        TextField tfServer = new TextField();

        Label lblPort = new Label("Port");
        TextField tfPort = new TextField();

        Button start = new Button("Start");
        start.setOnMouseClicked(event -> {
            Chat chat = new Chat(userName);
            new Thread(() -> {
//                try {
////                    ChatClient.run(userName,
////                            tfServer.getCharacters().toString(),
////                            Integer.parseInt(tfPort.getCharacters().toString()),
////                            chat);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }).start();
            chat.start(stage);
        });

        grid.add(lblServer, 0, 0);
        grid.add(tfServer, 1, 0);

        grid.add(lblPort, 0, 1);
        grid.add(tfPort, 1, 1);
        grid.add(start, 1, 2, 2, 1);


        Scene scene = new Scene(grid);
        stage.setScene(scene);
        stage.show();
    }

    private void startAsServer(String userName, Stage stage) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        Label lblPort = new Label("Port");
        TextField tfPort = new TextField();

        Button start = new Button("Start");
        start.setOnMouseClicked(event -> {
            Chat chat = new Chat(userName);
            new Thread(() -> {
//                try {
//                    ChatServer.run(userName,
//                            Integer.parseInt(tfPort.getCharacters().toString()),
//                            chat);
//                } catch (IOException | InterruptedException e) {
//                    e.printStackTrace();
//                }
            }).start();
            chat.start(stage);
        });

        grid.add(lblPort, 0, 0);
        grid.add(tfPort, 1, 0);
        grid.add(start, 1, 2, 2, 1);

        Scene scene = new Scene(grid);
        stage.setScene(scene);
        stage.show();
    }
}

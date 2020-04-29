package ru.hse.rabbitmq;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.BiConsumer;

public class Chat implements Ui {
    private final ObservableList<String> msgs = FXCollections.<String>observableArrayList();
    private final String userName;
    private BiConsumer<String, String> callback;

    public Chat(String userName) {
        this.userName = userName;
    }

    public void start(Stage stage) {
        ListView<String> seasons = new ListView<>(msgs);
        seasons.setOrientation(Orientation.VERTICAL);

        GridPane grid = new GridPane();

        TextField message = new TextField();
        Button send = new Button("send");
        long timeStamp = System.currentTimeMillis();
        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
        Date result = new Date(timeStamp);
        String time = simple.format(result);

        send.setOnMouseClicked(e -> {
            sendMessage(time, message);
        });


        send.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendMessage(time, message);
            }
        });

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(80);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(10);

        grid.getColumnConstraints().addAll(column1, column2);

        grid.add(message, 0, 0);
        grid.add(send, 1, 0);

        BorderPane border = new BorderPane();
        border.setBottom(grid);
        border.setCenter(seasons);

        Scene scene = new Scene(border, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void displayMsg(String channelName, String message) {
        Platform.runLater(() -> msgs.addAll(message));
    }

    @Override
    public void setSendCallback(BiConsumer<String, String> callback) {
        this.callback = callback;
    }

    private void sendMessage(String time, TextField message) {
        msgs.add("<" + time + ">" + "[" + userName + "]" + ": " + message.getCharacters().toString());
        if (callback != null) {
            callback.accept(userName, message.getCharacters().toString());
        }
        message.clear();
        message.requestFocus();
    }
}

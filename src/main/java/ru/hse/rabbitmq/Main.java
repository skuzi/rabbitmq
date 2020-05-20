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

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    private SSLContext initSsl() throws Throwable { // kek

        char[] keyPassphrase = "sd-test".toCharArray();
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream("/home/lyubortk/hw/rabbitmq-serv/certs/client_key.p12"), keyPassphrase);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, "sd-test".toCharArray());

        char[] trustPassphrase = "sd-test".toCharArray();
        KeyStore tks = KeyStore.getInstance("JKS");
        tks.load(new FileInputStream("/home/lyubortk/hw/rabbitmq-serv/java-rabbitstore"), trustPassphrase);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(tks);

        SSLContext c = SSLContext.getInstance("TLSv1.2");
        c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return c;
    }

    @Override
    public void start(Stage stage) {
        System.setProperty("javax.net.debug", "ssl");
//        System.setProperty("jdk.tls.client.protocols", "TLSv1.2");

        SSLContext sslContext;
        try {
            sslContext = initSsl();
        } catch (Throwable throwable) {
            System.err.println(throwable.getMessage());
            throwable.printStackTrace();
            return;
        }

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
                tfChannel.getCharacters().toString(),
                sslContext
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

    private void startAsClient(String userName, String address, String channel, SSLContext sslContext) {
        Chat chat = new Chat(channel, userName);
        new Publisher().init(address, chat, sslContext);
        new Receiver().init(address, chat, sslContext);
        chat.start(channel, new Stage());
    }
}

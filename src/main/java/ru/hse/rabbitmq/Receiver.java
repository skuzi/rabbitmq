package ru.hse.rabbitmq;

import com.rabbitmq.client.*;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Receiver {
    private final ConnectionFactory factory = new ConnectionFactory();
    private Ui uiField;

    public void init(String serverName, Ui ui, SSLContext context) {
        factory.setHost(serverName);
        factory.setPort(5671);
        factory.useSslProtocol(context);
//        factory.enableHostnameVerification();
        ui.setSubscribeCallback(this::subscribe);
        uiField = ui;
    }

    private void subscribe(String channelName)  {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(channelName, BuiltinExchangeType.FANOUT);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, channelName, "");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                uiField.displayMsg(channelName, message);
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }
}

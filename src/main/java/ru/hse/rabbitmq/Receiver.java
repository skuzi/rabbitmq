package ru.hse.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Receiver {
    private static final ConnectionFactory factory = new ConnectionFactory();
    private static Ui ui;

    private static void init(String serverName, Ui ui) {
        factory.setHost(serverName);
        Receiver.ui = ui;
    }

    private static void subscribe(String channelName) throws IOException, TimeoutException {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(channelName, BuiltinExchangeType.FANOUT);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, channelName, "");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            ui.displayMsg(channelName, message);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}

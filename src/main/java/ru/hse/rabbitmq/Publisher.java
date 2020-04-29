package ru.hse.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Publisher {
    private final ConnectionFactory factory = new ConnectionFactory();

    public void init(String serverName, Ui ui) {
        factory.setHost(serverName);
        ui.setSendCallback(this::publish);
    }

    private void publish(String channelName, String message) {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(channelName, BuiltinExchangeType.FANOUT);
            channel.basicPublish(channelName, "", null, message.getBytes());
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }
}

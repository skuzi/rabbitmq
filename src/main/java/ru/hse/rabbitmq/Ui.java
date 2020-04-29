package ru.hse.rabbitmq;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Ui {
    void displayMsg(String channelName, String msg);

    void setSendCallback(BiConsumer<String, String> callback);

    void setSubscribeCallback(Consumer<String> callback);
}
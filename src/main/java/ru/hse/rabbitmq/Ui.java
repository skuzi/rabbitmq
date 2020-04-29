package ru.hse.rabbitmq;

import java.util.function.BiConsumer;

public interface Ui {
    void displayMsg(String channelName, String msg);

    void setSendCallback(BiConsumer<String, String> callback);
}
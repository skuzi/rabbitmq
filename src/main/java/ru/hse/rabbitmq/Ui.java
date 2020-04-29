package ru.hse.rabbitmq;

import java.util.function.BiConsumer;

public interface Ui {
    void displayMsg(String msg);

    void setCallback(BiConsumer<String, String> callback);
}
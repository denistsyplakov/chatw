package ru.yandex.lc.chatw.dto;

public class CommandChatMessage {

    /**
     * Room or user, depending on stomp channel.
     */
    public String to;

    public String message;

}

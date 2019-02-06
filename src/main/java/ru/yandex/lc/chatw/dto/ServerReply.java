package ru.yandex.lc.chatw.dto;

public class ServerReply {

    public enum Status{
        OK,
        NOT_OK
    }

    public Status status;

    public String error;

}

package ru.yandex.lc.chatw.dto;

/**
 * Unified server reply object. Because reply is json this object is a bit messy collection of possible replies.
 */
public class ServerReply {

    public enum Status {
        OK,
        NOT_OK
    }

    public ServerReply(Status status, String error) {
        this.status = status;
        this.error = error;
    }

    public Status status;

    public String error;

    public String payload;

    public static ServerReply okReply() {
        return new ServerReply(Status.OK, null);
    }

    public static ServerReply okReply(String payload) {
        ServerReply reply = new ServerReply(Status.OK, null);
        reply.payload = payload;
        return reply;
    }

}

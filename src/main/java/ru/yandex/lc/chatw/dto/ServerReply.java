package ru.yandex.lc.chatw.dto;

/**
 * Unified server reply object. Because reply is json this object is a bit messy collection of possible replies.
 */
public class ServerReply {

    public enum Status {
        OK,
        NOT_OK
    }

    public ServerReply(Status status, String error,String command) {
        this.status = status;
        this.error = error;
        this.command = command;
    }

    public Status status;

    public String command;

    public String error;

    public String payload;

    public static ServerReply okReply(String command) {
        return new ServerReply(Status.OK, null,command);
    }

    public static ServerReply okReplyWithPayload(String payload,String command) {
        ServerReply reply = new ServerReply(Status.OK, null,command);
        reply.payload = payload;
        return reply;
    }

    @Override
    public String toString() {
        return "ServerReply{" +
                "status=" + status +
                ", command='" + command + '\'' +
                ", error='" + error + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}

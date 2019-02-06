package ru.yandex.lc.chatw.model;

import java.util.HashSet;
import java.util.Set;

public class UserSession {

    public String sessionId;

    public String userName;

    public final Set<ChatRoom> chatRooms = new HashSet<>();

    public UserSession(String sessionId) {
        this.sessionId = sessionId;
    }
}

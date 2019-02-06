package ru.yandex.lc.chatw.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatRoom {

    public final String uuid = UUID.randomUUID().toString();

    public final String name;

    public final Set<UserSession> users = new HashSet<>();

    public ChatRoom(String name) {
        this.name = name;
    }
}

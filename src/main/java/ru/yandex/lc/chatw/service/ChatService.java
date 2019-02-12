package ru.yandex.lc.chatw.service;

import jdk.jshell.spi.ExecutionControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.lc.chatw.model.ChatRoom;
import ru.yandex.lc.chatw.model.UserSession;
import ru.yandex.lc.chatw.http.WSEvents;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ChatService {

    private AtomicInteger uniqueChatRoomNamePrefix = new AtomicInteger(0);

    private final Logger log = LoggerFactory.getLogger(WSEvents.class);

    /**
     * session id -> UserSession
     */
    private final Map<String, UserSession> sessionMap = new ConcurrentHashMap<>();

    /**
     * userName -> UserSession
     */
    private final Map<String, UserSession> userNames = new ConcurrentHashMap<>();

    /**
     * chat room uuid -> ChatRoom
     */
    private final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    /**
     * Set of room names
     */
    private final Set<String> chatRoomNames = new HashSet<>();

    /**
     * just registers user in a map
     *
     * @param id sessionId
     * @return session object.
     */
    public UserSession registerSession(String id) {
        return sessionMap.computeIfAbsent(id, UserSession::new);
    }

    /**
     * Deregister user from the list of sessions and removes all related data structures.
     *
     * @param id sessionId
     */
    public synchronized void removeSession(String id) {
        Optional.of(sessionMap.remove(id))
                .ifPresent((val) -> {
                    Optional.of(val.userName).ifPresent(userNames::remove);
                    chatRooms.values().forEach((room) -> {
                        room.users.remove(val);
                        if (room.users.size() == 0) {
                            chatRoomNames.remove(room.name);
                        }
                    });
                    chatRooms.entrySet().removeIf((entry) -> entry.getValue().users.size() == 0);
                });
    }

    /**
     * Try to claim user name for session.
     *
     * @param sessionId sessionId
     * @param userName  desired user name
     * @return true if captured, false if name is already taken.
     */
    public synchronized boolean tryToClaimUserName(String sessionId, String userName) {
        if (userNames.containsKey(userName)) {
            return false;
        }
        var userSession = sessionMap.get(sessionId);
        if (userSession == null) {
            throw new RuntimeException("Session not found: " + sessionId);
        }
        userSession.userName = userName;
        userNames.put(userName, userSession);
        return true;
    }

    /**
     * Tries to create a chat room with the given name. If OK returns room UUID. If room already exists,
     * method returns null. Automatically adds user to the room.
     *
     * @param roomName  desired room name
     * @param sessionId session Id of a user who are creating the room.
     * @return new room UUID or null if room with the given name exists or user with the session id does not exists.
     */
    public synchronized String createChatRoom(String sessionId, String roomName) {
        if (chatRoomNames.contains(roomName)) {
            return null;
        }
        var us = sessionMap.get(sessionId);
        if (us == null) {
            return null;
        }
        var chatRoom = new ChatRoom(roomName);
        chatRooms.put(chatRoom.uuid, chatRoom);
        chatRoom.users.add(us);
        chatRoomNames.add(roomName);
        return chatRoom.uuid;
    }

    /**
     * Tries to create chat room with the given user. If user does not exists, returns null. There could be several
     * chat rooms with the same user. Unique chat room name is generated automatically using @{@link #uniqueChatRoomNamePrefix}.
     * Method automatically adds both participants to the room.
     *
     * @param userName1 user name of the first user
     * @param userName2 user name of the second user
     * @return new room UUID or null.
     */
    public synchronized String createChatRoomWithTheUser(String userName1, String userName2) {
        var u1 = userNames.get(userName1);
        var u2 = userNames.get(userName2);
        if ((u1 == null) || (u2 == null)) {
            return null;
        }
        String name;
        do {
            name = uniqueChatRoomNamePrefix.getAndIncrement() + "|" + userName1 + " > " + userName2;
        } while (chatRoomNames.contains(name));
        var chatRoom = new ChatRoom(name);
        chatRooms.put(chatRoom.uuid, chatRoom);
        chatRoom.users.add(u1);
        chatRoom.users.add(u2);
        chatRoomNames.add(chatRoom.name);
        return chatRoom.uuid;
    }

    /**
     * Attempts to join or leave chat with the given room uuid
     *
     * @param roomUUID  room uuid
     * @param sessionId id of a user who joins/leaves chat
     * @return true if operation performed, false if room with the given uuid does not exists or user was not found.
     */
    public synchronized boolean joinLeaveChatRoom(String sessionId, String roomUUID, RoomAction action) {
        var room = chatRooms.get(roomUUID);
        if (room == null) {
            return false;
        }
        var u = sessionMap.get(sessionId);
        if (u == null) {
            return false;
        }
        switch (action) {
            case JOIN: {
                room.users.add(u);
                break;
            }
            case LEAVE: {
                room.users.remove(u);
                break;
            }
            default:
                throw new IllegalStateException(action + "");
        }
        return true;
    }

    public enum RoomAction {
        LEAVE,
        JOIN
    }

}

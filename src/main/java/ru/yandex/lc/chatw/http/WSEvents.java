package ru.yandex.lc.chatw.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.yandex.lc.chatw.service.ChatService;

@Component
public class WSEvents {

    private Logger log = LoggerFactory.getLogger(WSEvents.class);

    private final ChatService chatService;

    @Autowired
    public WSEvents(ChatService chatService) {
        this.chatService = chatService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        log.info("Session connected id {}", sha.getSessionId());
        chatService.registerSession(sha.getSessionId());
    }

    @EventListener
    public void onSessionDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        log.info("Session disconnected id {}", sha.getSessionId());
        chatService.removeSession(sha.getSessionId());
    }

}

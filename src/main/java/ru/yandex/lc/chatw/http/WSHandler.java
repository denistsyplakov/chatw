package ru.yandex.lc.chatw.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import ru.yandex.lc.chatw.dto.*;
import ru.yandex.lc.chatw.service.ChatService;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

@Controller
public class WSHandler {

    Logger log = LoggerFactory.getLogger(WSHandler.class);

    private final SimpMessagingTemplate smt;

    private final ChatService chat;

    @Autowired
    public WSHandler(ChatService chat, SimpMessagingTemplate smt) {
        this.chat = chat;
        this.smt = smt;
    }

    @MessageMapping("/set-name")
    public void setName(@Header("simpSessionId") String sessionId, CommandSetName cmd) {
        log.info("Set name:" + cmd);
        if (chat.tryToClaimUserName(sessionId, cmd.name)) {
            sendReplyToUser(sessionId, ServerReply.okReply(CommandSetName.class.getSimpleName()));
        } else {
            sendReplyToUser(sessionId, new ServerReply(
                    ServerReply.Status.NOT_OK,
                    "Name is already taken.",
                    CommandSetName.class.getSimpleName()));
        }
    }

    @MessageMapping("/create-room")
    public void createRoom(@Header("simpSessionId") String sessionId, CommandCreateChatRom cmd) {
        Optional.ofNullable(chat.createChatRoom(sessionId, cmd.roomName))
                .ifPresentOrElse(
                (ruuid) -> sendReplyToUser(sessionId, ServerReply.okReply(ruuid)),
                () -> sendReplyToUser(sessionId, new ServerReply(
                        ServerReply.Status.NOT_OK,
                        "Can not create the room.",CommandCreateChatRom.class.getSimpleName()))
        );
    }

    private void sendReplyToUser(String sessionId, ServerReply reply) {
        smt.convertAndSend("/topic/u-" + sessionId, reply);
    }

}

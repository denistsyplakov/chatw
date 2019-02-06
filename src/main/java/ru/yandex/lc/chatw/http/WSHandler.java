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
import ru.yandex.lc.chatw.dto.Greeting;
import ru.yandex.lc.chatw.dto.HelloMessage;

import java.util.Timer;
import java.util.TimerTask;

@Controller
public class WSHandler {

    Logger log = LoggerFactory.getLogger(WSHandler.class);

    @Autowired
    private SimpMessagingTemplate smt;

    private Timer tm = new Timer();

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(@Header("simpSessionId") String sessionId, HelloMessage message) throws Exception {
        log.info("Message from {}", sessionId);
        log.info("tpl {}",smt);
        tm.schedule(new TmpTimerTask(sessionId),1000 );
        smt.convertAndSend("/topic/greetings",new Greeting("TTTT"));
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.name) + "!");
    }

    class TmpTimerTask extends TimerTask{

        private String sessionId;

        public TmpTimerTask(String sessionId) {
            this.sessionId = sessionId;
        }

        @Override
        public void run() {
            log.info("Sending tmpt");
            smt.convertAndSend("/topic/u-" + sessionId , new Greeting("QQQ"));
        }
    }

}

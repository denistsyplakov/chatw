package ru.yandex.lc.chatw.http;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import ru.yandex.lc.chatw.dto.Greeting;
import ru.yandex.lc.chatw.dto.HelloMessage;

@Controller
public class WSHandler {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.name) + "!");
    }

}

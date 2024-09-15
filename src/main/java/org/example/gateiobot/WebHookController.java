package org.example.gateiobot;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebHookController {

    private final MyTelegramBot myTelegramBot;

    public WebHookController(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    @PostMapping("/web-hook")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return myTelegramBot.onWebhookUpdateReceived(update);
    }
}

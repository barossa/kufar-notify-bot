package by.kufar.bot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.WebhookBot;

@RestController
@RequiredArgsConstructor
public class WebhookController {
    private final WebhookBot webhookBot;

    @PostMapping("/")
    public BotApiMethod<?> updateReceived(@RequestBody Update update) {
        return webhookBot.onWebhookUpdateReceived(update);
    }
}

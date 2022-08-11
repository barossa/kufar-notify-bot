package by.kufar.bot.controller;

import by.kufar.bot.config.BotConfiguration;
import by.kufar.bot.controller.util.ChatIdResolver;
import by.kufar.bot.entity.User;
import by.kufar.bot.handler.AbstractUpdateHandler;
import by.kufar.bot.service.UpdateHandlerService;
import by.kufar.bot.service.UserService;
import by.kufar.bot.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.util.List;

@Slf4j
@Component
@Transactional
public class NotificationBot extends SpringWebhookBot {
    private final BotConfiguration configuration;
    private final UserService userService;
    private final UpdateHandlerService updateHandlerService;

    public NotificationBot(SetWebhook webhook, BotConfiguration configuration,
                           UserServiceImpl userService, UpdateHandlerService updateHandlerService,
                           List<AbstractUpdateHandler> handlers) {
        super(webhook);
        this.configuration = configuration;
        this.userService = userService;
        this.updateHandlerService = updateHandlerService;
        handlers.forEach(h -> h.setExecutorService(this::executeMethod));
        userService.setExecutorService(this::executeMethod);
    }

    @Override
    public String getBotUsername() {
        return configuration.getUsername();
    }

    @Override
    public String getBotToken() {
        return configuration.getToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        long chatId = ChatIdResolver.resolve(update);
        User user = userService.findUser(chatId);
        return updateHandlerService.handle(user, update);
    }

    @Override
    public String getBotPath() {
        return configuration.getWebhookPath();
    }

    private void executeMethod(BotApiMethod<?> method) {
        try {
            execute(method);
        } catch (Exception e) {
            log.error("Can't execute method: {}", e.getMessage(), e);
        }
    }
}

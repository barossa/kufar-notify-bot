package by.kufar.bot.bot;

import by.kufar.bot.config.BotConfiguration;
import by.kufar.bot.entity.User;
import by.kufar.bot.exception.NoSuchHandlerException;
import by.kufar.bot.handler.AbstractUpdateHandler;
import by.kufar.bot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.util.List;

@Slf4j
@Component
public class NotificationBot extends SpringWebhookBot {
    private final BotConfiguration configuration;
    private final List<AbstractUpdateHandler> handlers;
    private final UserService userService;

    public NotificationBot(SetWebhook webhook, BotConfiguration configuration,
                           List<AbstractUpdateHandler> handlers, UserService userService) {
        super(webhook);
        this.configuration = configuration;
        this.handlers = handlers;
        this.userService = userService;
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
        AbstractUpdateHandler handler = handlers.stream()
                .filter((h) -> h.getStatus().equals(user.getStatus()))
                .findFirst()
                .orElseThrow(NoSuchHandlerException::new);

        return update.hasCallbackQuery() ? handler.onCallback(user, update.getCallbackQuery()) :
                handler.onMessage(user, update.getMessage());
    }

    @Override
    public String getBotPath() {
        return configuration.getWebhookPath();
    }
}

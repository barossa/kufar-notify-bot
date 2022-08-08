package by.kufar.bot.handler;

import by.kufar.bot.entity.User;
import by.kufar.bot.entity.UserStatus;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import static by.kufar.bot.config.BotConfiguration.DEFAULT_LOCALE;

public abstract class AbstractUpdateHandler {
    protected final Object[] EMPTY_ARGS = new Object[]{};
    protected final UserStatus status;
    protected final MessageSource messageSource;

    //Handle user request with status
    protected AbstractUpdateHandler(UserStatus status, MessageSource messageSource) {
        this.status = status;
        this.messageSource = messageSource;
    }

    public abstract BotApiMethod<?> onMessage(User user, Message message);

    public abstract BotApiMethod<?> onCallback(User user, CallbackQuery query);

    public UserStatus getStatus() {
        return status;
    }

    protected String resolveMessage(Object[] args) {
        return messageSource.getMessage(status.getMessageKey(), args, DEFAULT_LOCALE);
    }
}

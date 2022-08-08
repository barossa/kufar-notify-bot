package by.kufar.bot.handler;

import by.kufar.bot.entity.User;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import static by.kufar.bot.entity.UserStatus.WELCOME;

@Component
public class WelcomeHandler extends AbstractUpdateHandler {

    public WelcomeHandler(MessageSource messageSource) {
        super(WELCOME, messageSource);
    }

    @Override
    public BotApiMethod<?> onMessage(User user, Message message) {
        return new SendMessage(user.getChatId() + "", resolveMessage(EMPTY_ARGS));
    }

    @Override
    public BotApiMethod<?> onCallback(User user, CallbackQuery query) {
        return onMessage(user, query.getMessage());
    }
}

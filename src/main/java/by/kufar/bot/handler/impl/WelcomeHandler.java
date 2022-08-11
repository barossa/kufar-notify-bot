package by.kufar.bot.handler.impl;

import by.kufar.bot.entity.User;
import by.kufar.bot.handler.AbstractUpdateHandler;
import by.kufar.bot.handler.util.MessageResolver;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;

import static by.kufar.bot.entity.UserStatus.MENU;
import static by.kufar.bot.entity.UserStatus.WELCOME;

@Component
public class WelcomeHandler extends AbstractUpdateHandler {
    public WelcomeHandler(MessageResolver messageResolver) {
        super(WELCOME, messageResolver, Collections.emptyList());
    }

    @Override
    public BotApiMethod<?> onMessage(User user, Message message) {
        String welcomeMessage = messageResolver.resolve(status.getMessageKey(), user, EMPTY_ARGS);
        SendMessage sendMessage = new SendMessage(user.getChatId() + "", welcomeMessage);
        executorService.execute(sendMessage);
        user.setStatus(MENU);
        return handlerService.handle(user, new Message());
    }

}

package by.kufar.bot.handler.impl;

import by.kufar.bot.entity.User;
import by.kufar.bot.entity.UserStatus;
import by.kufar.bot.handler.AbstractUpdateHandler;
import by.kufar.bot.handler.util.Button;
import by.kufar.bot.handler.util.KeyboardUtils;
import by.kufar.bot.handler.util.MessageResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

import static by.kufar.bot.entity.UserStatus.MENU;
import static by.kufar.bot.handler.util.Button.MY_SEARCH_REQUESTS;
import static by.kufar.bot.handler.util.Button.NEW_SEARCH;

@Slf4j
@Component
public class MenuHandler extends AbstractUpdateHandler {

    public MenuHandler(MessageResolver messageResolver) {
        super(MENU, messageResolver, List.of(MY_SEARCH_REQUESTS, NEW_SEARCH));
    }

    @Override
    public BotApiMethod<?> onMessage(User user, Message message) {
        String menuMessage = messageResolver.resolve(status.getMessageKey(), user, EMPTY_ARGS);
        SendMessage sendMessage = new SendMessage(user.getChatId() + "", menuMessage);
        List<KeyboardUtils.ButtonEntry> localizedButtons = buttons.stream()
                .map((btn) -> new KeyboardUtils.ButtonEntry(messageResolver.resolve(btn.getKey(), user)))
                .toList();
        ReplyKeyboardMarkup keyboardMarkup = KeyboardUtils.buildKeyboardMarkup(localizedButtons, 2);
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }

    @Override
    public BotApiMethod<?> onKeyboard(User user, Button button) {
        BotApiMethod<?> method;
        switch (button) {
            case NEW_SEARCH -> {
                user.setStatus(UserStatus.NEW_SEARCH);
                method = handlerService.handle(user, new Message());
            }
            case MY_SEARCH_REQUESTS -> {
                user.setStatus(UserStatus.MY_SEARCH_REQUESTS);
                method = handlerService.handle(user, new Message());
            }
            default -> {
                log.debug("Button not recognized -> Delegated to message handler");
                method = onMessage(user, new Message());
            }
        }
        return method;
    }
}

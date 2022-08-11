package by.kufar.bot.service.impl;

import by.kufar.bot.entity.User;
import by.kufar.bot.handler.AbstractUpdateHandler;
import by.kufar.bot.handler.NoSuchHandlerException;
import by.kufar.bot.handler.util.Button;
import by.kufar.bot.service.UpdateHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UpdateHandlerServiceImpl implements UpdateHandlerService {
    private final List<AbstractUpdateHandler> handlers;

    public UpdateHandlerServiceImpl(List<AbstractUpdateHandler> handlers) {
        this.handlers = handlers;
        handlers.forEach(h -> h.setHandlerService(this));
    }

    @Override
    public BotApiMethod<?> handle(User user, Update update) {
        AbstractUpdateHandler handler = findHandler(user);
        BotApiMethod<?> method;
        if (!update.hasCallbackQuery()) {
            Message message = update.getMessage();
            String text = Optional.ofNullable(message.getText()).orElse("");
            Optional<Button> button = handler.findButton(text, user);
            method = button.isPresent() ? handler.onKeyboard(user, button.get()) :
                    handler.onMessage(user, message);

        } else {
            method = handler.onCallback(user, update.getCallbackQuery());
        }
        return method;
    }

    @Override
    public BotApiMethod<?> handle(User user, Button button) {
        AbstractUpdateHandler handler = findHandler(user);
        return handler.onKeyboard(user, button);
    }

    @Override
    public BotApiMethod<?> handle(User user, Message message) {
        AbstractUpdateHandler handler = findHandler(user);
        return handler.onMessage(user, message);
    }

    @Override
    public BotApiMethod<?> handle(User user, CallbackQuery callbackQuery) {
        AbstractUpdateHandler handler = findHandler(user);
        return handler.onCallback(user, callbackQuery);
    }

    private AbstractUpdateHandler findHandler(User user) {
        return handlers.stream().filter(h -> h.getStatus().equals(user.getStatus()))
                .findFirst().orElseThrow(NoSuchHandlerException::new);
    }
}

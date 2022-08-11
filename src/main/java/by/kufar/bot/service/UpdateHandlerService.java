package by.kufar.bot.service;

import by.kufar.bot.entity.User;
import by.kufar.bot.handler.util.Button;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandlerService {
    BotApiMethod<?> handle(User user, Update update);

    BotApiMethod<?> handle(User user, Button button);

    BotApiMethod<?> handle(User user, Message message);

    BotApiMethod<?> handle(User user, CallbackQuery callbackQuery);
}

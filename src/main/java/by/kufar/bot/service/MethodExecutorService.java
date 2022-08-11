package by.kufar.bot.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

public interface MethodExecutorService {
    void execute(BotApiMethod<?> method);
}

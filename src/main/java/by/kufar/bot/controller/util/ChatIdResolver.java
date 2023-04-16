package by.kufar.bot.controller.util;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public final class ChatIdResolver {
    public static final long UNDEFINED_ID = -1;

    private ChatIdResolver() {
    }

    public static long resolve(Update update) {
        long id;
        if (update.hasCallbackQuery()) {
            id = update.getCallbackQuery().getMessage().getChatId();

        } else if (update.hasMessage()) {
            id = update.getMessage().getChatId();

        } else if (update.hasMyChatMember()){
            id = update.getMyChatMember().getChat().getId();

        }else {
            log.warn("Can't resolve chat id");
            id = UNDEFINED_ID;
        }
        return id;
    }
}

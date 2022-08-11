package by.kufar.bot.handler.util;

import by.kufar.bot.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageResolver {

    private final MessageSource messageSource;

    public String resolve(String key, User user) {
        return messageSource.getMessage(key, new Object[]{}, user.getLocale());
    }

    public String resolve(String key, User user, Object[] args) {
        return messageSource.getMessage(key, args, user.getLocale());
    }

}

package by.kufar.bot.service.impl;

import by.kufar.bot.entity.Advertisement;
import by.kufar.bot.entity.User;
import by.kufar.bot.handler.util.MessageResolver;
import by.kufar.bot.repo.UserRepository;
import by.kufar.bot.service.MethodExecutorService;
import by.kufar.bot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static by.kufar.bot.config.BotConfiguration.DEFAULT_LOCALE;
import static by.kufar.bot.controller.util.ChatIdResolver.UNDEFINED_ID;
import static by.kufar.bot.entity.UserStatus.WELCOME;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String NEW_AD_MSG_KEY = "bot.newAdvertisement";

    private final UserRepository userRepository;
    private final MessageResolver messageResolver;

    private MethodExecutorService executorService;

    @Override
    public User findUser(long chatId) {
        if (chatId == UNDEFINED_ID) {
            throw new IllegalArgumentException();
        }
        Optional<User> optionalUser = userRepository.findById(chatId);
        return optionalUser.orElseGet(() -> {
            User unsaved = new User(chatId, WELCOME, DEFAULT_LOCALE, Collections.emptySet());
            return userRepository.save(unsaved);
        });
    }

    @Override
    public void update(User user) {
        userRepository.save(user);
    }

    @Override
    public void notify(User user, List<Advertisement> advertisements) {
        for (Advertisement advertisement : advertisements) {
            String message = messageResolver.resolve(NEW_AD_MSG_KEY, user, new Object[]{advertisement.getName(), advertisement.getPrice(), advertisement.getLink()});
            executorService.execute(new SendMessage(user.getChatId() + "", message));
        }
    }

    public void setExecutorService(MethodExecutorService executorService) {
        this.executorService = executorService;
    }
}

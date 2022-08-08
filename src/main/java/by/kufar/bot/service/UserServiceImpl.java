package by.kufar.bot.service;

import by.kufar.bot.entity.User;
import by.kufar.bot.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static by.kufar.bot.bot.ChatIdResolver.UNDEFINED_ID;
import static by.kufar.bot.entity.UserStatus.WELCOME;

@Component
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findUser(long chatId) {
        if (chatId == UNDEFINED_ID) {
            throw new IllegalArgumentException();
        }
        Optional<User> optionalUser = userRepository.findById(chatId);
        User user;
        if (optionalUser.isEmpty()) {
            user = new User(chatId, WELCOME);
            userRepository.save(user);
        } else {
            user = optionalUser.get();
        }
        return user;
    }
}

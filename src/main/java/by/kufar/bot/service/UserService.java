package by.kufar.bot.service;

import by.kufar.bot.entity.Advertisement;
import by.kufar.bot.entity.User;

import java.util.List;

public interface UserService {
    User findUser(long chatId);

    void update(User user);

    void notify(User user, List<Advertisement> advertisements);
}

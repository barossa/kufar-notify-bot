package by.kufar.bot.service;

import by.kufar.bot.entity.User;

public interface UserService {
    User findUser(long chatId);
}

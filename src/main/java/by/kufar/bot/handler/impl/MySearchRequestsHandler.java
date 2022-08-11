package by.kufar.bot.handler.impl;

import by.kufar.bot.entity.SearchRequest;
import by.kufar.bot.entity.User;
import by.kufar.bot.handler.AbstractUpdateHandler;
import by.kufar.bot.handler.util.Button;
import by.kufar.bot.handler.util.KeyboardUtils;
import by.kufar.bot.handler.util.MessageResolver;
import by.kufar.bot.service.AdvertisementService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

import static by.kufar.bot.entity.UserStatus.MENU;
import static by.kufar.bot.entity.UserStatus.MY_SEARCH_REQUESTS;
import static by.kufar.bot.handler.util.Button.BACK;

@Component
public class MySearchRequestsHandler extends AbstractUpdateHandler {
    private static final String SEARCH_MSG_PATTERN = "bot.searchMessage";
    private static final String SEARCH_EMPTY_MSG_PATTERN = "bot.searchMessageEmpty";

    private final AdvertisementService advertisementService;

    protected MySearchRequestsHandler(MessageResolver messageResolver, AdvertisementService advertisementService) {
        super(MY_SEARCH_REQUESTS, messageResolver, List.of(BACK));
        this.advertisementService = advertisementService;
    }

    @Override
    public BotApiMethod<?> onMessage(User user, Message message) {
        String welcomeMsg = messageResolver.resolve(status.getMessageKey(), user);
        List<SearchRequest> userSearches = advertisementService.findUserSearches(user);
        String responseMsg;
        if (userSearches.isEmpty()) {
            responseMsg = welcomeMsg + messageResolver.resolve(SEARCH_EMPTY_MSG_PATTERN, user);
        } else {
            StringBuilder builder = new StringBuilder(welcomeMsg);
            Function<SearchRequest, String> toString = (r) -> messageResolver.resolve(SEARCH_MSG_PATTERN, user,
                    new Object[]{r.getQuery(), r.getAdvertisements().size(), r.getLastUpdated().format(DateTimeFormatter.ofPattern("hh:mm:ss", user.getLocale()))});
            userSearches.stream().map(toString).forEach(builder::append);
            responseMsg = builder.toString();
        }

        SendMessage sendMessage = new SendMessage(user.getChatId() + "", responseMsg);
        List<KeyboardUtils.ButtonEntry> localizedButtons = buttons.stream()
                .map((btn) -> new KeyboardUtils.ButtonEntry(messageResolver.resolve(btn.getKey(), user)))
                .toList();
        sendMessage.setReplyMarkup(KeyboardUtils.buildKeyboardMarkup(localizedButtons));
        return sendMessage;
    }

    @Override
    public BotApiMethod<?> onKeyboard(User user, Button button) {
        user.setStatus(MENU);
        return handlerService.handle(user, new Message());
    }
}

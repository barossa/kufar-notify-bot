package by.kufar.bot.handler.impl;

import by.kufar.bot.entity.User;
import by.kufar.bot.entity.UserStatus;
import by.kufar.bot.handler.AbstractUpdateHandler;
import by.kufar.bot.handler.util.Button;
import by.kufar.bot.handler.util.KeyboardUtils;
import by.kufar.bot.handler.util.MessageResolver;
import by.kufar.bot.service.AdvertisementService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static by.kufar.bot.entity.UserStatus.MENU;
import static by.kufar.bot.entity.UserStatus.NEW_SEARCH;
import static by.kufar.bot.handler.util.Button.*;

@Component
public class NewSearchHandler extends AbstractUpdateHandler {

    private static final String SEARCH_REQUEST_QUERY_KEY = "search_request";
    private static final String CONFIRM_SEARCH_MSG_KEY = "bot.confirmSearch";

    private final AdvertisementService advertisementService;

    public NewSearchHandler(MessageResolver messageResolver, AdvertisementService advertisementService) {
        super(NEW_SEARCH, messageResolver, List.of(SUBMIT, CANCEL, BACK));
        this.advertisementService = advertisementService;
    }

    @Override
    public BotApiMethod<?> onMessage(User user, Message message) {
        BotApiMethod<?> method;
        Set<User.PinnedData> data = user.getData();
        if (!data.contains(User.PinnedData.of(SEARCH_REQUEST_QUERY_KEY))) {
            if (message.hasText()) {
                String query = message.getText();
                user.pinData(new User.PinnedData(SEARCH_REQUEST_QUERY_KEY, query));

                long quantity = advertisementService.findQuantity(query);
                String confirmMessage = messageResolver.resolve(CONFIRM_SEARCH_MSG_KEY, user,
                        new Object[]{quantity, query});

                List<KeyboardUtils.ButtonEntry> localizedButtons = buttons.stream()
                        .map((btn) -> new KeyboardUtils.ButtonEntry(messageResolver.resolve(btn.getKey(), user)))
                        .toList();
                SendMessage sendMessage = new SendMessage(user.getChatId() + "", confirmMessage);
                sendMessage.setReplyMarkup(KeyboardUtils.buildKeyboardMarkup(localizedButtons));
                method = sendMessage;

            } else {
                method = readSearchQueryMessage(user);
            }

        } else {
            method = readSearchQueryMessage(user);
        }
        return method;
    }

    @Override
    public BotApiMethod<?> onKeyboard(User user, Button button) {
        switch (button) {
            case SUBMIT: {
                Optional<User.PinnedData> queryOptional = user.getData().stream().filter(d -> d.getKey().equals(SEARCH_REQUEST_QUERY_KEY)).findFirst();
                if (queryOptional.isPresent()) {
                    String query = queryOptional.get().getValue();
                    advertisementService.registerSearch(query, user);
                    user.setStatus(UserStatus.MY_SEARCH_REQUESTS);
                }
                break;
            }
            default: {
                user.setStatus(MENU);
            }

        }
        user.clearData();
        return handlerService.handle(user, new Message());
    }

    private SendMessage readSearchQueryMessage(User user) {
        SendMessage sendMessage = new SendMessage(user.getChatId() + "", messageResolver.resolve(status.getMessageKey(), user));
        List<KeyboardUtils.ButtonEntry> localizedButtons = Stream.of(BACK)
                .map((btn) -> new KeyboardUtils.ButtonEntry(messageResolver.resolve(btn.getKey(), user)))
                .toList();
        sendMessage.setReplyMarkup(KeyboardUtils.buildKeyboardMarkup(localizedButtons));
        return sendMessage;
    }
}

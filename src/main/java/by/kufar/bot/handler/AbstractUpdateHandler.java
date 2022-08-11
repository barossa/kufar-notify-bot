package by.kufar.bot.handler;

import by.kufar.bot.entity.User;
import by.kufar.bot.entity.UserStatus;
import by.kufar.bot.handler.util.Button;
import by.kufar.bot.handler.util.MessageResolver;
import by.kufar.bot.service.MethodExecutorService;
import by.kufar.bot.service.UpdateHandlerService;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class AbstractUpdateHandler {
    protected final Object[] EMPTY_ARGS = new Object[]{};
    protected final UserStatus status;
    protected final MessageResolver messageResolver;
    protected final List<Button> buttons;
    protected UpdateHandlerService handlerService;
    protected MethodExecutorService executorService;

    /*
     * Handle user request by status type
     * */
    protected AbstractUpdateHandler(UserStatus status, MessageResolver messageResolver, Collection<Button> buttons) {
        this.status = status;
        this.messageResolver = messageResolver;
        this.buttons = new ArrayList<>(buttons);
    }

    public abstract BotApiMethod<?> onMessage(User user, Message message);

    public BotApiMethod<?> onCallback(User user, CallbackQuery query) {
        throw new IllegalStateException("Method should be overridden to process the callback action");
    }

    public BotApiMethod<?> onKeyboard(User user, Button button) {
        throw new IllegalStateException("Method should be overridden to process the button action");
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setHandlerService(UpdateHandlerService handlerService) {
        this.handlerService = handlerService;
    }

    public void setExecutorService(MethodExecutorService executorService) {
        this.executorService = executorService;
    }

    public Optional<Button> findButton(String name, User user) {
        return buttons.stream().filter(b -> messageResolver.resolve(b.getKey(), user).equals(name)).findFirst();
    }
}

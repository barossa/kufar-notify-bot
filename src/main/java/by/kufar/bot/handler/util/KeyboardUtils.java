package by.kufar.bot.handler.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

public final class KeyboardUtils {
    private static final boolean RESIZE_REPLY_KEYBOARD_MARKUP = true;

    private KeyboardUtils() {
    }

    public static InlineKeyboardMarkup buildInlineMarkup(Collection<ButtonEntry> buttons) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (ButtonEntry button : buttons) {
            InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
            keyboardButton.setText(button.name());
            keyboardButton.setCallbackData(button.name());
            row.add(keyboardButton);
        }
        return new InlineKeyboardMarkup(Collections.singletonList(row));
    }

    public static ReplyKeyboardMarkup buildKeyboardMarkup(Collection<ButtonEntry> buttons, int... rowSizes) {
        Iterator<ButtonEntry> buttonIterator = buttons.iterator();
        List<KeyboardRow> rows = new ArrayList<>();

        for (int size : rowSizes) {
            KeyboardRow currentRow = new KeyboardRow();
            while (buttonIterator.hasNext() && currentRow.size() < size) {
                ButtonEntry button = buttonIterator.next();
                currentRow.add(button.name());
            }
            if (!currentRow.isEmpty()) {
                rows.add(currentRow);
            }
        }

        while (buttonIterator.hasNext()) {
            KeyboardRow row = new KeyboardRow();
            row.add(buttonIterator.next().name());
            rows.add(row);
        }

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(rows);
        replyKeyboardMarkup.setResizeKeyboard(RESIZE_REPLY_KEYBOARD_MARKUP);
        return replyKeyboardMarkup;
    }

    public record ButtonEntry(String name) {
    }
}

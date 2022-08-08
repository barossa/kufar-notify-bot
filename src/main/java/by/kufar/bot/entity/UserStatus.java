package by.kufar.bot.entity;

public enum UserStatus {
    WELCOME("bot.welcome");

    private final String messageKey;

    UserStatus(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}

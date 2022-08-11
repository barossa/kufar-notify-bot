package by.kufar.bot.entity;

public enum UserStatus {
    WELCOME("bot.welcome"),
    MENU("bot.selectSomething"),
    NEW_SEARCH("bot.newSearch"),
    MY_SEARCH_REQUESTS("bot.mySearchRequests");

    private final String messageKey;

    UserStatus(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}

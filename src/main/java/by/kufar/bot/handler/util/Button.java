package by.kufar.bot.handler.util;

public enum Button {
    MY_SEARCH_REQUESTS("buttons.mySearchRequests"),
    NEW_SEARCH("buttons.newSearch"),
    BACK("buttons.back"),
    SUBMIT("buttons.submit"),
    CANCEL("buttons.cancel");
    private final String key;

    Button(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
package by.kufar.bot.handler;

public class NoSuchHandlerException extends RuntimeException {
    public NoSuchHandlerException() {
    }

    public NoSuchHandlerException(String s) {
        super(s);
    }
}

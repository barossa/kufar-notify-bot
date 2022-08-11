package by.kufar.bot.service;

public interface WebhookService {
    void registerWebhook(String token, String path);

    String findActualWebhook(String token);
}

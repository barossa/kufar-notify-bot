package by.kufar.bot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

import java.util.Locale;

@Configuration
@Getter
public class BotConfiguration {
    private static final String LOCALE_LANG = "ru";
    private static final String LOCALE_COUNTRY = "RU";
    public static final Locale DEFAULT_LOCALE = new Locale(LOCALE_LANG, LOCALE_COUNTRY);

    @Value("${telegramBots.username}")
    private String username;
    @Value("${telegramBots.token}")
    private String token;
    @Value("${telegramBots.webhookPath}")
    private String webhookPath;

    @Bean
    public SetWebhook setWebhook() {
        return new SetWebhook(webhookPath);
    }
}

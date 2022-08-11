package by.kufar.bot;

import by.kufar.bot.config.BotConfiguration;
import by.kufar.bot.service.WebhookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class BotApp {
    public static void main(String[] args) {
        SpringApplication.run(BotApp.class, args);
    }

    @Bean
    CommandLineRunner manageWebhook(WebhookService service, BotConfiguration configuration) {
        return args -> {
            if (configuration.isRegisterWebhook()) {
                String actualWebhook = service.findActualWebhook(configuration.getToken());
                if (!actualWebhook.equals(configuration.getWebhookPath())) {
                    log.info("Webhook is outdated! Trying to register new one...");
                    service.registerWebhook(configuration.getToken(), configuration.getWebhookPath());
                    log.info("Webhook \"{}\" registered successfully!", configuration.getWebhookPath());
                } else {
                    log.info("Webhook path is up to date!");
                }
            }
        };
    }
}

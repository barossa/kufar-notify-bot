package by.kufar.bot.config;

import by.kufar.bot.service.impl.UpdateAdvertisementsJob;
import lombok.Getter;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

import java.util.Locale;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

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

    @Value("${telegramBots.registerWebhook}")
    private boolean registerWebhook;
    @Value("${telegramBots.updateIntervalMinutes}")
    private int interval;

    @Bean
    public SetWebhook setWebhook() {
        return new SetWebhook(webhookPath);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public JobDetailFactoryBean jobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(UpdateAdvertisementsJob.class);
        jobDetailFactory.setDescription("Invoke update advertisements job...");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }
    @Bean
    public Trigger trigger(JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
                .withSchedule(simpleSchedule().repeatForever().withIntervalInMinutes(interval))
                .build();
    }
}

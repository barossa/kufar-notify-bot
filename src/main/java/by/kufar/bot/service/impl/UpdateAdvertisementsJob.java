package by.kufar.bot.service.impl;

import by.kufar.bot.service.AdvertisementService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class UpdateAdvertisementsJob implements Job {
    private final AdvertisementService advertisementService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        advertisementService.updateSearchResults();
    }
}

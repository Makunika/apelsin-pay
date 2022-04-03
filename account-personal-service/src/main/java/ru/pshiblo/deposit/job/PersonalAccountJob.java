package ru.pshiblo.deposit.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.pshiblo.deposit.core.domain.PersonalAccount;
import ru.pshiblo.deposit.core.services.PersonalAccountService;
import ru.pshiblo.deposit.job.process.PersonalAccountProcessor;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalAccountJob {

    private final List<PersonalAccountProcessor> depositProcessors;
    private final PersonalAccountService service;

    @Scheduled(cron = "0 0 0 * * *")
    public void generatePercent() {
        log.info("Start generate percent");
        LocalDate now = LocalDate.now();
        log.info(now.toString());
        List<PersonalAccount> accounts = service.getByStartWorkDay(now.getDayOfMonth());

        for (PersonalAccount account : accounts) {
            log.info("process " + account.toString());
            for (PersonalAccountProcessor depositProcessor : depositProcessors) {
                depositProcessor.process(account);
            }
        }

    }
}

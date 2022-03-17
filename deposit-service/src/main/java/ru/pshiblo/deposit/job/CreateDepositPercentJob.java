package ru.pshiblo.deposit.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.domain.Deposit;
import ru.pshiblo.account.service.DepositService;
import ru.pshiblo.deposit.job.process.DepositProcessor;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateDepositPercentJob {

    private final List<DepositProcessor> depositProcessors;
    private final DepositService service;

    @Scheduled(cron = "0 0 0 * * *")
    public void generatePercent() {
        log.info("Start generate percent");
        LocalDate now = LocalDate.now();
        log.info(now.toString());
        List<Deposit> deposits = service.getByDayStartDepositDate(now.getDayOfMonth());

        for (Deposit deposit : deposits) {
            log.info("process " + deposit.toString());
            for (DepositProcessor depositProcessor : depositProcessors) {
                depositProcessor.process(deposit);
            }
        }

    }
}

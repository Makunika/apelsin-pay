package ru.pshiblo.transaction.rabbit.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.transaction.domain.Account;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.exceptions.TransactionNotAllowedException;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
import ru.pshiblo.transaction.repository.TransactionRepository;
import ru.pshiblo.transaction.service.interfaces.AccountService;
import ru.pshiblo.transaction.service.interfaces.CardService;

/**
 * @author Maxim Pshiblo
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OpenTransactionListener {

    private final RabbitTemplate rabbitTemplate;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CardService cardService;


    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.OPEN_ROUTE,
                    value = @Queue(RabbitConsts.OPEN_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    @Transactional
    public void openTransaction(@Payload Transaction transaction) {
        log.info("trans open!");
        log.info(transaction.toString());
        transaction.setStatus(TransactionStatus.OPENED);

        Account account = accountService.getByNumber(transaction.getFromNumber());

        if (account.getLock()) {
            throw new TransactionNotAllowedException("Account is lock");
        }

        if (account.getBalance().compareTo(transaction.getMoney()) > 0) {
            throw new TransactionNotAllowedException("Balance small");
        }

        transaction = transactionRepository.save(transaction);

        //if may be ban - on approve!!!!

        transaction.setStatus(TransactionStatus.APPROVED);

        if (!transactionRepository.existsByStatusAndId(TransactionStatus.CANCELED, transaction.getId())) {
            //TODO: save to history
            transactionRepository.save(transaction);
            rabbitTemplate.convertAndSend(RabbitConsts.COMMISSION_ROUTE, transaction);
        }
    }
}

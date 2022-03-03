package ru.pshiblo.transaction.rabbit.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.transaction.domain.Account;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.Currency;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.exceptions.TransactionNotAllowedException;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
import ru.pshiblo.transaction.repository.TransactionRepository;
import ru.pshiblo.transaction.service.interfaces.AccountService;
import ru.pshiblo.transaction.service.interfaces.CardService;

import javax.transaction.NotSupportedException;

/**
 * @author Maxim Pshiblo
 */
@Service
@RequiredArgsConstructor
public class SendTransactionListener {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final CardService cardService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.SEND_ROUTE,
                    value = @Queue(RabbitConsts.SEND_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    @Transactional
    public synchronized void sendTransaction(@Payload Transaction transaction) throws NotSupportedException {
        transaction = transactionRepository.findById(transaction.getId()).orElseThrow(NotFoundException::new);

        if (transaction.getStatus() == TransactionStatus.COMMISSION) {
            if (transaction.isInner()) {
                Account toAccount = transaction.isToCard() ?
                        cardService.getByNumber(transaction.getToNumber()).getAccount() :
                        accountService.getByNumber(transaction.getToNumber());

                Account fromAccount = accountService.getByNumber(transaction.getFromNumber());

                Currency transactionCurrency = transaction.getCurrency();

                //TODO: support Currency

                if (!transactionRepository.existsByStatusAndId(TransactionStatus.CANCELED, transaction.getId())) {
                    if (accountService.getById(fromAccount.getId()).getBalance().compareTo(transaction.getMoney().add(transaction.getCommission())) < 0) {
                        toAccount.setBalance(accountService.getById(toAccount.getId()).getBalance().add(transaction.getMoney()));
                        accountService.save(toAccount);
                        fromAccount.setBalance(accountService.getById(fromAccount.getId()).getBalance().subtract(transaction.getMoney().add(transaction.getCommission())));
                        accountService.save(fromAccount);

                        transaction.setStatus(TransactionStatus.SENT);
                        transaction = transactionRepository.save(transaction);

                        rabbitTemplate.convertAndSend(RabbitConsts.CLOSE_ROUTE, transaction);
                    } else {
                        throw new TransactionNotAllowedException();
                    }
                }
            } else {
                throw new NotSupportedException("only is inner");
            }
        }
    }
}

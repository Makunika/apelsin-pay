package ru.pshiblo.payment.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pshiblo.payment.clients.model.OpenPaymentInnerDto;
import ru.pshiblo.payment.clients.model.Transaction;

@FeignClient(name = "transaction-service")
public interface TransactionClient {

    @PostMapping("/api/transaction/payment/inner")
    Transaction openPaymentInner(@RequestBody OpenPaymentInnerDto request);
}

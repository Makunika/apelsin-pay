package ru.pshiblo.payment.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pshiblo.payment.clients.model.OpenPaymentInnerDto;
import ru.pshiblo.payment.clients.model.OpenPaymentTinkoffDto;
import ru.pshiblo.payment.clients.model.Transaction;

import javax.validation.Valid;

@FeignClient(name = "transaction-service")
public interface TransactionClient {

    @PostMapping("/api/transaction/payment/inner")
    Transaction openPaymentInner(@RequestBody OpenPaymentInnerDto request);

    @PostMapping("/api/transaction/payment/tinkoff")
    Transaction openPaymentTinkoff(@Valid @RequestBody OpenPaymentTinkoffDto request);

    @PostMapping("api/transaction/success/redirect/{id}")
    void confirmTransaction(@PathVariable int id);
}

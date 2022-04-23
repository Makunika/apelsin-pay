package ru.pshiblo.transaction.tinkoff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pshiblo.transaction.tinkoff.model.request.TinkoffPayment;
import ru.pshiblo.transaction.tinkoff.model.response.TinkoffStatusPayment;

@FeignClient(name = "tinkoff", url = "https://business.tinkoff.ru/openapi/sandbox/secured")
public interface TinkoffApi {
    @PostMapping("/api/v1/payment/ruble-transfer/pay")
    void paymentTo(@RequestBody TinkoffPayment tinkoffPayment);

    @GetMapping("/api/v1/payment/{id}")
    TinkoffStatusPayment getStatusPayment(@PathVariable long id);
}

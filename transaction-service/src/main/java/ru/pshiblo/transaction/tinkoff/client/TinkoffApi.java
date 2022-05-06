package ru.pshiblo.transaction.tinkoff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pshiblo.transaction.tinkoff.config.TinkoffApiConfig;
import ru.pshiblo.transaction.tinkoff.model.request.TinkoffInvoicingCancelOrConfirm;
import ru.pshiblo.transaction.tinkoff.model.request.TinkoffInvoicingCreate;
import ru.pshiblo.transaction.tinkoff.model.request.TinkoffPayment;
import ru.pshiblo.transaction.tinkoff.model.response.TinkoffInvoicingResponse;
import ru.pshiblo.transaction.tinkoff.model.response.TinkoffInvoicingStatusResponse;
import ru.pshiblo.transaction.tinkoff.model.response.TinkoffStatusPayment;

@FeignClient(
        contextId = "tinkoffId",
        name = "tinkoff",
        url = "https://business.tinkoff.ru/openapi/sandbox",
        configuration = TinkoffApiConfig.class
)
public interface TinkoffApi {
    @PostMapping("/secured/api/v1/payment/ruble-transfer/pay")
    void paymentTo(@RequestBody TinkoffPayment tinkoffPayment);

    @GetMapping("/secured/api/v1/payment/{id}")
    TinkoffStatusPayment getStatusPayment(@PathVariable("id") long id);

    @PostMapping("/api/v1/e-invoice")
    TinkoffInvoicingResponse createInvoicing(@RequestBody TinkoffInvoicingCreate request);

    @GetMapping("/api/v1/e-invoice/{id}/status")
    TinkoffInvoicingStatusResponse getStatusInvoicing(@PathVariable("id") String id);

    @PostMapping("/api/v1/e-invoice/{id}/confirm")
    void confirmInvoicing(@RequestBody TinkoffInvoicingCancelOrConfirm request, @PathVariable("id") String id);

    @PostMapping("/api/v1/e-invoice/{id}/cancel")
    void cancelInvoicing(@RequestBody TinkoffInvoicingCancelOrConfirm request, @PathVariable("id") String id);
}

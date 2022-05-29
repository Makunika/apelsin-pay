package ru.pshiblo.payment.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.pshiblo.payment.clients.model.BusinessAccount;

@FeignClient(name = "account-business-service")
public interface AccountBusinessClient {
    @GetMapping("/api/business/company/{companyId}")
    BusinessAccount getByCompanyId(@PathVariable("companyId") Long companyId);
}

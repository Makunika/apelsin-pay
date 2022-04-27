package ru.pshiblo.payment.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "account-business-service")
public interface AccountBusinessClient {
}

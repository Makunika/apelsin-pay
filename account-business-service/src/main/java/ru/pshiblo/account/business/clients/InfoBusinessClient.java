package ru.pshiblo.account.business.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "info-business-service")
public interface InfoBusinessClient {

}

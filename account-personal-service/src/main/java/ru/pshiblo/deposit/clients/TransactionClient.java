package ru.pshiblo.deposit.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.pshiblo.deposit.model.MoneyEdit;
import ru.pshiblo.deposit.model.Transaction;

@FeignClient(name = "transaction-service")
public interface TransactionClient {

    @RequestMapping(method = RequestMethod.POST, value = "/transactions/api/transaction/admin/edit/money", consumes = MediaType.APPLICATION_JSON_VALUE)
    Transaction editMoneyToAccount(@RequestBody MoneyEdit moneyEdit);
}

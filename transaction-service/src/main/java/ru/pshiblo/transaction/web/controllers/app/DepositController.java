package ru.pshiblo.transaction.web.controllers.app;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.common.protocol.transaction.AccountInfo;
import ru.pshiblo.common.protocol.transaction.CardInfo;
import ru.pshiblo.common.protocol.transaction.DepositInfo;

import javax.transaction.NotSupportedException;
import java.util.List;

/**
 * @author Maxim Pshiblo
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("app/money/deposit")
public class DepositController {

    @SneakyThrows
    @GetMapping("user/{userId}")
    public List<DepositInfo> getDepositsByUserId(@PathVariable Integer userId) {
        throw new NotSupportedException();
    }
    @SneakyThrows
    @GetMapping("number/{number}")
    public DepositInfo getDepositByNumber(@PathVariable Integer number) {
        throw new NotSupportedException();
    }
    @SneakyThrows
    @GetMapping("{id}")
    public DepositInfo getDepositById(@PathVariable Integer id) {
        throw new NotSupportedException();
    }
    @SneakyThrows
    @DeleteMapping("{id}")
    public void blockDeposit(@PathVariable Integer id) {
        throw new NotSupportedException();
    }


}

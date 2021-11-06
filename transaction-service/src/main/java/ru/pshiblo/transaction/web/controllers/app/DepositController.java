package ru.pshiblo.transaction.web.controllers.app;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.common.protocol.transaction.AccountInfo;
import ru.pshiblo.common.protocol.transaction.CardInfo;
import ru.pshiblo.common.protocol.transaction.DepositInfo;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("app/money/deposit")
public class DepositController {

    @GetMapping("user/{userId}")
    public List<DepositInfo> getDepositsByUserId(@PathVariable Integer userId) {

    }

    @GetMapping("number/{number}")
    public DepositInfo getDepositByNumber(@PathVariable Integer number) {

    }

    @GetMapping("{id}")
    public DepositInfo getDepositById(@PathVariable Integer id) {

    }

    @DeleteMapping("{id}")
    public void blockDeposit(@PathVariable Integer id) {

    }


}

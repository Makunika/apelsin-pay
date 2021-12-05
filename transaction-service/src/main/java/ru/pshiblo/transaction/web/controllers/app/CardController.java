package ru.pshiblo.transaction.web.controllers.app;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.common.protocol.transaction.AccountInfo;
import ru.pshiblo.common.protocol.transaction.CardInfo;

import javax.transaction.NotSupportedException;
import java.util.List;

/**
 * @author Maxim Pshiblo
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("app/money/card")
public class CardController {
    @SneakyThrows
    @GetMapping("user/{userId}")
    public List<CardInfo> getCardsByUserId(@PathVariable Integer userId) {
        throw new NotSupportedException();
    }
    @SneakyThrows
    @GetMapping("number/{number}")
    public CardInfo getCardByNumber(@PathVariable Integer number) {
        throw new NotSupportedException();
    }
    @SneakyThrows
    @GetMapping("{id}")
    public CardInfo getCardById(@PathVariable Integer id) {
        throw new NotSupportedException();
    }
    @SneakyThrows
    @DeleteMapping("{id}")
    public void blockCard(@PathVariable Integer id) {
        throw new NotSupportedException();
    }


}

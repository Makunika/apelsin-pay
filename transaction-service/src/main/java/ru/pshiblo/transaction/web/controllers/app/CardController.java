package ru.pshiblo.transaction.web.controllers.app;

import lombok.RequiredArgsConstructor;
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

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("app/money/card")
public class CardController {

    @GetMapping("user/{userId}")
    public List<CardInfo> getCardsByUserId(@PathVariable Integer userId) {

    }

    @GetMapping("number/{number}")
    public CardInfo getCardByNumber(@PathVariable Integer number) {

    }

    @GetMapping("{id}")
    public CardInfo getCardById(@PathVariable Integer id) {

    }

    @DeleteMapping("{id}")
    public void blockCard(@PathVariable Integer id) {

    }


}

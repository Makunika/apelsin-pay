package ru.pshiblo.transaction.service.interfaces;

import ru.pshiblo.transaction.domain.Account;
import ru.pshiblo.transaction.domain.Card;
import ru.pshiblo.transaction.enums.Currency;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
public interface CardService {
    Card createCard(Card card, Currency currency);
    Card createCard(Card card, Account account);
    List<Card> getByUserId(int userId);
    Card getById(int id);
    Card getByNumber(String number);
    boolean checkByPin(String number, int pin);
    boolean checkByCvc(String number, int cvc);
}

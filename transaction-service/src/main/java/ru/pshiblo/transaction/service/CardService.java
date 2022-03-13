package ru.pshiblo.transaction.service;

import ru.pshiblo.security.model.AuthUser;
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
    void changePin(String numberCard, String newPin, AuthUser authUser);
    String getCvcByNumber(String number, AuthUser authUser);
    void blockCard(Integer id, AuthUser authUser);
    void blockAccountCard(Integer id, AuthUser authUser);
}
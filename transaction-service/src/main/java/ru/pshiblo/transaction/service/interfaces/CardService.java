package ru.pshiblo.transaction.service.interfaces;

import ru.pshiblo.transaction.domain.Card;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
public interface CardService {
    Card createCard(int userId);
    List<Card> getByUserId(int userId);
    Card getById(int id);
    Card getByNumber(int number);
}

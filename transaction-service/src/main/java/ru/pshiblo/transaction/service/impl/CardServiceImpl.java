package ru.pshiblo.transaction.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.pshiblo.common.exception.NotAllowedOperationException;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.security.model.AuthUser;
import ru.pshiblo.transaction.domain.Account;
import ru.pshiblo.transaction.domain.Card;
import ru.pshiblo.transaction.enums.AccountType;
import ru.pshiblo.transaction.enums.Currency;
import ru.pshiblo.transaction.repository.CardRepository;
import ru.pshiblo.transaction.service.AccountService;
import ru.pshiblo.transaction.service.CardService;
import ru.pshiblo.transaction.utils.RandomGenerator;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Maxim Pshiblo
 */
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final AccountService accountService;
    private final CardRepository cardRepository;

    @Value("${bank.bik}")
    private String bankBik;

    @Override
    public Card createCard(Card card, Currency currency) {
        return createCard(card, accountService.create(card.getUserId(), currency, AccountType.CARD));
    }

    @Override
    public Card createCard(Card card, Account account) {

        //send notify to email maybe

        Card newCard = new Card();
        newCard.setAccount(account);
        newCard.setExpired(LocalDate.now().plusYears(4));
        newCard.setCvc(Integer.parseInt(RandomGenerator.randomNumber(3)));
        newCard.setPin(RandomGenerator.randomNumber(4));
        newCard.setTypePay(card.getTypePay());
        newCard.setType(card.getType());
        newCard.setUserId(card.getUserId());
        newCard.setLockCard(false);
        String currentBik = card.getTypePay().getNumber() + bankBik;
        StringBuilder number;
        do {
            number = new StringBuilder(currentBik);
            number.append(RandomGenerator.randomNumber(6));
        } while (cardRepository.existsByNumber(number.toString()));
        newCard.setNumber(number.toString());

        return cardRepository.save(newCard);
    }

    @Override
    public List<Card> getByUserId(int userId) {
        return cardRepository.findByUserId(userId);
    }

    @Override
    public Card getById(int id) {
        return cardRepository.findById(id).orElseThrow(() -> new NotFoundException(id, Card.class));
    }

    @Override
    public Card getByNumber(String number) {
        return cardRepository.findByNumber(number).orElseThrow(() -> new NotFoundException("Card with number " + number + " not found"));
    }

    @Override
    public boolean checkByPin(String number, int pin) {
        Card card = getByNumber(number);
        return Integer.parseInt(card.getPin()) == pin;
    }

    @Override
    public boolean checkByCvc(String number, int cvc) {
        Card card = getByNumber(number);
        return card.getCvc() == cvc;
    }

    @Override
    public void changePin(String numberCard, String newPin, AuthUser authUser) {
        Card card = getByNumber(numberCard);

        if (!card.getUserId().equals(((int) authUser.getId())) || newPin.length() != 4) {
            throw new NotAllowedOperationException("Forbidden");
        }

        card.setPin(newPin);
        cardRepository.save(card);
    }

    @Override
    public String getCvcByNumber(String number, AuthUser authUser) {
        Card card = getByNumber(number);

        if (!card.getUserId().equals(((int) authUser.getId()))) {
            throw new NotAllowedOperationException("Forbidden");
        }

        return card.getCvc().toString();
    }

    @Override
    public void blockCard(Integer id, AuthUser authUser) {
        Card card = getById(id);

        if (!card.getUserId().equals(((int) authUser.getId()))) {
            throw new NotAllowedOperationException("Forbidden");
        }

        card.setLockCard(true);
        cardRepository.save(card);
    }

    @Override
    public void blockAccountCard(Integer id, AuthUser authUser) {
        Card card = getById(id);

        if (!card.getUserId().equals(((int) authUser.getId()))) {
            throw new NotAllowedOperationException("Forbidden");
        }

        card.setLockCard(true);
        card.getAccount().setLock(true);
        cardRepository.save(card);
    }
}

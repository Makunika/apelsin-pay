package ru.pshiblo.transaction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.transaction.domain.Account;
import ru.pshiblo.transaction.domain.Card;
import ru.pshiblo.transaction.enums.Currency;
import ru.pshiblo.transaction.repository.CardRepository;
import ru.pshiblo.transaction.service.interfaces.AccountService;
import ru.pshiblo.transaction.service.interfaces.CardService;
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

    @Value("bank.bik")
    private String bankBik;

    @Override
    public Card createCard(Card card, Currency currency) {
        return createCard(card, accountService.create(card.getUserId(), currency));
    }

    @Override
    public Card createCard(Card card, Account account) {

        //send notify to email maybe

        Card newCard = new Card();
        newCard.setAccount(account);
        newCard.setExpired(LocalDate.now().plusYears(4));
        newCard.setCvc(Integer.parseInt(RandomGenerator.randomNumber(4)));
        newCard.setPin(RandomGenerator.randomNumber(5));
        newCard.setTypePay(card.getTypePay());
        newCard.setType(card.getType());
        newCard.setUserId(card.getUserId());
        newCard.setLock(false);
        
        String currentBik = card.getTypePay().getNumber() + bankBik;
        StringBuilder number;
        do {
            number = new StringBuilder(currentBik);
            number.append(RandomGenerator.randomNumber(9));
            number.append(8);
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
}

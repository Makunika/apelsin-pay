package ru.pshiblo.transaction.web.controllers.app;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.security.AuthUtils;
import ru.pshiblo.transaction.mappers.CardMapper;
import ru.pshiblo.transaction.service.CardService;
import ru.pshiblo.transaction.web.dto.request.ChangePinCardDto;
import ru.pshiblo.transaction.web.dto.request.CreateCardDto;
import ru.pshiblo.transaction.web.dto.response.CardResponseDto;

import java.util.Collection;

/**
 * @author Maxim Pshiblo
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/card")
public class CardController {

    private final CardService cardService;
    private final CardMapper cardMapper;

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping
    public CardResponseDto createNew(@RequestBody CreateCardDto createCardDto) {
        createCardDto.setUserId(((int) AuthUtils.getUserId()));
        return cardMapper
                .toDto(
                        cardService.createCard(
                                cardMapper.toEntity(createCardDto), createCardDto.getCurrency()
                        )
                );
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PutMapping("/pin")
    public void changePin(@RequestBody ChangePinCardDto changePinCardDto) {
        cardService.changePin(changePinCardDto.getNumberCard(), changePinCardDto.getNewPin(), AuthUtils.getAuthUser());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping()
    public Collection<CardResponseDto> getCardsByUserId() {
        return cardMapper.toDtos(cardService.getByUserId((int) AuthUtils.getUserId()));
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("{number}")
    public String getCvcByNumber(@PathVariable String number) {
        return cardService.getCvcByNumber(number, AuthUtils.getAuthUser());
    }

    @PreAuthorize("hasAuthority('SCOPE_server')")
    @GetMapping("{id}")
    public CardResponseDto getCardById(@PathVariable Integer id) {
        return cardMapper.toDto(cardService.getById(id));
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @DeleteMapping("{id}")
    public void blockCard(@PathVariable Integer id) {
        cardService.blockCard(id, AuthUtils.getAuthUser());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @DeleteMapping("account/{id}")
    public void blockAccountAndAll(@PathVariable Integer id) {
        cardService.blockAccountCard(id, AuthUtils.getAuthUser());
    }


}

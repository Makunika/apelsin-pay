package ru.pshiblo.transaction.web.controllers.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.security.AuthUtils;
import ru.pshiblo.transaction.service.TransactionService;
import ru.pshiblo.transaction.web.dto.request.InfoPrepareRequestDto;
import ru.pshiblo.transaction.model.InfoPrepare;

@RestController
@RequestMapping("/api/prepare/info")
@RequiredArgsConstructor
public class AccountInfoController {

    private final TransactionService service;

    @PreAuthorize("hasAuthority('scope_USER')")
    @PostMapping
    public InfoPrepare getInfoPrepare(@RequestBody InfoPrepareRequestDto rq) {
        return service.getPrepareInfo(rq.getToNumber(), rq.getFromNumber(), rq.getMoney(), rq.getCurrency(), AuthUtils.getUserId());
    }

}

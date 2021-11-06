package ru.pshiblo.common.protocol.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Maxim Pshiblo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardInfo {
    private Integer id;
    private Integer userId;
    private Integer number;
    private String type;
    private String typePay;
    private AccountInfo accountInfo;
    private Integer cvc;
    private LocalDate expired;
    private Boolean lock;
}

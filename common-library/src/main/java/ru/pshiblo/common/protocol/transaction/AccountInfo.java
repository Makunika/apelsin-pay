package ru.pshiblo.common.protocol.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Maxim Pshiblo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {
    private Integer id;
    private Integer number;
    private String type;
    private BigDecimal balance;
    private Boolean lock;
    private String currency;
}

package ru.pshiblo.common.protocol.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Maxim Pshiblo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositInfo {
    private Integer id;
    private Integer userId;
    private Integer number;
    private String type;
    private AccountInfo accountInfo;
}

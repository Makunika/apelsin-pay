package ru.pshiblo.transaction.tinkoff.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TinkoffTo {
    private String name;
    private String inn;
    private String kpp;
    private String bik;
    private String bankName;
    private String corrAccountNumber;
    private String accountNumber;
}

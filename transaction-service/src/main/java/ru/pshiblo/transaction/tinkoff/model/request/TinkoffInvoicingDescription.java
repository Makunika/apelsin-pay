package ru.pshiblo.transaction.tinkoff.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TinkoffInvoicingDescription {
    @JsonProperty("short")
    private String shortName;
    private String full;
}

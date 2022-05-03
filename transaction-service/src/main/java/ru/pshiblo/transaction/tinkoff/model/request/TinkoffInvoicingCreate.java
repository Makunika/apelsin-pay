package ru.pshiblo.transaction.tinkoff.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
public class TinkoffInvoicingCreate {
    private String orderId;
    private BigDecimal amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-dd'T'HH:mm:ss'+03:00'")
    private Date endDate;
    private TinkoffInvoicingDescription description;
    private String redirectUrl;
}


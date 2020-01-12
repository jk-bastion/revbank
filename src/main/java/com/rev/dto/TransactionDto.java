package com.rev.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class TransactionDto {

    private long transactionId;

    @JsonProperty(required = true)
    private long fromAccountId;

    @JsonProperty(required = true)
    private long toAccountId;

    @JsonProperty(required = true)
    private BigDecimal amount;

    @JsonProperty(required = true)
    private String currencyCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date date;

    private String status;

    private String message;

    public TransactionDto() {
    }

    public TransactionDto(long transactionId, long fromAccountId, long toAccountId, BigDecimal amount, String currencyCode,
                          Date date, String status, String message) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
        this.date = date;
    }
}

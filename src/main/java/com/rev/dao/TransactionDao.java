package com.rev.dao;

import com.rev.common.TransactionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@Entity(name = "transaction")
@Table(name = "transaction")
public class TransactionDao {

    @Id
    @GeneratedValue
    private Long transactionId;

    @NotNull(message = "Source account id is required")
    @Column(name= "fromAccountId")
    private Long fromAccountId;

    @NotNull(message = "Destination account id is required")
    @Column(name = "toAccountId")
    private Long toAccountId;

    @NotNull(message = "Transaction amount is required.")
    @Column(name ="amount")
    private BigDecimal amount;

    @NotNull(message = "Currency code is required")
    @Column(name = "currencyCode")
    private String currencyCode;

    @NotNull(message = "Transaction status is required")
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name ="date")
    private java.sql.Date date;

    @Column(name ="message")
    private String message;

    public TransactionDao() {
    }

    public TransactionDao(Long transactionId, Long fromAccountId, Long toAccountId, BigDecimal amount, String currencyCode, TransactionStatus status,
                          java.sql.Date date, String message) {
        this.transactionId = transactionId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.status = status;
        this.date = date;
        this.message = message;
    }
}

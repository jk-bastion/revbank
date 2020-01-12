package com.rev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class AccountDto {

    private long accountId;

    @JsonProperty(required = true)
    private String username;

    @JsonProperty(required = true)
    private String email;

    @JsonProperty(required = true)
    private BigDecimal balance;

    @JsonProperty(required = true)
    private String currencyCode;

    public AccountDto() {
    }

    public AccountDto(long accountId, String username, String email, BigDecimal balance, String currencyCode) {
        this.accountId = accountId;
        this.username = username;
        this.email = email;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }
}

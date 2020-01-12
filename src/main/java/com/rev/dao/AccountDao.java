package com.rev.dao;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@Entity(name = "account")
@Table(name = "account")
public class AccountDao {
    @Id
    @GeneratedValue
    private long accountId;

    @NotNull(message = "username is required")
    @Column(name="username")
    private String username;

    @Email(message = "invalid email")
    @Column(name="email", unique = true)
    private String email;

    @Column(name= "balance")
    private BigDecimal balance;

    @Column(name= "currencyCode")
    private String currencyCode;

    public AccountDao() {
    }

    public AccountDao(long accountId, String username, String email, BigDecimal balance, String currencyCode) {
        this.accountId = accountId;
        this.username = username;
        this.email = email;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }
}

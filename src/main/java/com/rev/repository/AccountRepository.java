package com.rev.repository;

import com.rev.common.exception.AccountBalanceUpdateException;
import com.rev.dao.AccountDao;

import java.util.List;

public interface AccountRepository {

    AccountDao createAccount(AccountDao accountDao);
    List<AccountDao> getAllAccounts();
    AccountDao findAccountById(Long accountId);
    void deleteAccount(Long accountId);
    void updateAccountBalance(AccountDao accountDao) throws AccountBalanceUpdateException;
}

package com.rev.repository;

import com.rev.common.exception.NotEnoughBalanceException;
import com.rev.dao.AccountDao;
import com.rev.dao.TransactionDao;

import java.util.List;

public interface TransactionRepository {

    TransactionDao addTransaction(AccountDao accountFrom, AccountDao accountTo, TransactionDao transactionDao) throws NotEnoughBalanceException;

    void addTransaction(TransactionDao transactionDao);

    List<TransactionDao> getTransactionsForAccount(Long accountId);

}

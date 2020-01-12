package com.rev.server;

import com.google.inject.Inject;
import com.rev.common.ErrorsCode;
import com.rev.common.TransactionStatus;
import com.rev.common.exception.AccountNotExistsException;
import com.rev.common.exception.InvalidCurrencyException;
import com.rev.common.exception.NotEnoughBalanceException;
import com.rev.dao.AccountDao;
import com.rev.dao.TransactionDao;
import com.rev.dto.TransactionDto;
import com.rev.repository.AccountRepository;
import com.rev.repository.AccountRepositoryImpl;
import com.rev.repository.TransactionRepository;
import com.rev.repository.TransactionRepositoryImpl;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.rev.common.ErrorsCode.*;

public class TransactionServer {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Inject
    public TransactionServer(TransactionRepositoryImpl transactionRepository, AccountRepositoryImpl accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public void addTransaction(final TransactionDto transactionDto) throws Exception {

        AccountDao accountFrom = getAndValidateAccount(transactionDto.getFromAccountId(), SRC_ACCOUNT_NOT_EXISTS);
        AccountDao accountTo = getAndValidateAccount(transactionDto.getToAccountId(), DES_ACCOUNT_NOT_EXISTS);

        if (!(accountFrom.getCurrencyCode().equals(transactionDto.getCurrencyCode()) && accountTo.getCurrencyCode().equals(accountFrom.getCurrencyCode()))) {
            transactionRepository.addTransaction(getTransactionDao(transactionDto, INVALID_CURRENCY.getMessage(), TransactionStatus.FAILED));
            throw new InvalidCurrencyException(INVALID_CURRENCY.getMessage());
        }

        if (!(accountFrom.getBalance().compareTo(transactionDto.getAmount()) >= 0)) {
            transactionRepository.addTransaction(accountFrom, accountTo, getTransactionDao(transactionDto, NOT_ENOUGH_BALANCE.getMessage(), TransactionStatus.FAILED));
            throw new NotEnoughBalanceException(NOT_ENOUGH_BALANCE.getMessage());
        }
        TransactionDao transactionDao = transactionRepository.addTransaction(accountFrom, accountTo, getTransactionDao(transactionDto, "", TransactionStatus.SUCCESS));

        if (transactionDao.getTransactionId() == null) {
            if (NOT_ENOUGH_BALANCE.getMessage().equals(transactionDao.getMessage())) {
                transactionDao.setMessage(UNEXPECTED_ERROR.getMessage());
                transactionDao.setStatus(TransactionStatus.FAILED);
            }
            transactionRepository.addTransaction(transactionDao);
        }
    }

    private TransactionDao getTransactionDao(TransactionDto transactionDto,  String message, TransactionStatus status) {
        return TransactionDao.builder()
                    .fromAccountId(transactionDto.getFromAccountId())
                    .toAccountId(transactionDto.getToAccountId())
                    .amount(transactionDto.getAmount())
                    .currencyCode(transactionDto.getCurrencyCode())
                    .message(message)
                    .date(new Date(Instant.now().toEpochMilli()))
                    .status(status)
                    .build();
    }

    private AccountDao getAndValidateAccount(final Long accountId, final ErrorsCode errorsCode) throws AccountNotExistsException {
        AccountDao accountFrom = accountRepository.findAccountById(accountId);
        if (accountFrom.getAccountId() == 0) {
            throw new AccountNotExistsException(errorsCode.getMessage());
        }
        return accountFrom;
    }

    public List<TransactionDto> getTransactionsForAccount(final Long accountId) throws AccountNotExistsException {
        if (accountRepository.findAccountById(accountId).getAccountId() == 0) {
            throw new AccountNotExistsException(ACCOUNT_NOT_EXISTS.getMessage());
        }

        return transactionRepository.getTransactionsForAccount(accountId)
                .stream()
                .map(transactionDao -> TransactionDto.builder()
                                                    .transactionId(transactionDao.getTransactionId())
                                                    .fromAccountId(transactionDao.getFromAccountId())
                                                    .toAccountId(transactionDao.getToAccountId())
                                                    .amount(transactionDao.getAmount())
                                                    .currencyCode(transactionDao.getCurrencyCode())
                                                    .status(transactionDao.getStatus().name())
                                                    .date(transactionDao.getDate())
                                                    .message(transactionDao.getMessage())
                                                    .build())
                .collect(Collectors.toList());
    }
}

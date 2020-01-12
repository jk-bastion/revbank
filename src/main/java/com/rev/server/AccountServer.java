package com.rev.server;

import com.rev.common.ErrorsCode;
import com.rev.common.exception.AccountBalanceUpdateException;
import com.rev.common.exception.AccountCreationException;
import com.rev.common.exception.AccountNotExistsException;
import com.rev.dao.AccountDao;
import com.rev.dto.AccountDto;
import com.rev.repository.AccountRepository;
import com.rev.repository.AccountRepositoryImpl;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static com.rev.common.ErrorsCode.ACCOUNT_NOT_EXISTS;
import static com.rev.common.ErrorsCode.ACCOUNT_UPDATE_BALANCE_FAILED;

public class AccountServer {

    private final AccountRepository accountRepository;

    @Inject
    public AccountServer(AccountRepositoryImpl accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountDto createAccount(AccountDto accountDto) throws AccountCreationException {
        try {
            return mapAccountDaoToAccountDto(accountRepository.createAccount(createAccountDao(accountDto)));
        } catch (Exception ex) {
            throw new AccountCreationException(ErrorsCode.ACCOUNT_CREATION_FAILED.getMessage());
        }
    }

    public List<AccountDto> getAllAccounts() {
        return accountRepository.getAllAccounts()
                .stream()
                .map(accountDao -> mapAccountDaoToAccountDto(accountDao))
                .collect(Collectors.toList());
    }

    public AccountDto findAccountById(Long accountId) throws AccountNotExistsException {
        AccountDao accountDao = accountRepository.findAccountById(accountId);
        validateIfAccountExists(accountDao);
        return mapAccountDaoToAccountDto(accountDao);
    }

    public void updateAccountBalance(AccountDto accountDto) throws AccountNotExistsException, AccountBalanceUpdateException {
        AccountDao accountDao = accountRepository.findAccountById(accountDto.getAccountId());
        validateIfAccountExists(accountDao);
        accountDao.setBalance(accountDto.getBalance());
        try {
            accountRepository.updateAccountBalance(accountDao);
        } catch (Exception exception) {
            throw new AccountBalanceUpdateException(ACCOUNT_UPDATE_BALANCE_FAILED.getMessage());
        }
    }

    public void deleteAccount(long accountId) throws AccountNotExistsException {
        AccountDao accountDao = accountRepository.findAccountById(accountId);
        validateIfAccountExists(accountDao);
        accountRepository.deleteAccount(accountId);
    }

    private void validateIfAccountExists(AccountDao accountDao) throws AccountNotExistsException {
        if (accountDao.getAccountId() == 0) {
            throw new AccountNotExistsException(ACCOUNT_NOT_EXISTS.getMessage());
        }
    }

    private AccountDao createAccountDao(AccountDto accountDto) {
        return AccountDao.builder()
                .email(accountDto.getEmail())
                .username(accountDto.getUsername())
                .currencyCode(accountDto.getCurrencyCode())
                .balance(accountDto.getBalance())
                .build();
    }

    private AccountDto mapAccountDaoToAccountDto(AccountDao accountDao) {
        return AccountDto.builder()
                .accountId(accountDao.getAccountId())
                .username(accountDao.getUsername())
                .email(accountDao.getEmail())
                .balance(accountDao.getBalance())
                .currencyCode(accountDao.getCurrencyCode())
                .build();
    }
}

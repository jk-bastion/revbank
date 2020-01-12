package com.rev.server;

import com.rev.common.exception.AccountBalanceUpdateException;
import com.rev.common.exception.AccountCreationException;
import com.rev.common.exception.AccountNotExistsException;
import com.rev.dao.AccountDao;
import com.rev.dto.AccountDto;
import com.rev.repository.AccountRepositoryImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static com.rev.common.ErrorsCode.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountServerTest {

    @InjectMocks
    private AccountServer accountServer;

    @Mock
    private AccountRepositoryImpl accountRepository;

    private static final String USERNAME = "testusername";
    private static final String EMAIL = "test@email.com";
    private static final String CURRENCY_CODE = "EUR";
    private static final long ACCOUNT_ID = 1l;
    private static final BigDecimal BALANCE = new BigDecimal(10);

    @Test
    public void shouldCreateAccountSuccessfully() throws AccountCreationException {
        // given
        AccountDao accountDao = createAccountDao();
        when(accountRepository.createAccount(any(AccountDao.class))).thenReturn(accountDao);
        AccountDto accountDto = createAccountDto();

        // when
        AccountDto createdAccountDto = accountServer.createAccount(accountDto);

        // then
        accountDto.setAccountId(accountDao.getAccountId());
        verify(accountRepository).createAccount(any(AccountDao.class));
        assertThat(createdAccountDto).isEqualTo(accountDto);
    }

    @Test
    public void shouldThrownAccountCreationExceptionWhenCreatingAccountFailed() {
        // given
        when(accountRepository.createAccount(any(AccountDao.class))).thenThrow(new RuntimeException());
        AccountDto accountDto = createAccountDto();

        // when && then
        try {
            accountServer.createAccount(accountDto);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(AccountCreationException.class);
            assertThat(e.getMessage()).isEqualTo(ACCOUNT_CREATION_FAILED.getMessage());
            verify(accountRepository).createAccount(any(AccountDao.class));
        }
    }

    @Test
    public void shouldGetAllAccounts() {
        // given
        given(accountRepository.getAllAccounts()).willReturn(List.of(
                AccountDao.builder().accountId(1l).build(),
                AccountDao.builder().accountId(2l).build(),
                AccountDao.builder().accountId(3l).build()));

        // when
        List<AccountDto> accountList = accountServer.getAllAccounts();

        // then
        assertThat(accountList).hasSize(3);
        assertThat(accountList).contains(
                AccountDto.builder().accountId(1l).build(),
                AccountDto.builder().accountId(2l).build(),
                AccountDto.builder().accountId(3l).build());
    }

    @Test
    public void shouldFindAccountById() throws AccountNotExistsException {
        // given
        AccountDao accountDao = createAccountDao();
        given(accountRepository.findAccountById(ACCOUNT_ID)).willReturn(accountDao);

        // when
        AccountDto accountDto = accountServer.findAccountById(ACCOUNT_ID);

        // then
        verify(accountRepository).findAccountById(ACCOUNT_ID);
        AccountDto accountDto1 = createAccountDto();
        accountDto1.setAccountId(ACCOUNT_ID);
        assertThat(accountDto).isEqualTo(accountDto1);
    }

    @Test
    public void shouldThrownAccountNotExistExceptionWhenFindAccountByIdAndAccountDoesNotExist() {
        // given
        given(accountRepository.findAccountById(2l)).willReturn(new AccountDao());

        // when && then
        try {
            accountServer.findAccountById(2l);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(AccountNotExistsException.class);
            assertThat(e.getMessage()).isEqualTo(ACCOUNT_NOT_EXISTS.getMessage());
        }
    }

    @Test
    public void shouldDeleteAccount() throws AccountNotExistsException {
        // given
        AccountDao accountDao = createAccountDao();
        given(accountRepository.findAccountById(ACCOUNT_ID)).willReturn(accountDao);

        // when
        accountServer.deleteAccount(ACCOUNT_ID);

        // then
        verify(accountRepository).deleteAccount(ACCOUNT_ID);
        verify(accountRepository).findAccountById(ACCOUNT_ID);
    }

    @Test
    public void shouldThrowAccountNotExistWhenDeletingAccountAndAccountDoesNotExist() {
        // given
        given(accountRepository.findAccountById(ACCOUNT_ID)).willReturn(new AccountDao());

        // when && then
        try {
            accountServer.deleteAccount(ACCOUNT_ID);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(AccountNotExistsException.class);
            assertThat(e.getMessage()).isEqualTo(ACCOUNT_NOT_EXISTS.getMessage());
            verify(accountRepository, never()).deleteAccount(ACCOUNT_ID);
            verify(accountRepository).findAccountById(ACCOUNT_ID);
        }
    }

    @Test
    public void shouldUpdateAccountBalance() throws AccountNotExistsException, AccountBalanceUpdateException {
        // given
        AccountDao accountDao = createAccountDao();
        given(accountRepository.findAccountById(ACCOUNT_ID)).willReturn(accountDao);
        AccountDto accountDto = createAccountDto();
        accountDto.setAccountId(ACCOUNT_ID);
        accountDto.setBalance(new BigDecimal(10));

        // when
        accountServer.updateAccountBalance(accountDto);

        // then
        accountDao.setBalance(new BigDecimal(10));
        verify(accountRepository).updateAccountBalance(accountDao);
        verify(accountRepository).findAccountById(ACCOUNT_ID);
    }

    @Test
    public void shouldThrowAccountBalanceUpdatExceptionWhenUpdateAccountBalanceFailed() {
        // given
        AccountDao accountDao = createAccountDao();
        given(accountRepository.findAccountById(ACCOUNT_ID)).willReturn(accountDao);
        doThrow(new RuntimeException()).when(accountRepository).updateAccountBalance(accountDao);
        AccountDto accountDto = createAccountDto();
        accountDto.setAccountId(ACCOUNT_ID);
        accountDto.setBalance(new BigDecimal(10));

        // when && then
        try {
            accountServer.updateAccountBalance(accountDto);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(AccountBalanceUpdateException.class);
            assertThat(e.getMessage()).isEqualTo(ACCOUNT_UPDATE_BALANCE_FAILED.getMessage());
            accountDao.setBalance(new BigDecimal(10));
            verify(accountRepository).updateAccountBalance(accountDao);
            verify(accountRepository).findAccountById(ACCOUNT_ID);
        }
    }

    private AccountDto createAccountDto() {
        return AccountDto.builder()
                .username(USERNAME)
                .balance(BALANCE)
                .currencyCode(CURRENCY_CODE)
                .email(EMAIL)
                .build();
    }

    private AccountDao createAccountDao() {
        return AccountDao.builder()
                .accountId(ACCOUNT_ID)
                .balance(BALANCE)
                .currencyCode(CURRENCY_CODE)
                .email(EMAIL)
                .username(USERNAME)
                .build();
    }

}
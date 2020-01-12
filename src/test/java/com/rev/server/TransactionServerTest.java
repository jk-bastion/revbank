package com.rev.server;


import com.rev.common.ErrorsCode;
import com.rev.common.TransactionStatus;
import com.rev.common.exception.AccountNotExistsException;
import com.rev.common.exception.InvalidCurrencyException;
import com.rev.common.exception.NotEnoughBalanceException;
import com.rev.dao.AccountDao;
import com.rev.dao.TransactionDao;
import com.rev.dto.TransactionDto;
import com.rev.repository.AccountRepositoryImpl;
import com.rev.repository.TransactionRepositoryImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.util.List;

import static com.rev.common.ErrorsCode.DES_ACCOUNT_NOT_EXISTS;
import static com.rev.common.ErrorsCode.NOT_ENOUGH_BALANCE;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class TransactionServerTest {

    @InjectMocks
    private TransactionServer transactionServer;

    @Mock
    private AccountRepositoryImpl accountRepository;

    @Mock
    private TransactionRepositoryImpl transactionRepository;

    private static final long ACCOUNT_ID = 1l;
    private static final String CURRENCY_CODE = "EUR";

    @Test
    public void shouldReturnAllTransactionForGivenAccount() throws AccountNotExistsException {
        // given
        Date date = new Date(Instant.now().toEpochMilli());
        given(accountRepository.findAccountById(ACCOUNT_ID)).willReturn(AccountDao.builder().accountId(ACCOUNT_ID).build());
        given(transactionRepository.getTransactionsForAccount(ACCOUNT_ID)).willReturn(
                List.of(TransactionDao.builder()
                                .transactionId(1l)
                                .fromAccountId(ACCOUNT_ID)
                                .toAccountId(3l)
                                .date(date)
                                .message("ok")
                                .status(TransactionStatus.SUCCESS)
                                .currencyCode(CURRENCY_CODE)
                                .build(),
                        TransactionDao.builder()
                                .transactionId(2l)
                                .fromAccountId(ACCOUNT_ID)
                                .toAccountId(2l)
                                .date(date)
                                .message("failed")
                                .status(TransactionStatus.FAILED)
                                .currencyCode(CURRENCY_CODE)
                                .build()));
        // when
        List<TransactionDto> transactionList = transactionServer.getTransactionsForAccount(ACCOUNT_ID);

        // then
        assertThat(transactionList).hasSize(2);
        assertThat(transactionList).contains(TransactionDto.builder()
                        .transactionId(1l)
                        .fromAccountId(ACCOUNT_ID)
                        .toAccountId(3l)
                        .message("ok")
                        .currencyCode(CURRENCY_CODE)
                        .status(TransactionStatus.SUCCESS.name())
                        .date(date)
                        .build(),
                TransactionDto.builder()
                        .transactionId(2l)
                        .fromAccountId(ACCOUNT_ID)
                        .toAccountId(2l)
                        .message("failed")
                        .currencyCode(CURRENCY_CODE)
                        .status(TransactionStatus.FAILED.name())
                        .date(date)
                        .build());
    }

    @Test
    public void shouldThrowAccountNotExistExceptionWhenGettingAllTransactionForGivenAccountAndAccountDoesNotExist() {

        // giben
        given(accountRepository.findAccountById(ACCOUNT_ID)).willReturn(new AccountDao());
        // when && then
        try {
            transactionServer.getTransactionsForAccount(ACCOUNT_ID);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(AccountNotExistsException.class);
            assertThat(e.getMessage()).isEqualTo(ErrorsCode.ACCOUNT_NOT_EXISTS.getMessage());
            verify(accountRepository).findAccountById(ACCOUNT_ID);
            verify(transactionRepository, never()).getTransactionsForAccount(ArgumentMatchers.anyLong());
            verify(transactionRepository, never()).addTransaction(any(TransactionDao.class));
        }
    }

    @Test
    public void shouldAddTransaction() throws Exception {
        // given
        TransactionDto transactionDto = TransactionDto.builder()
                .fromAccountId(ACCOUNT_ID)
                .toAccountId(2l)
                .currencyCode(CURRENCY_CODE)
                .amount(new BigDecimal(2))
                .build();

        AccountDao sourceAccountDao = AccountDao.builder()
                .accountId(ACCOUNT_ID)
                .currencyCode(CURRENCY_CODE)
                .balance(new BigDecimal(10))
                .build();
        given(accountRepository.findAccountById(ACCOUNT_ID)).willReturn(sourceAccountDao);

        AccountDao destinationAccountDao = AccountDao.builder()
                .accountId(2l)
                .currencyCode(CURRENCY_CODE)
                .balance(new BigDecimal(2))
                .build();
        given(accountRepository.findAccountById(2l)).willReturn(destinationAccountDao);
        given(transactionRepository.addTransaction(any(AccountDao.class), any(AccountDao.class), any(TransactionDao.class))).willReturn(new TransactionDao());

        // when
        transactionServer.addTransaction(transactionDto);

        // then
        verify(accountRepository).findAccountById(ACCOUNT_ID);
        verify(accountRepository).findAccountById(2l);
        verify(transactionRepository).addTransaction(any(AccountDao.class), any(AccountDao.class), any(TransactionDao.class));
    }

    @Test
    public void shouldNotAddTransactionWhenSourceAccountDoesNotExist() throws Exception {
        // given
        TransactionDto transactionDto = TransactionDto.builder()
                .fromAccountId(ACCOUNT_ID)
                .build();
        given(accountRepository.findAccountById(ACCOUNT_ID)).willReturn(new AccountDao());

        // when && then
        try {
            transactionServer.addTransaction(transactionDto);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(AccountNotExistsException.class);
            assertThat(e.getMessage()).isEqualTo(ErrorsCode.SRC_ACCOUNT_NOT_EXISTS.getMessage());
            verify(accountRepository).findAccountById(ACCOUNT_ID);
            verify(accountRepository, never()).findAccountById(2l);
            verify(transactionRepository, never()).addTransaction(any(TransactionDao.class));
            verify(transactionRepository, never()).addTransaction(any(AccountDao.class), any(AccountDao.class), any(TransactionDao.class));
        }
    }

    @Test
    public void shouldNotAddTransactionWhenDestinationAccountDoesNotExist() throws Exception {
        // given
        TransactionDto transactionDto = TransactionDto.builder()
                .fromAccountId(ACCOUNT_ID)
                .toAccountId(2l)
                .build();
        given(accountRepository.findAccountById(ACCOUNT_ID)).willReturn(AccountDao.builder().accountId(ACCOUNT_ID).build());
        given(accountRepository.findAccountById(2l)).willReturn(new AccountDao());

        // when && then
        try {
            transactionServer.addTransaction(transactionDto);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(AccountNotExistsException.class);
            assertThat(e.getMessage()).isEqualTo(DES_ACCOUNT_NOT_EXISTS.getMessage());
            verify(accountRepository).findAccountById(ACCOUNT_ID);
            verify(accountRepository).findAccountById(2l);
            verify(transactionRepository, never()).addTransaction(any(TransactionDao.class));
            verify(transactionRepository, never()).addTransaction(any(AccountDao.class), any(AccountDao.class), any(TransactionDao.class));
        }
    }

    @Test
    public void shouldAddFailedTransactionWhenCurrencyIsDifferent() throws Exception {
        // given
        TransactionDto transactionDto = TransactionDto.builder()
                .fromAccountId(ACCOUNT_ID)
                .toAccountId(2l)
                .currencyCode("USD")
                .amount(new BigDecimal(2))
                .build();

        AccountDao sourceAccountDao = AccountDao.builder()
                .accountId(ACCOUNT_ID)
                .currencyCode(CURRENCY_CODE)
                .balance(new BigDecimal(10))
                .build();
        given(accountRepository.findAccountById(ACCOUNT_ID)).willReturn(sourceAccountDao);

        AccountDao destinationAccountDao = AccountDao.builder()
                .accountId(2l)
                .currencyCode(CURRENCY_CODE)
                .balance(new BigDecimal(2))
                .build();
        given(accountRepository.findAccountById(2l)).willReturn(destinationAccountDao);

        // when && then
        try {
            transactionServer.addTransaction(transactionDto);
            Assert.fail();
        } catch (Exception e) {
            // then
            assertThat(e).isInstanceOf(InvalidCurrencyException.class);
            assertThat(e.getMessage()).isEqualTo(ErrorsCode.INVALID_CURRENCY.getMessage());
            verify(accountRepository).findAccountById(ACCOUNT_ID);
            verify(accountRepository).findAccountById(2l);
            verify(transactionRepository).addTransaction(any(TransactionDao.class));
            verify(transactionRepository, never()).addTransaction(any(AccountDao.class), any(AccountDao.class), any(TransactionDao.class));
        }
    }

    @Test
    public void shouldAddFailedTransactionWhenNotEnoughBalanceOnSourceAccount() throws Exception {
        // given
        TransactionDto transactionDto = TransactionDto.builder()
                .fromAccountId(ACCOUNT_ID)
                .toAccountId(2l)
                .currencyCode(CURRENCY_CODE)
                .amount(new BigDecimal(2))
                .build();

        AccountDao sourceAccountDao = AccountDao.builder()
                .accountId(ACCOUNT_ID)
                .currencyCode(CURRENCY_CODE)
                .balance(new BigDecimal(1))
                .build();
        given(accountRepository.findAccountById(ACCOUNT_ID)).willReturn(sourceAccountDao);

        AccountDao destinationAccountDao = AccountDao.builder()
                .accountId(2l)
                .currencyCode(CURRENCY_CODE)
                .balance(new BigDecimal(2))
                .build();
        given(accountRepository.findAccountById(2l)).willReturn(destinationAccountDao);

        // when && then
        try {
            transactionServer.addTransaction(transactionDto);
            Assert.fail();
        } catch (Exception e) {
            // then
            assertThat(e).isInstanceOf(NotEnoughBalanceException.class);
            assertThat(e.getMessage()).isEqualTo(ErrorsCode.NOT_ENOUGH_BALANCE.getMessage());
            verify(accountRepository).findAccountById(ACCOUNT_ID);
            verify(accountRepository).findAccountById(2l);
            verify(transactionRepository, never()).addTransaction(any(TransactionDao.class));
            verify(transactionRepository).addTransaction(any(AccountDao.class), any(AccountDao.class), any(TransactionDao.class));
        }
    }

    @Test
    public void shouldAddFailedTransactionWhenSomethingWentWrongOnDbSite() throws Exception {
        // given
        TransactionDto transactionDto = TransactionDto.builder()
                .fromAccountId(ACCOUNT_ID)
                .toAccountId(4l)
                .currencyCode(CURRENCY_CODE)
                .amount(new BigDecimal(2))
                .build();

        AccountDao sourceAccountDao = AccountDao.builder()
                .accountId(ACCOUNT_ID)
                .currencyCode(CURRENCY_CODE)
                .balance(new BigDecimal(6))
                .build();
        given(accountRepository.findAccountById(ACCOUNT_ID)).willReturn(sourceAccountDao);

        AccountDao destinationAccountDao = AccountDao.builder()
                .accountId(4l)
                .currencyCode(CURRENCY_CODE)
                .balance(new BigDecimal(2))
                .build();
        given(accountRepository.findAccountById(4l)).willReturn(destinationAccountDao);
        given(transactionRepository.addTransaction(any(AccountDao.class), any(AccountDao.class), any(TransactionDao.class)))
                .willReturn(TransactionDao.builder().message(NOT_ENOUGH_BALANCE.getMessage()).build());

        // when
        transactionServer.addTransaction(transactionDto);

        // then
        verify(accountRepository).findAccountById(ACCOUNT_ID);
        verify(accountRepository).findAccountById(4l);
        verify(transactionRepository).addTransaction(any(AccountDao.class), any(AccountDao.class), any(TransactionDao.class));
        verify(transactionRepository).addTransaction(any(TransactionDao.class));
    }
}

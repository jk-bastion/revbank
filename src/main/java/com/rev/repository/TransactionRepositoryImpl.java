package com.rev.repository;


import com.google.inject.Inject;
import com.rev.common.ErrorsCode;
import com.rev.common.TransactionStatus;
import com.rev.common.exception.NotEnoughBalanceException;
import com.rev.dao.AccountDao;
import com.rev.dao.TransactionDao;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

@Slf4j
public class TransactionRepositoryImpl implements TransactionRepository {

    private static final String TRANSACTION_TABLE = "transaction";
    private final EntityManager entityManager;

    @Inject
    public TransactionRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public TransactionDao addTransaction(AccountDao accountFrom, AccountDao accountTo, TransactionDao transactionDao) throws NotEnoughBalanceException {
        log.info("Starting account transaction");
        try {
            entityManager.getTransaction().begin();
            log.info("checking balance, from Account balance {} amount to subtruct {}", accountFrom.getBalance(), transactionDao.getAmount());

            entityManager.refresh(accountFrom);
            log.info("refreshing accountFrom {}", accountFrom.getBalance());
            if (accountFrom.getBalance().compareTo(transactionDao.getAmount()) >= 0) {
                accountFrom.setBalance(accountFrom.getBalance().subtract(transactionDao.getAmount()));
                entityManager.refresh(accountTo);
                accountTo.setBalance(accountTo.getBalance().add(transactionDao.getAmount()));

                log.info("proceed transaction");
                entityManager.persist(accountFrom);
                entityManager.persist(accountTo);
                entityManager.persist(transactionDao);
                entityManager.getTransaction().commit();
                log.info("ending transaction");
            } else {
                log.info("not enough money on source account");
                transactionDao.setStatus(TransactionStatus.FAILED);
                transactionDao.setMessage(ErrorsCode.NOT_ENOUGH_BALANCE.name());
            }
        } catch (Exception exception) {
            log.info("{}", exception.getMessage());
            entityManager.getTransaction().rollback();
        }
        return transactionDao;
    }


    public void addTransaction(TransactionDao transactionDao) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(transactionDao);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            log.info("{}", e.getMessage());
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public List<TransactionDao> getTransactionsForAccount(final Long accountId) {

        try {
            Query query = entityManager.createQuery("from " + TRANSACTION_TABLE + " where fromAccountId = ?1");
            query.setParameter(1, accountId);
            return (List<TransactionDao>) query.getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}

package com.rev.repository;

import com.rev.dao.AccountDao;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

@Slf4j
public class AccountRepositoryImpl implements AccountRepository {

    private static final String ACCOUNT_TABLE = "account";
    private final EntityManager entityManager;

    @Inject
    public AccountRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public AccountDao createAccount(AccountDao accountDao) {

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(accountDao);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            log.info("{}", e.getMessage());
            entityManager.getTransaction().rollback();
            throw e;
        }
        return accountDao;
    }

    @Override
    public void updateAccountBalance(AccountDao accountDao) {
        try {
            entityManager.getTransaction().begin();
            log.info("start updating account id {} balance to {}", accountDao.getAccountId(), accountDao.getBalance());
            Query query = entityManager.createQuery("update " + ACCOUNT_TABLE + " set balance=?1 where accountId=?2");
            query.setParameter(1, accountDao.getBalance());
            query.setParameter(2, accountDao.getAccountId());
            query.executeUpdate();
            entityManager.getTransaction().commit();
            log.info("finish updating account id {} balance to {}", accountDao.getAccountId(), accountDao.getBalance());
        } catch (Exception e) {
            log.info("{}", e.getMessage());
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public List<AccountDao> getAllAccounts() {
        try {
            Query query = entityManager.createQuery("from " + ACCOUNT_TABLE);
            return (List<AccountDao>) query.getResultList();
        } catch (Exception ex) {
            log.info("{}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public AccountDao findAccountById(Long accountId) {
        try {
            Query query = entityManager.createQuery("from " + ACCOUNT_TABLE + " where accountId = ?1");
            query.setParameter(1, accountId);
            return (AccountDao) query.getSingleResult();
        } catch (Exception ex) {
            log.info("{}", ex.getMessage());
            return new AccountDao();
        }
    }

    @Override
    public void deleteAccount(Long accountId) {
        try {
            entityManager.getTransaction().begin();
            Query query = entityManager.createQuery("delete from " + ACCOUNT_TABLE + " where accountId = ?1");
            query.setParameter(1, accountId);
            query.executeUpdate();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            log.info("{}", e.getMessage());
            entityManager.getTransaction().rollback();
        }
    }
}

package com.revolut.transfers.account.infrastructure;

import com.revolut.transfers.account.config.EntityManagerProvider;
import com.revolut.transfers.account.domain.Account;
import com.revolut.transfers.account.domain.AccountId;
import com.revolut.transfers.account.domain.AccountRepository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.Optional;

import static com.revolut.transfers.account.infrastructure.TransactionUtil.returnTransactionResult;

public class HibernateAccountRepository implements AccountRepository {

    public HibernateAccountRepository(EntityManagerProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
        returnTransactionResult(() -> getEntityManager().createNativeQuery("CREATE SEQUENCE ACCOUNT_SEQ").executeUpdate());
    }

    @Override
    public Optional<Account> findById(AccountId accountId) {
        EntityManager manager = getEntityManager();
        Account retrieved = manager.find(Account.class, accountId);
        return Optional.ofNullable(retrieved);
    }

    @Override
    public void add(Account p) {
        EntityManager manager = getEntityManager();
        manager.persist(p);
    }

    @Override
    public void delete(Account account) {
        EntityManager manager = getEntityManager();
        account = manager.merge(account);
        manager.remove(account);
    }

    @Override
    public AccountId nextId() {
        BigInteger newId = (BigInteger) getEntityManager().createNativeQuery("SELECT NEXTVAL('ACCOUNT_SEQ') as account_id").getSingleResult();
        return AccountId.exisitingId(newId.longValue());

    }

    private EntityManager getEntityManager() {
        return sessionProvider.session();
    }

    private EntityManagerProvider sessionProvider;
}



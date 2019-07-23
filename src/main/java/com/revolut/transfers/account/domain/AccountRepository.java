package com.revolut.transfers.account.domain;

import java.util.Optional;

public interface AccountRepository {

    AccountId nextId();

    Optional<Account> findById(AccountId accountId);

    void add(Account p);

    void delete(Account accountId);
}

package com.revolut.transfers.account.domain;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findById(AccountId accountId);

    void add(Account p);

    AccountId nextId();
}

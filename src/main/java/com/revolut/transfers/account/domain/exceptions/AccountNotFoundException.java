package com.revolut.transfers.account.domain.exceptions;

import com.revolut.transfers.account.domain.AccountId;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(AccountId from) {
        super(String.format("account %s: not found",from));
    }
}

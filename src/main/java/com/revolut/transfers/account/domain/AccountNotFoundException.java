package com.revolut.transfers.account.domain;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(AccountId from) {
        super(String.format("account %s: not found",from));
    }
}

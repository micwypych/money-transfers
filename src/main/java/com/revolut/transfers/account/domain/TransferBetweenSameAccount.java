package com.revolut.transfers.account.domain;

public class TransferBetweenSameAccount extends RuntimeException {
    public TransferBetweenSameAccount(AccountId from) {
        super(String.format("Transfer between same account: %s", from));
    }
}

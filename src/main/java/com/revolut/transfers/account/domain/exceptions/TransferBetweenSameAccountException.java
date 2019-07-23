package com.revolut.transfers.account.domain.exceptions;

import com.revolut.transfers.account.domain.AccountId;

public class TransferBetweenSameAccountException extends RuntimeException {
    public TransferBetweenSameAccountException(AccountId from) {
        super(String.format("Transfer between same account: %s", from));
    }
}

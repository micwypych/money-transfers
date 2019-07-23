package com.revolut.transfers.account.domain.exceptions;

import com.revolut.transfers.account.domain.AccountId;

import javax.money.MonetaryAmount;

public class NotEnoghFoundException extends RuntimeException {
    public NotEnoghFoundException(AccountId accountId, MonetaryAmount requested, MonetaryAmount balance) {
        super(String.format(
                "Not enough founds on the account: %s. Requested amount: %s. Current balance: %s",
                accountId,
                requested,
                balance));
    }
}

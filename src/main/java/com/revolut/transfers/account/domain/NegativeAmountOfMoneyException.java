package com.revolut.transfers.account.domain;

import javax.money.MonetaryAmount;

public class NegativeAmountOfMoneyException extends IllegalArgumentException {
    public NegativeAmountOfMoneyException(MonetaryAmount amount) {
        super(String.format("The money amount cannot be negative: %s", amount));
    }
}

package com.revolut.transfers.account.domain.exceptions;

import com.revolut.transfers.account.domain.AccountId;

import javax.money.CurrencyUnit;

public class MontetaryAmountRequiresConversionException extends RuntimeException {
    public MontetaryAmountRequiresConversionException(AccountId accountId, CurrencyUnit requested, CurrencyUnit accountCurrency) {
        super(String.format(
                "Account id: %s. Requested currency: %s does not match the account's currency: %s.",
                accountId,
                requested,
                accountCurrency));
    }
}

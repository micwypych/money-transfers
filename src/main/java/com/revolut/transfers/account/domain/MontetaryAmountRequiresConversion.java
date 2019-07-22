package com.revolut.transfers.account.domain;

import javax.money.CurrencyUnit;

public class MontetaryAmountRequiresConversion extends RuntimeException {
    public MontetaryAmountRequiresConversion(AccountId accountId, CurrencyUnit requested, CurrencyUnit accountCurrency) {
        super(String.format(
                "Account id: %s. Requested currency: %s does not match the account's currency: %s.",
                accountId,
                requested,
                accountCurrency));
    }
}

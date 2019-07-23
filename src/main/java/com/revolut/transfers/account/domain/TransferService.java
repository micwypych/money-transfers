package com.revolut.transfers.account.domain;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;

public interface TransferService {
    void transfer(AccountId from, AccountId to, MonetaryAmount amount);

    Account createAccount(CurrencyUnit accountCurrencyUnit);

    Account createAccountWithInitialBalance(CurrencyUnit accountCurrencyUnit, BigDecimal initialBalance);

    Account retriveAccount(AccountId accountId);

    void deleteAccount(AccountId accountId);
}

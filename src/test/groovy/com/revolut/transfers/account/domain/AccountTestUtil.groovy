package com.revolut.transfers.account.domain

import org.javamoney.moneta.Money

import javax.money.Monetary

class AccountTestUtil {
    static Account newAccount(long id, String currencyUnit) {
        return new Account(AccountId.exisitingId(id), Monetary.getCurrency(currencyUnit))
    }

    static Account newAccountWithInitialBalance(long id, String currencyUnit, BigDecimal initialAmmount) {
        def currency = Monetary.getCurrency(currencyUnit)
        Account account = new Account(AccountId.exisitingId(id), currency)
        account.deposit(Money.of(initialAmmount, currency))
        return account
    }
}

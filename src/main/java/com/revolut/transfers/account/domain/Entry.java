package com.revolut.transfers.account.domain;

import com.google.common.base.Objects;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
@Immutable
public class Entry {

    public Entry(AccountId accountId, MonetaryAmount amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public MonetaryAmount getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entry entry = (Entry) o;
        return Objects.equal(accountId, entry.accountId) &&
                Objects.equal(amount, entry.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountId, amount);
    }

    private AccountId accountId;
    @Columns(columns = {@Column(name = "currency"), @Column(name = "amount")})
    @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmountAndCurrency")
    private MonetaryAmount amount;

    Entry() {
        accountId = AccountId.newId();
        amount = Money.of(0, "PLN");
    }

    void setAccountId(AccountId anAccountId) {
        this.accountId = anAccountId;
    }

    void setAmount(MonetaryAmount anAmount) {
        this.amount = anAmount;
    }
}

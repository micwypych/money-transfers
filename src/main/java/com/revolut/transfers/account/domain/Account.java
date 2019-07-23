package com.revolut.transfers.account.domain;

import com.google.common.base.Objects;
import com.revolut.transfers.account.domain.exceptions.MontetaryAmountRequiresConversionException;
import com.revolut.transfers.account.domain.exceptions.NegativeAmountOfMoneyException;
import com.revolut.transfers.account.domain.exceptions.NotEnoghFoundException;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "accounts")
@Entity
public class Account {

    public Account(AccountId id, CurrencyUnit currency) {
        this.id = id;
        this.balance = Money.zero(currency);
        this.entries = new ArrayList<>();
    }

    public void deposit(MonetaryAmount amount) {
        checkIfOperationCurrencyMatchesAccountCurrency(amount);
        checkIfPositiveAmount(amount);

        Entry depositEntry = new Entry(getId(), amount);
        entries.add(depositEntry);
        balance = balance.add(amount);
    }

    public void withdraw(MonetaryAmount amount) {
        checkIfOperationCurrencyMatchesAccountCurrency(amount);
        checkIfPositiveAmount(amount);
        checkIfThereAreEnoughFounds(amount);

        Entry withdrawEntry = new Entry(getId(), amount.negate());
        entries.add(withdrawEntry);
        balance = balance.subtract(amount);
    }

    public AccountId getId() {
        return id;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public MonetaryAmount getBalance() {
        return balance;
    }

    public CurrencyUnit getCurrency() {
        return getBalance().getCurrency();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equal(id, account.id) &&
                Objects.equal(balance, account.balance) &&
                Objects.equal(entries, account.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, balance, entries);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private AccountId id;
    @Columns(columns = {@Column(name = "currency"), @Column(name = "balance")})
    @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmountAndCurrency")
    private MonetaryAmount balance;
    //private OwnerId ownerId;
    @ElementCollection
    @CollectionTable(name = "account_entries")
    @Fetch(FetchMode.JOIN)
    private List<Entry> entries;

    private Account() {

    }

    private void checkIfThereAreEnoughFounds(MonetaryAmount amount) {
        if (amount.isGreaterThan(getBalance())) {
            throw new NotEnoghFoundException(getId(), amount, getBalance());
        }
    }

    private void checkIfOperationCurrencyMatchesAccountCurrency(MonetaryAmount amount) {
        if (!amount.getCurrency().equals(getBalance().getCurrency())) {
            throw new MontetaryAmountRequiresConversionException(getId(), amount.getCurrency(), getBalance().getCurrency());
        }
    }

    private void checkIfPositiveAmount(MonetaryAmount amount) {
        if (amount.isNegativeOrZero()) {
            throw new NegativeAmountOfMoneyException(amount);
        }
    }
}

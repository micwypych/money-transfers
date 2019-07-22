package com.revolut.transfers.account.domain;

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
        checkIfNonNegativeAmount(amount);

        Entry depositEntry = new Entry(getId(), amount);
        entries.add(depositEntry);
        balance = balance.add(amount);
    }

    public void withdraw(MonetaryAmount amount) {
        checkIfOperationCurrencyMatchesAccountCurrency(amount);
        checkIfNonNegativeAmount(amount);
        checkIfThereAreEnoughFounds(amount);

        Entry withdrawEntry = new Entry(getId(), amount.negate());
        entries.add(withdrawEntry);
        balance = balance.subtract(amount);
    }

//    @EmbeddedId
//    private AccountId id;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private AccountId id;
    @Columns(columns = { @Column(name = "currency"), @Column(name = "balance") })
    @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmountAndCurrency")
    private MonetaryAmount balance;
    //private OwnerId ownerId;
    @Version
    private Integer version;
    @ElementCollection
    @CollectionTable(name="account_entries")
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
            throw new MontetaryAmountRequiresConversion(getId(), amount.getCurrency(), getBalance().getCurrency());
        }
    }

    private void checkIfNonNegativeAmount(MonetaryAmount amount) {
        if (amount.isNegative()) {
            throw new NegativeAmountOfMoneyException(amount);
        }
    }

    public AccountId getId() {
        return id;
    }

//    public Long getId() {
//        return id;
//    }

    public List<Entry> getEntries() {
        return entries;
    }

    public MonetaryAmount getBalance() {
        return balance;
    }
}

package com.revolut.transfers.account.domain;

import com.revolut.transfers.account.domain.exceptions.AccountNotFoundException;
import com.revolut.transfers.account.domain.exceptions.TransferBetweenSameAccountException;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

public class TransferServiceImpl implements TransferService {

    public TransferServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void transfer(AccountId from, AccountId to, MonetaryAmount amount) {
        checkNotNull(from);
        checkNotNull(to);
        checkNotNull(amount);
        checkNotSameAccounts(from, to);

        Account sourceAccount = accountRepository.findById(from).orElseThrow(() -> new AccountNotFoundException(from));
        Account destinationAccount = accountRepository.findById(to).orElseThrow(() -> new AccountNotFoundException(to));

        sourceAccount.withdraw(amount);
        destinationAccount.deposit(amount);
    }

    @Override
    public Account createAccount(CurrencyUnit accountCurrencyUnit) {
        checkNotNull(accountCurrencyUnit);
        Account account = new Account(accountRepository.nextId(), accountCurrencyUnit);
        accountRepository.add(account);
        return account;
    }

    @Override
    public Account createAccountWithInitialBalance(CurrencyUnit accountCurrencyUnit, BigDecimal initialBalance) {
        checkNotNull(accountCurrencyUnit);
        checkNotNull(initialBalance);
        Account account = new Account(accountRepository.nextId(), accountCurrencyUnit);
        account.deposit(Money.of(initialBalance, accountCurrencyUnit));
        accountRepository.add(account);
        return account;
    }

    @Override
    public Account retriveAccount(AccountId accountId) {
        checkNotNull(accountId);
        return accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    public void deleteAccount(AccountId accountId) {
        checkNotNull(accountId);
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        accountRepository.delete(account);
    }

    private final AccountRepository accountRepository;

    private void checkNotSameAccounts(AccountId from, AccountId to) {
        if (from.equals(to)) {
            throw new TransferBetweenSameAccountException(from);
        }
    }
}

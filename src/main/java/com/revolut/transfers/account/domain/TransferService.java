package com.revolut.transfers.account.domain;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.revolut.transfers.account.infrastructure.TransactionUtil.returnTransactionResult;
import static com.revolut.transfers.account.infrastructure.TransactionUtil.skipTransactionResult;

public class TransferService {

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void transfer(AccountId from, AccountId to, MonetaryAmount amount) {
        skipTransactionResult(() -> {
            checkNotNull(from);
            checkNotNull(to);
            checkNotNull(amount);
            checkNotSameAccounts(from, to);

            Account sourceAccount = accountRepository.findById(from).orElseThrow(() -> new AccountNotFoundException(from));
            Account destinationAccount = accountRepository.findById(to).orElseThrow(() -> new AccountNotFoundException(to));

            sourceAccount.withdraw(amount);
            destinationAccount.deposit(amount);
        });
    }

    private void checkNotSameAccounts(AccountId from, AccountId to) {
        if (from.equals(to)) {
            throw new TransferBetweenSameAccount(from);
        }
    }

    public Account createAccount(CurrencyUnit accountCurrencyUnit) {
        return returnTransactionResult(() -> {
            checkNotNull(accountCurrencyUnit);
            Account account = new Account(accountRepository.nextId(), accountCurrencyUnit);
            accountRepository.add(account);
            return account;
        });
    }

    public Account retriveAccount(AccountId accountId) {
        return returnTransactionResult(() -> {
            checkNotNull(accountId);
            return accountRepository.findById(accountId).orElseThrow(AccountNotFound::new);
        });
    }

    public void deleteAccount(AccountId accountId) {
        skipTransactionResult(() -> {
            checkNotNull(accountId);
            Account account = accountRepository.findById(accountId).orElseThrow(AccountNotFound::new);
            accountRepository.delete(account);
        });
    }

    private AccountRepository accountRepository;
}

package com.revolut.transfers.account.domain;

import bitronix.tm.TransactionManagerServices;

import javax.money.MonetaryAmount;
import javax.transaction.UserTransaction;

import static com.google.common.base.Preconditions.checkNotNull;

public class TransferService {

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void transfer(AccountId from, AccountId to, MonetaryAmount amount) throws Exception {
        UserTransaction tx = TransactionManagerServices.getTransactionManager();
        try {
            tx.begin();

            checkNotNull(from);
            checkNotNull(to);
            checkNotNull(amount);

            Account sourceAccount = accountRepository.findById(from).orElseThrow(() -> new AccountNotFoundException(from));
            Account destinationAccount = accountRepository.findById(to).orElseThrow(() -> new AccountNotFoundException(to));

            sourceAccount.withdraw(amount);
            destinationAccount.deposit(amount);

            tx.commit();
        } catch(Exception ex) {
            tx.rollback();
        }

    }

    private AccountRepository accountRepository;
}

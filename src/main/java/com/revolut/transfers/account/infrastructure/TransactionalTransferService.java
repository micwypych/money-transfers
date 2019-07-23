package com.revolut.transfers.account.infrastructure;

import com.revolut.transfers.account.domain.Account;
import com.revolut.transfers.account.domain.AccountId;
import com.revolut.transfers.account.domain.TransferService;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;

import static com.revolut.transfers.account.infrastructure.TransactionUtil.returnTransactionResult;
import static com.revolut.transfers.account.infrastructure.TransactionUtil.skipTransactionResult;

public class TransactionalTransferService implements TransferService {

    public TransactionalTransferService(TransferService service) {
        this.service = service;
    }

    @Override
    public void transfer(AccountId from, AccountId to, MonetaryAmount amount) {
        skipTransactionResult(() -> {
            service.transfer(from, to, amount);
        });
    }

    @Override
    public Account createAccount(CurrencyUnit accountCurrencyUnit) {
        return returnTransactionResult(() -> service.createAccount(accountCurrencyUnit));
    }

    @Override
    public Account createAccountWithInitialBalance(CurrencyUnit accountCurrencyUnit, BigDecimal initialBalance) {
        return returnTransactionResult(() -> service.createAccountWithInitialBalance(accountCurrencyUnit, initialBalance));
    }

    @Override
    public Account retriveAccount(AccountId accountId) {
        return returnTransactionResult(() -> service.retriveAccount(accountId));
    }

    @Override
    public void deleteAccount(AccountId accountId) {
        skipTransactionResult(() -> {
            service.deleteAccount(accountId);
        });
    }

    private final TransferService service;
}

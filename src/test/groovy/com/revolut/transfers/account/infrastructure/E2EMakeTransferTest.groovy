package com.revolut.transfers.account.infrastructure

import com.google.gson.Gson
import com.revolut.transfers.account.config.EntityManagerProvider
import com.revolut.transfers.account.config.TransfersConfig
import com.revolut.transfers.account.domain.Account
import com.revolut.transfers.account.domain.AccountRepository
import com.revolut.transfers.account.domain.TransferService
import org.javamoney.moneta.Money
import spock.lang.Specification

import javax.money.Monetary

import static com.revolut.transfers.account.domain.AccountTestUtil.newAccount
import static com.revolut.transfers.account.infrastructure.TransactionUtil.returnTransactionResult

class E2EMakeTransferTest extends Specification {
    def "single transfer successfully stores transfer from Adam to Beth account" () {
        setup:
        EntityManagerProvider provider = new EntityManagerProvider()
        AccountRepository accounts = new HibernateAccountRepository(provider)
        TransferService transferService = new TransferService(accounts)
        Account adamAccount = new Account(accounts.nextId(), Monetary.getCurrency("PLN"))
        adamAccount.deposit(Money.of(350,"PLN"))
        Account bethAccount = new Account(accounts.nextId(), Monetary.getCurrency("PLN"))
        returnTransactionResult({ -> accounts.add(adamAccount); accounts.add(bethAccount) })
        when:
        transferService.transfer(adamAccount.getId(), bethAccount.getId(), Money.of(199, "PLN"))

        adamAccount = returnTransactionResult({ accounts.findById(adamAccount.getId()) }).get()
        bethAccount = returnTransactionResult({ accounts.findById(bethAccount.getId()) }).get()
        then:
        adamAccount.entries.size() == 2
        bethAccount.entries.size() == 1
        adamAccount.balance == Money.of(151,"PLN")
        bethAccount.balance == Money.of(199,"PLN")
    }
}

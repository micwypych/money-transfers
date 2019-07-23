package com.revolut.transfers.account.domain

import com.revolut.transfers.account.domain.exceptions.TransferBetweenSameAccountException
import org.javamoney.moneta.Money
import spock.lang.Specification

import javax.money.Monetary

import static com.revolut.transfers.account.domain.AccountTestUtil.newAccount
import static com.revolut.transfers.account.domain.AccountTestUtil.newAccountWithInitialBalance

class TransferServiceImplTest extends Specification {
    def "transfer between existing accounts is possible if on the source account there are enough founds"() {
        setup:
        Account sourceAccount = newAccountWithInitialBalance(1002, "PLN", 350.78)
        Account destinationAccount = newAccount(3004, "PLN")
        AccountRepository repository = accountRepositoryStub(sourceAccount, destinationAccount)
        TransferServiceImpl transferService = new TransferServiceImpl(repository)

        when:
        transferService.transfer(new AccountId(1002), new AccountId(3004), Money.of(213.44, "PLN"))

        then:
        sourceAccount.balance == Money.of(137.34, "PLN")
        destinationAccount.balance == Money.of(213.44, "PLN")
    }

    def "transfer to the same account as from is prohibited"() {
        setup:
        Account selfAccount = newAccountWithInitialBalance(3446, "PLN", 350.78)
        AccountRepository repository = accountRepositoryStub(selfAccount)
        TransferServiceImpl transferService = new TransferServiceImpl(repository)

        when:
        transferService.transfer(new AccountId(3446), new AccountId(3446), Money.of(213.44, "PLN"))

        then:
        TransferBetweenSameAccountException ex = thrown()
        ex.message == "Transfer between same account: AccountId{3446}"
    }

    def "transfer with null amount throws exception"() {
        setup:
        Account sourceAccount = newAccountWithInitialBalance(1002, "PLN", 350.78)
        Account destinationAccount = newAccount(3004, "PLN")
        AccountRepository repository = accountRepositoryStub(sourceAccount, destinationAccount)
        TransferServiceImpl transferService = new TransferServiceImpl(repository)

        when:
        transferService.transfer(sourceAccount.getId(), destinationAccount.getId(), null)

        then:
        thrown(RuntimeException)
    }

    def "transfer to non existent account throws exception"() {
        setup:
        Account sourceAccount = newAccountWithInitialBalance(1002, "PLN", 350.78)
        AccountRepository repository = accountRepositoryStub(sourceAccount)
        TransferServiceImpl transferService = new TransferServiceImpl(repository)

        when:
        transferService.transfer(sourceAccount.getId(), AccountId.exisitingId(4444), Money.of(8.81, "PLN"))

        then:
        thrown(RuntimeException)
    }

    def "transfer from non existent account throws exception"() {
        setup:
        Account destinationAccount = newAccountWithInitialBalance(1002, "PLN", 350.78)
        AccountRepository repository = accountRepositoryStub(destinationAccount)
        TransferServiceImpl transferService = new TransferServiceImpl(repository)

        when:
        transferService.transfer(AccountId.exisitingId(4445), destinationAccount.getId(), Money.of(9.99, "PLN"))

        then:
        thrown(RuntimeException)
    }

    def "create new empty account"() {
        setup:
        AccountRepository repository = Mock()
        TransferService transferService = new TransferServiceImpl(repository)
        when:
        transferService.createAccount(Monetary.getCurrency("GBP"))
        then:
        1 * repository.add({ it.currency == Monetary.getCurrency("GBP") && it.entries.size() == 0 })
    }

    def "create new account with initial value"() {
        setup:
        AccountRepository repository = Mock()
        TransferService transferService = new TransferServiceImpl(repository)
        when:
        transferService.createAccountWithInitialBalance(Monetary.getCurrency("GBP"), 89.21)
        then:
        1 * repository.add({
            it.currency == Monetary.getCurrency("GBP") &&
                    it.entries.size() == 1 &&
                    it.balance.isEqualTo(Money.of(89.21, "GBP"))
        })
    }

    AccountRepository accountRepositoryStub(Account... accounts) {
        AccountRepository repository = Stub(AccountRepository.class)
        for (Account account : accounts) {
            repository.findById(account.getId()) >> Optional.of(account)
        }
        return repository
    }
}

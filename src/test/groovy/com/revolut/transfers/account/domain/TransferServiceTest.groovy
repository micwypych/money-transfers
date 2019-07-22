package com.revolut.transfers.account.domain

import org.javamoney.moneta.Money
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import static com.revolut.transfers.account.domain.AccountTestUtil.*

class TransferServiceTest extends Specification {
    def "transfer between existing accounts to existing account is successful"() {
        setup:
        Account sourceAccount = newAccountWithInitialBalance(1002, "PLN", 350.78)
        Account destinationAccount = newAccount(3004, "PLN")
        AccountRepository repository = accountRepositoryStub(sourceAccount, destinationAccount)
        TransferService transferService = new TransferService(repository)

        when:
        transferService.transfer(new AccountId(1002), new AccountId(3004), Money.of(213.44, "PLN"))

        then:
        sourceAccount.balance == Money.of(137.34, "PLN")
        destinationAccount.balance == Money.of(213.44, "PLN")
    }

    AccountRepository accountRepositoryStub(Account ... accounts) {
        AccountRepository repository = Stub(AccountRepository.class)
        for (Account account : accounts) {
            repository.findById(account.getId()) >> Optional.of(account)
        }
        return repository
    }
}

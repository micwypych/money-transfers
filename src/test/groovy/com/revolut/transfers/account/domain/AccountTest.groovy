package com.revolut.transfers.account.domain

import com.revolut.transfers.account.domain.exceptions.MontetaryAmountRequiresConversionException
import com.revolut.transfers.account.domain.exceptions.NegativeAmountOfMoneyException
import com.revolut.transfers.account.domain.exceptions.NotEnoghFoundException
import org.javamoney.moneta.Money
import spock.lang.Specification

import static com.revolut.transfers.account.domain.AccountTestUtil.newAccount
import static com.revolut.transfers.account.domain.AccountTestUtil.newAccountWithInitialBalance

class AccountTest extends Specification {
    def "it is not possible to withdraw money from account with 0 balance"() {
        setup:
        Account account = newAccount(2134, "PLN")

        when:
        account.withdraw(Money.of(654.45, "PLN"))

        then:
        NotEnoghFoundException ex = thrown()
        ex.message == "Not enough founds on the account: AccountId{2134}. Requested amount: PLN 654.45. Current balance: PLN 0"
    }

    def "it is not possible to deposit Pounds GBP into EUR account"() {
        setup:
        Account account = newAccount(4432, "EUR")

        when:
        account.deposit(Money.of(987.45, "GBP"))

        then:
        MontetaryAmountRequiresConversionException ex = thrown()
        ex.message == "Account id: AccountId{4432}. Requested currency: GBP does not match the account's currency: EUR."
    }

    def "it is not possible to withdraw JODs from EUR account"() {
        setup:
        Account account = newAccountWithInitialBalance(1278, "EUR", 1000000.00)

        when:
        account.withdraw(Money.of(10023.345, "JOD"))

        then:
        MontetaryAmountRequiresConversionException ex = thrown()
        ex.message == "Account id: AccountId{1278}. Requested currency: JOD does not match the account's currency: EUR."
    }

    def "it is not possible to deposit negative amount of money into an account"() {
        setup:
        Account account = newAccount(3312, "EUR")

        when:
        account.deposit(Money.of(-321.45, "EUR"))

        then:
        NegativeAmountOfMoneyException ex = thrown()
        ex.message == "The money amount cannot be negative: EUR -321.45"
    }

    def "it is not possible to withdraw negative amount of money from an account"() {
        setup:
        Account account = newAccountWithInitialBalance(3312, "EUR", 1000.00)

        when:
        account.withdraw(Money.of(-916.32, "EUR"))

        then:
        NegativeAmountOfMoneyException ex = thrown()
        ex.message == "The money amount cannot be negative: EUR -916.32"
    }

    def "it is not possible to deposit zero amount of money into an account"() {
        setup:
        Account account = newAccount(3312, "EUR")

        when:
        account.deposit(Money.of(0.00, "EUR"))

        then:
        NegativeAmountOfMoneyException ex = thrown()
        ex.message == "The money amount cannot be negative: EUR 0"
    }

    def "it is possible to deposit money to new account"() {
        setup:
        Account account = newAccount(7813, "CHF")

        when:
        account.deposit(Money.of(87.65, "CHF"))

        then:
        account.balance == Money.of(87.65, "CHF")
    }

    def "money deposit and then withdraw part of it"() {
        setup:
        Account account = newAccount(5437, "USD")

        when:
        account.deposit(Money.of(100.19, "USD"))
        account.withdraw(Money.of(15.22, "USD"))

        then:
        account.balance == Money.of(84.97, "USD")
    }

    def "10k small deposits sums up into correct result with no round-off error"() {
        setup:
        Account account = newAccount(7813, "JOD")

        when:
        for (int i = 0; i < 10000; i++) {
            account.deposit(Money.of(0.001, "JOD"))
        }

        then:
        account.entries.size() == 10000
        account.balance == Money.of(10.000, "JOD")
    }

    def "sequence of deposits and withdraws end in proper balance"() {
        setup:
        Account account = newAccount(4498, "EUR")

        when:
        account.deposit(Money.of(7.04, "EUR"))
        account.deposit(Money.of(8.80, "EUR"))
        account.withdraw(Money.of(12.02, "EUR"))
        account.deposit(Money.of(9.83, "EUR"))

        then:
        account.entries.size() == 4
        account.balance == Money.of(13.65, "EUR")
    }
}

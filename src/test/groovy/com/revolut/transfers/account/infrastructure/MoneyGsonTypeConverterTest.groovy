package com.revolut.transfers.account.infrastructure

import com.google.gson.Gson
import com.revolut.transfers.account.config.TransfersConfig
import com.revolut.transfers.account.domain.Account
import org.javamoney.moneta.Money
import spock.lang.Specification

import static com.revolut.transfers.account.domain.AccountTestUtil.newAccount

class MoneyGsonTypeConverterTest extends Specification {
    def "serilize moneys"() {
        setup:
        Gson gson = new TransfersConfig().gson()
        def money = Money.of(56.89,"PLN")
        expect:
        gson.toJson(money) == "{\"currency\":\"PLN\",\"amount\":56.89}"
        gson.fromJson(gson.toJson(money),Money.class) == money
    }

    def "serilize empty Account"() {
        setup:
        Gson gson = new TransfersConfig().gson()
        def account = newAccount(8989,"JOD")
        expect:
        gson.toJson(account) == "{\"id\":{\"id\":8989},\"balance\":{\"currency\":\"JOD\",\"amount\":0},\"entries\":[]}"
        gson.fromJson(gson.toJson(account), Account.class) == account
    }

    def "serilize Account with entries"() {
        setup:
        Gson gson = new TransfersConfig().gson()
        def account = newAccount(8989,"JOD")
        account.deposit(Money.of(34.123,"JOD"))
        account.deposit(Money.of(45.001,"JOD"))
        account.withdraw(Money.of(71.752,"JOD"))
        Account deserializeAccount = gson.fromJson(gson.toJson(account),Account.class)
        expect:
        gson.toJson(account) == "{\"id\":{\"id\":8989},\"balance\":{\"currency\":\"JOD\",\"amount\":7.372},\"entries\":[{\"accountId\":{\"id\":8989},\"amount\":{\"currency\":\"JOD\",\"amount\":34.123}},{\"accountId\":{\"id\":8989},\"amount\":{\"currency\":\"JOD\",\"amount\":45.001}},{\"accountId\":{\"id\":8989},\"amount\":{\"currency\":\"JOD\",\"amount\":-71.752}}]}"
        deserializeAccount == account
        deserializeAccount.balance == account.balance
        deserializeAccount.entries == account.entries
        deserializeAccount.id == account.id
    }
}

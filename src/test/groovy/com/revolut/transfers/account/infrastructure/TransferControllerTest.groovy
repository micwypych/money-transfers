package com.revolut.transfers.account.infrastructure

import com.google.gson.Gson
import com.revolut.transfers.account.config.TransfersConfig
import com.revolut.transfers.account.domain.AccountId
import com.revolut.transfers.account.domain.TransferServiceImpl
import org.javamoney.moneta.Money
import spark.Request
import spark.Response
import spock.lang.Specification

import javax.money.Monetary

class TransferControllerTest extends Specification {
    def "MakeTransfer"() {
        setup:
        Gson gson = TransfersConfig.gson()
        TransferServiceImpl service = Mock()
        TransferController controller = new TransferController(gson, service)
        Request request = Stub()
        request.params("id") >> "3456"
        request.params("transferToId") >> "9012"
        request.body() >> "{\"currency\":\"PLN\",\"amount\":56.89}"
        Response response = Stub()
        when:
        controller.makeTransfer(request, response)
        then:
        noExceptionThrown()
        1 * service.transfer(AccountId.exisitingId(3456), AccountId.exisitingId(9012), Money.of(56.89, "PLN"))
    }

    def "Create new empty Account"() {
        setup:
        Gson gson = TransfersConfig.gson()
        TransferServiceImpl service = Mock()
        TransferController controller = new TransferController(gson, service)
        Request request = Stub()
        request.body() >> "{\"currency\":\"PLN\"}"
        Response response = Stub()
        when:
        controller.createAccount(request, response)
        then:
        noExceptionThrown()
        1 * service.createAccount(Monetary.getCurrency("PLN"))
    }

    def "Create new Account with initial balance"() {
        setup:
        Gson gson = TransfersConfig.gson()
        TransferServiceImpl service = Mock()
        TransferController controller = new TransferController(gson, service)
        Request request = Stub()
        request.body() >> "{\"currency\":\"PLN\", \"initialBalance\":56718.78}"
        Response response = Stub()
        when:
        controller.createAccount(request, response)
        then:
        noExceptionThrown()
        1 * service.createAccountWithInitialBalance(Monetary.getCurrency("PLN"), BigDecimal.valueOf(56718.78))
    }
}

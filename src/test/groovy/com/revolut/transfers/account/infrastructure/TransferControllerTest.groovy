package com.revolut.transfers.account.infrastructure

import com.google.gson.Gson
import com.revolut.transfers.account.config.TransfersConfig
import com.revolut.transfers.account.domain.AccountId
import com.revolut.transfers.account.domain.TransferService
import spark.Request
import spark.Response
import spock.lang.Specification

class TransferControllerTest extends Specification {
    def "MakeTransfer"() {
        setup:
        Gson gson = TransfersConfig.gson()
        TransferService service = Mock()
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
        1 * service.transfer(AccountId.exisitingId(3456),AccountId.exisitingId(9012),_)
    }

    def "CreateAccount"() {
    }
}

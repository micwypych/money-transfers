package com.revolut.transfers.account.infrastructure;

import com.google.gson.Gson;
import com.revolut.transfers.account.domain.Account;
import com.revolut.transfers.account.domain.AccountId;
import com.revolut.transfers.account.domain.TransferService;
import org.javamoney.moneta.Money;
import spark.Request;
import spark.Response;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

public class TransferController {

    public TransferController(Gson gson, TransferService transferService) {
        this.transferService = transferService;
        this.gson = gson;
    }

    public void makeTransfer(Request request, Response response) {
        AccountId fromAcountId = AccountId.exisitingId(Long.parseLong(request.params("id")));
        AccountId toAcountId = AccountId.exisitingId(Long.parseLong(request.params("transferToId")));

        MonetaryAmount amount = gson.fromJson(request.body(), MonetaryAmount.class);
        transferService.transfer(fromAcountId,toAcountId,amount);
        response.status(204);
    }

    public Account createAccount(Request request, Response response) {
        CurrencyUnit accountCurrencyUnit = Monetary.getCurrency(gson.fromJson(request.body(), String.class));
        response.status(201);
        return transferService.createAccount(accountCurrencyUnit);
    }

    private final TransferService transferService;
    private final Gson gson;
}

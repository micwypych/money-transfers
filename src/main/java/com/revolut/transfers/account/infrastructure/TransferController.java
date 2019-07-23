package com.revolut.transfers.account.infrastructure;

import com.google.gson.Gson;
import com.revolut.transfers.account.domain.Account;
import com.revolut.transfers.account.domain.AccountId;
import com.revolut.transfers.account.domain.TransferService;
import spark.Request;
import spark.Response;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class TransferController {

    public TransferController(Gson gson, TransferService transferService) {
        this.transferService = transferService;
        this.gson = gson;
    }

    public void makeTransfer(Request request, Response response) {
        AccountId fromAcountId = AccountId.exisitingId(Long.parseLong(request.params("id")));
        AccountId toAcountId = AccountId.exisitingId(Long.parseLong(request.params("transferToId")));

        MonetaryAmount amount = gson.fromJson(request.body(), MonetaryAmount.class);
        transferService.transfer(fromAcountId, toAcountId, amount);
        response.status(204);
    }

    public Account createAccount(Request request, Response response) {
        CreateAccountDTO accountData = gson.fromJson(request.body(), CreateAccountDTO.class);
        response.status(201);
        if (accountData.getInitialAmount().isPresent()) {
            return transferService.createAccountWithInitialBalance(accountData.getCurrency(), accountData.getInitialAmount().get());
        } else {
            return transferService.createAccount(accountData.getCurrency());
        }
    }

    public Account retrieveAccount(Request request, Response response) {
        AccountId toRetrieveId = AccountId.exisitingId(Long.parseLong(request.params("id")));
        return transferService.retriveAccount(toRetrieveId);
    }

    public void deleteAccount(Request request, Response response) {
        AccountId toDeleteId = AccountId.exisitingId(Long.parseLong(request.params("id")));
        transferService.deleteAccount(toDeleteId);
        response.status(204);
    }

    public static class CreateAccountDTO {
        public CreateAccountDTO(CurrencyUnit currency, BigDecimal initialAmount) {
            checkNotNull(currency);
            this.currency = currency;
            this.initialAmount = initialAmount;
        }

        public CreateAccountDTO(CurrencyUnit currency) {
            this(checkNotNull(currency), null);
        }

        public CurrencyUnit getCurrency() {
            return currency;
        }

        public Optional<BigDecimal> getInitialAmount() {
            return Optional.ofNullable(initialAmount);
        }

        private CurrencyUnit currency;
        private BigDecimal initialAmount;
    }

    private final TransferService transferService;
    private final Gson gson;
}

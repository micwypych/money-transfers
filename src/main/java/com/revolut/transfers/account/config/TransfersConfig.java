package com.revolut.transfers.account.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revolut.transfers.account.domain.Account;
import com.revolut.transfers.account.domain.AccountRepository;
import com.revolut.transfers.account.domain.TransferService;
import com.revolut.transfers.account.infrastructure.HibernateAccountRepository;
import com.revolut.transfers.account.infrastructure.MoneyGsonTypeConverter;
import com.revolut.transfers.account.infrastructure.TransferController;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.util.Optional;

public class TransfersConfig {


    public static AccountRepository accountRepository(EntityManagerProvider provider) {
        //TODO make it singleton
        return new HibernateAccountRepository(provider);
    }

    public static EntityManagerProvider entityManagerProvider() {
        //TODO make it singleton
        return new EntityManagerProvider();
    }

    public static TransferService transferService(AccountRepository accountRepository) {
        return new TransferService(accountRepository);
    }

    private TransfersConfig(){

    }

    public static TransferController transferController(Gson gson, TransferService transferService) {
        return new TransferController(gson, transferService);
    }

    public static TransferController defaultTransfersConfig() {
        EntityManagerProvider entityManagerProvider = entityManagerProvider();
        AccountRepository accountRepository = accountRepository(entityManagerProvider);
        return transferController(gson(), transferService(accountRepository));
    }

    public static Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(MonetaryAmount.class, new MoneyGsonTypeConverter())
                .registerTypeAdapter(Money.class, new MoneyGsonTypeConverter())
                .create();
    }
}

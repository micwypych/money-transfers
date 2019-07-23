package com.revolut.transfers.account.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revolut.transfers.account.config.typeconverters.AccountIdGsonTypeConverter;
import com.revolut.transfers.account.config.typeconverters.CreateAccountDTOGsonTypeConverter;
import com.revolut.transfers.account.config.typeconverters.CurrencyGsonTypeConverter;
import com.revolut.transfers.account.config.typeconverters.MoneyGsonTypeConverter;
import com.revolut.transfers.account.domain.AccountId;
import com.revolut.transfers.account.domain.AccountRepository;
import com.revolut.transfers.account.domain.TransferService;
import com.revolut.transfers.account.domain.TransferServiceImpl;
import com.revolut.transfers.account.infrastructure.HibernateAccountRepository;
import com.revolut.transfers.account.infrastructure.TransactionalTransferService;
import com.revolut.transfers.account.infrastructure.TransferController;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

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
        TransferService nonTransactionalService = new TransferServiceImpl(accountRepository);
        return new TransactionalTransferService(nonTransactionalService);
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
                .registerTypeAdapter(AccountId.class, new AccountIdGsonTypeConverter())
                .registerTypeAdapter(CurrencyUnit.class, new CurrencyGsonTypeConverter())
                .registerTypeAdapter(TransferController.CreateAccountDTO.class, new CreateAccountDTOGsonTypeConverter())
                .create();
    }

    private TransfersConfig() {

    }
}

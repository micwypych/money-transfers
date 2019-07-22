package com.revolut.transfers.account.config;

import com.revolut.transfers.account.domain.Account;
import com.revolut.transfers.account.domain.AccountRepository;
import com.revolut.transfers.account.domain.TransferService;
import com.revolut.transfers.account.infrastructure.HibernateAccountRepository;

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
}

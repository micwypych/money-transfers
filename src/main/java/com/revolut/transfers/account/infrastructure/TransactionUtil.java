package com.revolut.transfers.account.infrastructure;

import bitronix.tm.TransactionManagerServices;

import javax.transaction.UserTransaction;
import java.util.concurrent.Callable;

public class TransactionUtil {
    static <V> V withTransaction(Callable<V> f) {
        try {
            UserTransaction tx = TransactionManagerServices.getTransactionManager();
            tx.begin();
            V value = f.call();
            tx.commit();
            return value;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

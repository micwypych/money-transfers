package com.revolut.transfers.account.infrastructure;

import bitronix.tm.TransactionManagerServices;

import javax.transaction.UserTransaction;
import java.util.concurrent.Callable;

public class TransactionUtil {
    public static <V> V returnTransactionResult(Callable<V> f) {
        try {
            UserTransaction tx = TransactionManagerServices.getTransactionManager();
            try {
                tx.begin();
                V value = f.call();
                tx.commit();
                return value;
            } catch (Exception ex) {
                tx.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <V> void skipTransactionResult(Runnable f) {
        try {
            UserTransaction tx = TransactionManagerServices.getTransactionManager();
            try {
                tx.begin();
                f.run();
                tx.commit();
            } catch (Exception ex) {
                tx.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

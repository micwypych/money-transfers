package com.revolut.transfers.account.domain;

import com.google.common.base.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import java.io.Serializable;

@Embeddable
public final class AccountId implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountId accountId = (AccountId) o;
        return Objects.equal(id, accountId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AccountId{" + id + '}';
    }

    public long toSerialize() {
        return id;
    }

    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    private AccountId(Long id) {
        this.id = id;
    }

    public static AccountId newId() {
        return null;
    }

    public static AccountId exisitingId(long id) {
        return new AccountId(id);
    }

    AccountId() {
        id = null;
    }
}

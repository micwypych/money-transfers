package com.revolut.transfers.account.config.typeconverters;

import com.google.gson.*;
import com.revolut.transfers.account.domain.AccountId;

import java.lang.reflect.Type;

public class AccountIdGsonTypeConverter implements JsonSerializer<AccountId>, JsonDeserializer<AccountId> {
    @Override
    public JsonElement serialize(AccountId src, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(src.toSerialize());
    }

    @Override
    public AccountId deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        return AccountId.exisitingId(json.getAsLong());
    }
}

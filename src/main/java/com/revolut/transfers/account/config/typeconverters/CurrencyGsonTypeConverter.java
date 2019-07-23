package com.revolut.transfers.account.config.typeconverters;

import com.google.gson.*;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.lang.reflect.Type;

public class CurrencyGsonTypeConverter implements JsonSerializer<CurrencyUnit>, JsonDeserializer<CurrencyUnit> {
    @Override
    public JsonElement serialize(CurrencyUnit src, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public CurrencyUnit deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        return Monetary.getCurrency(json.getAsString());
    }

}

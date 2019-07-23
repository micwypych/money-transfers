package com.revolut.transfers.account.config.typeconverters;

import com.google.gson.*;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.lang.reflect.Type;

public class MoneyGsonTypeConverter implements JsonSerializer<MonetaryAmount>, JsonDeserializer<MonetaryAmount> {
    @Override
    public JsonElement serialize(MonetaryAmount src, Type srcType, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("currency", src.getCurrency().toString());
        jsonObject.addProperty("amount", src.getNumber());
        return jsonObject;
    }

    @Override
    public MonetaryAmount deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject o = json.getAsJsonObject();
        return Money.of(o.get("amount").getAsBigDecimal(), o.get("currency").getAsString());
    }

}

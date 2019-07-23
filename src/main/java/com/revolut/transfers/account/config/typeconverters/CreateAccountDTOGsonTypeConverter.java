package com.revolut.transfers.account.config.typeconverters;

import com.google.gson.*;
import com.revolut.transfers.account.infrastructure.TransferController;

import javax.money.CurrencyUnit;
import java.lang.reflect.Type;

public class CreateAccountDTOGsonTypeConverter implements JsonSerializer<TransferController.CreateAccountDTO>, JsonDeserializer<TransferController.CreateAccountDTO> {

    public static final String INITIAL_BALANCE_FIELD = "initialBalance";
    public static final String CURRENCY_FIELD = "currency";

    @Override
    public JsonElement serialize(TransferController.CreateAccountDTO src, Type srcType, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(CURRENCY_FIELD, context.serialize(src.getCurrency()));
        src.getInitialAmount().ifPresent(initialValue -> jsonObject.addProperty(INITIAL_BALANCE_FIELD, initialValue));
        return jsonObject;
    }

    @Override
    public TransferController.CreateAccountDTO deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject o = json.getAsJsonObject();
        if (o.has(INITIAL_BALANCE_FIELD)) {
            return new TransferController.CreateAccountDTO(
                    context.deserialize(o.get(CURRENCY_FIELD), CurrencyUnit.class),
                    o.get(INITIAL_BALANCE_FIELD).getAsBigDecimal());
        } else {

            return new TransferController.CreateAccountDTO(
                    context.deserialize(o.get(CURRENCY_FIELD), CurrencyUnit.class));
        }
    }

}

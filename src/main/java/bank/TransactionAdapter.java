package bank;

import com.google.gson.*;
import java.lang.reflect.Type;

public class TransactionAdapter implements JsonSerializer<Transaction>, JsonDeserializer<Transaction> {

    @Override
    public JsonElement serialize(Transaction src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject wrapper = new JsonObject();

        wrapper.addProperty("CLASSNAME", src.getClass().getSimpleName());

        JsonObject obj = new JsonObject();
        obj.addProperty("date", src.getDate());
        obj.addProperty("amount", src.getAmount());
        obj.addProperty("description", src.getDescription());

        if (src instanceof Payment p) {
            obj.addProperty("incomingInterest", p.getIncomingInterest());
            obj.addProperty("outgoingInterest", p.getOutgoingInterest());
        }

        if (src instanceof Transfer t) {
            obj.addProperty("sender", t.getSender());
            obj.addProperty("recipient", t.getRecipient());
        }

        wrapper.add("INSTANCE", obj);
        return wrapper;
    }


    @Override
    public Transaction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject wrapper = json.getAsJsonObject();
        String className = wrapper.get("CLASSNAME").getAsString();
        JsonObject obj = wrapper.get("INSTANCE").getAsJsonObject();

        switch (className) {
            case "Payment":
                return new Payment(
                        obj.get("date").getAsString(),
                        obj.get("amount").getAsDouble(),
                        obj.get("description").getAsString(),
                        obj.get("incomingInterest").getAsDouble(),
                        obj.get("outgoingInterest").getAsDouble()
                );

            case "IncomingTransfer":
                return new IncomingTransfer(
                        obj.get("date").getAsString(),
                        obj.get("amount").getAsDouble(),
                        obj.get("description").getAsString(),
                        obj.get("sender").getAsString(),
                        obj.get("recipient").getAsString()
                );

            case "OutgoingTransfer":
                return new OutgoingTransfer(
                        obj.get("date").getAsString(),
                        obj.get("amount").getAsDouble(),
                        obj.get("description").getAsString(),
                        obj.get("sender").getAsString(),
                        obj.get("recipient").getAsString()
                );
        }

        throw new JsonParseException("Unknown CLASSNAME: " + className);
    }

}

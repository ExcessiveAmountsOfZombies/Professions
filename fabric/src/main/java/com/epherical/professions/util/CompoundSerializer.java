package com.epherical.professions.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public interface CompoundSerializer<A, B> {

    void serialize(JsonObject jsonObject, A value, JsonSerializationContext context);

    B deserialize(JsonObject object, JsonDeserializationContext context);
}

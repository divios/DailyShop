package io.github.divios.lib.serialize;

import com.google.gson.JsonElement;

public interface jsonSerializer<T> {

    JsonElement toJson(T t);

    T fromJson(JsonElement element);

}

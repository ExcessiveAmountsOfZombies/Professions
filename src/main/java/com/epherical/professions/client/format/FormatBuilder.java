package com.epherical.professions.client.format;

public interface FormatBuilder<T> {

    default Format<T> buildDefaultFormat() {
        return deserializeToFormat(null);
    }

    Format<T> deserializeToFormat(T t);

}

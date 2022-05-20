package com.epherical.professions.data;

import org.jetbrains.annotations.Nullable;

public interface Storage<Result, Key> {

    boolean hasUser(Key uuid);

    @Nullable
    Result getUser(@Nullable Key uuid);

    Result createUser(@Nullable Key uuid);

    void saveUser(Result result);

    boolean isDatabase();
}

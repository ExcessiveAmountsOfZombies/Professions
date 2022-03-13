package com.epherical.professions.data;

import org.jetbrains.annotations.Nullable;

public interface Storage<Result, Key> {

    boolean hasUser(Key uuid);

    @Nullable
    Result getUser(Key uuid);

    Result createUser(Key uuid);

    void saveUser(Result result);
}

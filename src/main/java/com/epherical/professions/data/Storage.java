package com.epherical.professions.data;

import com.epherical.professions.profession.Profession;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Storage<Result, Key> {

    boolean hasUser(Key uuid);

    @Nullable
    Result getUser(@Nullable Key uuid);

    Result createUser(@Nullable Key uuid);

    void saveUser(Result result);

    boolean isDatabase();

    List<Result> getUsers(int from, int to, Profession profession);
}

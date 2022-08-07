package com.epherical.professions.client.editor;

import java.util.function.BiFunction;

public interface EditorCreator<T> extends BiFunction<Integer, Integer, T> {

}

package com.epherical.professions.client.editor;

import com.epherical.professions.client.editors.DatapackEditor;

import java.util.function.BiFunction;

public interface EditorCreator<T extends DatapackEditor> extends BiFunction<Integer, Integer, T> {

}

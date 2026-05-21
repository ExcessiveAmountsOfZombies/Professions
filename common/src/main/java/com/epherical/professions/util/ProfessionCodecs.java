package com.epherical.professions.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.epherical.professions.org.mbertoli.jfep.Parser;

import java.util.NavigableMap;
import java.util.TreeMap;


public class ProfessionCodecs {

    public static final Codec<NavigableMap<Integer, Parser>> EXP_SCALERS_CODEC =
            Codec.unboundedMap(Codec.STRING, Codec.STRING)
                    .comapFlatMap(map -> {
                        NavigableMap<Integer, Parser> out = new TreeMap<>();
                        for (var entry : map.entrySet()) {
                            try {
                                out.put(Integer.parseInt(entry.getKey()), new Parser(entry.getValue()));
                            } catch (NumberFormatException ex) {
                                return DataResult.error(() -> "Scaler key must be an integer: " + entry.getKey());
                            }
                        }
                        return DataResult.success(out);
                    }, map -> {
                        NavigableMap<String, String> out = new TreeMap<>();
                        for (var entry : map.entrySet()) {
                            out.put(Integer.toString(entry.getKey()), entry.getValue().getInputString());
                        }
                        return out;
                    });

}

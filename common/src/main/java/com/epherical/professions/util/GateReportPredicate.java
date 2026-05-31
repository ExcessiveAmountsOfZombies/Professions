package com.epherical.professions.util;

import com.epherical.professions.api.actions.Gate;
import com.epherical.professions.model.gating.GateReport;

import java.util.Objects;

@FunctionalInterface
public interface GateReportPredicate<T, U> {


    void test(T t, U u, GateReport gateReport, Gate<?> gate);


    default GateReportPredicate<T, U> and(GateReportPredicate<? super T, ? super U> other) {
        Objects.requireNonNull(other);
        return (T t, U u, GateReport gr, Gate<?> g) -> {
            test(t, u, gr, g);
            other.test(t, u, gr, g);
        };
    }

}

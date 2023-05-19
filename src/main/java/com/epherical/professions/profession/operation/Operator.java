package com.epherical.professions.profession.operation;

public class Operator<T, V> {

    private V occupations;
    private T action;

    public Operator(V occupations, T action) {
        this.occupations = occupations;
        this.action = action;
    }

    public T getOperator() {
        return action;
    }

    public V getOccupations() {
        return occupations;
    }
}

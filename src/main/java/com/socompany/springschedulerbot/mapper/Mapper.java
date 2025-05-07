package com.socompany.springschedulerbot.mapper;

public interface Mapper<F, T> {
    T map(F from);

    default T map(F fromObj, T toObj){
        throw new UnsupportedOperationException("Not implemented.");
    }
}

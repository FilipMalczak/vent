package com.github.filipmalczak.vent.dto;

public enum Operation {
    CREATE,
    DELETE,
    SET,
    ADD,
    LINK, //semantics: add reference
    REMOVE,
    GET,
    GET_SCHEMA,
    COMPACT
}

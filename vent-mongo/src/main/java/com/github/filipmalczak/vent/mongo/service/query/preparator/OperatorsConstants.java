package com.github.filipmalczak.vent.mongo.service.query.preparator;

import java.util.Set;

import static com.github.filipmalczak.vent.helper.Struct.set;

interface OperatorsConstants {
    String andOperator = "$and";
    String orOperator = "$or";
    String elemMatchOperator = "$elemMatch";
    String lteOperator = "$lte";
    String gteOperator = "$gte";
    String notOperator = "$not";
    String norOperator = "$nor";
    String neOperator = "$ne";

    Set<String> collapsableOperators = set(andOperator, orOperator);

    static boolean isOperator(String key){
        return key.startsWith("$");
    }

    static boolean isObjectPath(String key){
        return !isOperator(key);
    }

}

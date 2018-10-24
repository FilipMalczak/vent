package com.github.filipmalczak.vent.mongo.factory;

@FunctionalInterface
public interface FactoryPlugin<Result, ExtensionAPI> {
    ResultWithAPI<Result, ExtensionAPI> process(ResultWithAPI<Result, ExtensionAPI> exposedInstance);
}

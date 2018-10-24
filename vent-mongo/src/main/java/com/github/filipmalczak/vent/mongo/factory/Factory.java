package com.github.filipmalczak.vent.mongo.factory;

public interface Factory<Result, Config, ExtensionAPI> {
    ResultWithAPI<Result, ExtensionAPI> prepare(Config config);

    default Result create(Config config){
        return prepare(config).getResult();
    }

    default Config getDefaultConfig(){
        throw new UnsupportedOperationException();
    }

    default Result create(){
        return create(getDefaultConfig());
    }
}

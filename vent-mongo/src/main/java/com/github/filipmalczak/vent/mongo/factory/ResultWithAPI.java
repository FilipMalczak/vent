package com.github.filipmalczak.vent.mongo.factory;

import lombok.Value;

@Value(staticConstructor = "of")
public
class ResultWithAPI<Result, ExtensionAPI>{
    Result result;
    ExtensionAPI extensionAPI;
}

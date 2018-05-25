package com.github.filipmalczak.vent.web.controller;

import static com.github.filipmalczak.vent.web.Paths.V1;

public interface CrudPaths {
    String COLLECTION_PATH = V1+"/crud/{collection}";
    String OBJECT_PATH = COLLECTION_PATH+"/{id}";
}

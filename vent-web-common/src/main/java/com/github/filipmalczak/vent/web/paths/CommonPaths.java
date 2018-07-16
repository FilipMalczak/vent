package com.github.filipmalczak.vent.web.paths;

public interface CommonPaths {
    String QUERY_TIME_PARAM = "queryAt={queryAt}";
    String PATH_PARAM = "path={path}";

    String API = "api";
    String V1 = API+"/v1";

    String DB = V1+"/db";

    String OPTIMIZE = DB+"/optimize/{strength}/{type}";

    String COLLECTIONS = DB+"/collection";
    String COLLECTION = COLLECTIONS+"/{name}";
    String COLLECTION_WITH_QUERY_TIME = COLLECTION+"?"+QUERY_TIME_PARAM;

    String OBJECTS = COLLECTION+"/object";
    String IDS = OBJECTS+"/id";
    String OBJECTS_WITH_QUERY_TIME = OBJECTS+"?"+QUERY_TIME_PARAM;
    String IDS_WITH_QUERY_TIME = IDS+"?"+QUERY_TIME_PARAM;

    String OBJECT = OBJECTS+"/{id}";
    String OBJECT_WITH_QUERY_TIME = OBJECT+"?"+QUERY_TIME_PARAM;

    String STATE = OBJECT+"/state";
    String STATE_WITH_PATH = STATE+"?"+PATH_PARAM;

    //todo query
}

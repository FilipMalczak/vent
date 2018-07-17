package com.github.filipmalczak.vent.web.paths;

public interface CommonPaths {
    String QUERY_TIME_PARAM = "queryAt={queryAt}";
    String PATH_PARAM = "path={path}";

    String API = "api";
    String V1 = API+"/v1";

    String DB = V1+"/db";

    //fixme this s*it has to go from the API
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

    //I know, I know, its RPC, while the other are trying to be RESTish; whoever has a better idea - speak up on gitter
    String QUERY = COLLECTION+"/query";
    String QUERY_WITH_TIME = QUERY+"?"+QUERY_TIME_PARAM;
    //todo query
}

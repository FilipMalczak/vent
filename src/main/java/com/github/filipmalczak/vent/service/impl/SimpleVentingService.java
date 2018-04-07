package com.github.filipmalczak.vent.service.impl;

import com.github.filipmalczak.vent.helper.resolver.ObjectPathResolver;
import com.github.filipmalczak.vent.helper.resolver.ResolvedPath;
import com.github.filipmalczak.vent.model.Vent;
import com.github.filipmalczak.vent.model.VentObject;
import com.github.filipmalczak.vent.service.VentingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.filipmalczak.vent.helper.Struct.map;

@Service
public class SimpleVentingService implements VentingService {
    @Autowired
    private ObjectPathResolver resolver;

    @Override
    public Mono<Map> applyVents(Mono<VentObject> object) {
        return object.flatMap(o -> {
            //todo: we either need a deep copy utility for map-represented JSON, or we need to remember that saving the parameter will be disastrous
            Map result = o.getLastSnapshot();
            boolean firstVent = true;
            ventsLoop:
            for (Vent vent: o.getEvents()){
                switch (vent.getOperation()) {
                    //fixme: magic string duplicated here and in DelegatingRequestHandlingService
                    case CREATE: {
                        if (!firstVent)
                            throw new IllegalStateException(); //todo
                        result = (Map) vent.
                            getPayload().
                            getOrDefault("initialValue", map());
                        break;
                    }
                    case SET: {
                        applySet(
                            result,
                            (String) vent.getPayload().get("path"),
                            vent.getPayload().get("value")
                        );
                        break;
                    }
                    case ADD: {
                        applyAdd(
                            result,
                            (String) vent.getPayload().get("path"),
                            vent.getPayload().get("value")
                        );
                        break;
                    }
                    case REMOVE: {
                        applyRemove(
                            result,
                            (String) vent.getPayload().get("path")
                        );
                        break;
                    }
                    case PUT: {
                        result = (Map) vent.getPayload().get("newValue");
                        break;
                    }
                    case DELETE: {
                        result = null;
                        break ventsLoop;
                    }
                    default:
                }
                firstVent = false;
            }
            return Mono.justOrEmpty(result);
        });
    }

    private void applySet(Map result, String path, Object val){
        resolver.resolve(result, path).set(val);
    }

    private void applyAdd(Map result, String path, Object val){
        ResolvedPath resolved = resolver.resolve(result, path);
        Object o = resolved.get();
        if (!(o instanceof List))
            throw new RuntimeException(); //todo
        ((List) o).add(val);
    }

    private void applyRemove(Map result, String path){
        resolver.resolve(result, path).delete();
    }
}

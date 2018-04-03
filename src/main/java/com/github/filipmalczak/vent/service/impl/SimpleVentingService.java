package com.github.filipmalczak.vent.service.impl;

import com.github.filipmalczak.vent.helper.resolver.ObjectPathResolver;
import com.github.filipmalczak.vent.helper.resolver.ResolvedPath;
import com.github.filipmalczak.vent.model.Vent;
import com.github.filipmalczak.vent.model.VentObject;
import com.github.filipmalczak.vent.service.VentingService;
import lombok.Value;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimpleVentingService implements VentingService {
    @Autowired
    private ObjectPathResolver resolver;

    @Override
    public Mono<Map> applyVents(Mono<VentObject> object) {
        return object.map(o -> {
            //todo: we either need a deep copy utility for map-represented JSON, or we need to remember that saving the parameter will be disastrous
            Map result = o.getLastSnapshot();
            for (Vent vent: o.getEvents()){
                switch (vent.getOperation()) {
                    //fixme: magic string duplicated here and in DelegatingRequestHandlingService
                    case CREATE: {
                        result = (Map) vent.
                            getPayload().
                            getOrDefault("initialValue", new HashMap<>());
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
                    case LINK: {
                        applyLink(
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
                    case DELETE: {
                        result = null;
                    }
                    default:
                }
            }
            return result;
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

    private void applyLink(Map result, String path, Object val){
        throw new NotImplementedException("Links are not part of MVP");
    }

    private void applyRemove(Map result, String path){
        resolver.resolve(result, path).delete();
    }
}

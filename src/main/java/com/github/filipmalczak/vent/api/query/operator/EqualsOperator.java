package com.github.filipmalczak.vent.api.query.operator;

import com.github.filipmalczak.vent.embedded.model.events.impl.PutValue;
import com.github.filipmalczak.vent.embedded.model.events.impl.Update;
import com.github.filipmalczak.vent.helper.PathUtils;
import com.github.filipmalczak.vent.velvet.UnresolvablePathException;
import com.github.filipmalczak.vent.velvet.Velvet;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.filipmalczak.vent.helper.Struct.*;

@Value(staticConstructor = "with")
public class EqualsOperator implements Operator {
    private final String path;
    private final Object value;

    private Map initialStateQueryLevel(String[] parts, int i){
        if (i == parts.length-1)
            return pair(parts[i], value);
        //todo indexing
        return pair(parts[i], initialStateQueryLevel(parts, i+1));
    }

    @Override
    public Map<String, Object> toMongoInitialStateCriteria() {
        String[] parts = path.split("[.]");
        return initialStateQueryLevel(parts, 0);
//        return pair(path, value);
    }

    // https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo-template.type-mapping
    // enforce type hints in our code, so if Spring Data changes the approach, we can still use following impl
    @Override
    public Map<String, Object> toMongoEventCriteria() {
        List<String> superPaths = PathUtils.superPaths(path);
        if (superPaths.isEmpty())
            return pair(
                "$or",
                list(
                    map(
                        //exactly that value was put under exactly that path
                        pair("_class", PutValue.class.getCanonicalName()),
                        pair("path", path),
                        pair("value", value)
                    ),
                    //fixme see com.github.filipmalczak.vent.embedded.EmbeddedReactiveVentCollection.update()
                    pair("_class", Update.class.getCanonicalName())//todo refactor ofClass(Class) -> pair("_class", canonicalname)
                )
            );
        return pair(
            "$or",
            list(
                map(
                    pair("_class", PutValue.class.getCanonicalName()),
                    pair(
                        "$or",
                        list(
                            //exactly that value was put under exactly that path
                            map(pair("path", path), pair("value", value)),
                            //some object (not a primitive val) was put under some superpath
                            map(
                                //if path was of form a.b (2 components) then check whether there was "PUT object under a"
                                //if path was longer like a.b.c, then check "PUT object under a or a.b"
                                pair("path",
                                    superPaths.size() > 1 ?
                                        pair("$in", superPaths) :
                                        superPaths.get(0)
                                ),
                                pair("value", pair("$type", "object"))
                            )
                        )
                    )
                ),
                //fixme see com.github.filipmalczak.vent.embedded.EmbeddedReactiveVentCollection.update()
                pair("_class", Update.class.getCanonicalName())//todo refactor ofClass(Class) -> pair("_class", canonicalname)
            )
        );
    }

    @Override
    public Predicate<Map> toRuntimeCriteria() {
        return this::check;
    }

    private boolean check(Map o){
        try {
            return Velvet.parse(path).bind(o).get().equals(value);
        } catch (UnresolvablePathException e){
            return false;
        }
    }
}

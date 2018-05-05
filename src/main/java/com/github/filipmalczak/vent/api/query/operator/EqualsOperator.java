package com.github.filipmalczak.vent.api.query.operator;

import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.embedded.model.events.PutValue;
import com.github.filipmalczak.vent.embedded.model.events.Update;
import com.github.filipmalczak.vent.helper.PathUtils;
import com.github.filipmalczak.vent.velvet.UnresolvablePathException;
import com.github.filipmalczak.vent.velvet.Velvet;
import lombok.Value;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.filipmalczak.vent.helper.Struct.list;
import static com.github.filipmalczak.vent.helper.Struct.map;
import static com.github.filipmalczak.vent.helper.Struct.pair;
import static java.util.Arrays.copyOfRange;
import static java.util.stream.IntStream.range;

@Value(staticConstructor = "with")
public class EqualsOperator implements Operator {
    private final String path;
    private final Object value;

    @Override
    public Map<String, Object> toMongoInitialStateCriteria() {
        return null;
    }

    // https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo-template.type-mapping
    // enforce type hints in our code, so if Spring Data changes the approach, we can still use following impl
    @Override
    public Map<String, Object> toMongoEventCriteria() {
        return pair(
            "$or",
            list(
                map(
                    pair("_class", PutValue.class),
                    pair(
                        "$or",
                        list(
                            //exactly that value was put under exactly that path
                            map(pair("path", path), pair("value", value)),
                            //some object (not a primitive val) was put under some superpath
                            map(
                                pair("path", pair("$in", superPaths())),
                                pair("$type", "object")
                            )
                        )
                    )
                ),
                //fixme see com.github.filipmalczak.vent.embedded.EmbeddedReactiveVentCollection.update()
                pair("_class", Update.class)
            )
        );
    }

    private List<String> superPaths(){
        return PathUtils.superPaths(path);
    }

    @Override
    public Predicate<ObjectSnapshot> toRuntimeCriteria() {
        return this::check;
    }

    private boolean check(ObjectSnapshot o){
        try {
            return Velvet.parse(path).bind(o).get().equals(value);
        } catch (UnresolvablePathException e){
            return false;
        }
    }
}

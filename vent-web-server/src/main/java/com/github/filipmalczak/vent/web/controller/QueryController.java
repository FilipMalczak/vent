package com.github.filipmalczak.vent.web.controller;

import com.github.filipmalczak.vent.web.model.query.ExecuteQueryRequest;
import com.github.filipmalczak.vent.web.model.query.OperationNotSupportedException;
import com.github.filipmalczak.vent.web.service.QueryExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.github.filipmalczak.vent.web.paths.CommonPaths.QUERY;
import static com.github.filipmalczak.vent.web.paths.CommonPaths.QUERY_WITH_TIME;

@RestController
public class QueryController {
    @Autowired
    private QueryExecutorService executorService;

    @PostMapping(QUERY)
    public Flux executeQuery(@PathVariable String name, @RequestBody ExecuteQueryRequest request,
                             @RequestParam(required = false) Optional<LocalDateTime> queryAt){
        switch (request.getOperation()){
            case FIND: return executorService.find(name, request.getRootNode(), queryAt);
            case COUNT: return executorService.count(name, request.getRootNode(), queryAt).flux();
            case EXISTS: return executorService.exists(name, request.getRootNode(), queryAt).flux();
            default: throw new OperationNotSupportedException(request.getOperation());
        }
    }
}

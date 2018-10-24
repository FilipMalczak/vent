package com.github.filipmalczak.vent.mongo.factory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class PluggableFactory<This extends PluggableFactory<This, Result, Config, ExtensionAPI>,
                                        Result, Config, ExtensionAPI> implements Factory<Result, Config, ExtensionAPI> {
    private List<FactoryPlugin<Result, ExtensionAPI>> plugins = new LinkedList<>();

    public This plugin(FactoryPlugin<Result, ExtensionAPI> plugin){
        plugins.add(plugin);
        return (This) this;
    }

    public This plugins(FactoryPlugin<Result, ExtensionAPI>... plugins){
        this.plugins.addAll(Arrays.asList(plugins));
        return (This) this;
    }

    public This clearPlugins(){
        plugins.clear();
        return (This) this;
    }

    @Override
    public Result create(Config config) {
        ResultWithAPI<Result, ExtensionAPI> result = prepare(config);
        for (FactoryPlugin<Result, ExtensionAPI> plugin: plugins)
            result = plugin.process(result);
        return result.getResult();
    }
}

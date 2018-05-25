package com.github.filipmalczak.vent.velvet;

import com.github.filipmalczak.vent.velvet.impl.ByNameSelector;
import com.github.filipmalczak.vent.velvet.impl.Selector;

public class Velvet {
    public static UnboundPath parse(String velvetPath){
        Selector firstSelector = null;
        Selector lastSelector = null;
        for (String partWithIdxs: velvetPath.split("[.]")){
            int idxOfFirstLeftBracket = partWithIdxs.indexOf('[');
            String name = partWithIdxs;
            if (idxOfFirstLeftBracket >= 0)
                name = name.substring(0, idxOfFirstLeftBracket);
            Selector nameSelector = lastSelector != null ? lastSelector.byName(name) : new ByNameSelector(lastSelector, null, name);
            lastSelector = nameSelector;
            if (firstSelector == null)
                firstSelector = lastSelector;
            if (idxOfFirstLeftBracket >= 0) {
                String indexes = partWithIdxs.substring(idxOfFirstLeftBracket);
                while (indexes.length() > 0){
                    if (indexes.charAt(0) != '[')
                        throw new RuntimeException("");//todo
                    int idxOfFirstRightbracket = indexes.indexOf(']');
                    if (idxOfFirstRightbracket < 1)
                        throw new RuntimeException("");//todo
                    String number = indexes.substring(1, idxOfFirstRightbracket);
                    int index = Integer.parseInt(number); //todo: rethrow properly
                    lastSelector = lastSelector.byIndex(index);
                    indexes = indexes.substring(idxOfFirstRightbracket+1);
                }
            }
        }
        Selector finalLastSelector = firstSelector;
        return new UnboundPath() {
            @Override
            public String getPath() {
                return velvetPath;
            }

            @Override
            public Selector getRootSelector() {
                return finalLastSelector;
            }
        };
    }
}

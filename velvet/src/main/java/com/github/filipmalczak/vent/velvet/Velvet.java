package com.github.filipmalczak.vent.velvet;

import com.github.filipmalczak.vent.velvet.impl.ByNameSelector;
import com.github.filipmalczak.vent.velvet.impl.Selector;

import java.util.LinkedList;
import java.util.List;

public class Velvet {
    public static UnboundPath parse(String velvetPath) {
        Selector firstSelector = null;
        Selector lastSelector = null;
        List<String> subPaths = new LinkedList<>();
        String currentSubPath = null;
        for (String partWithIdxs : velvetPath.split("[.]")) {
            int idxOfFirstLeftBracket = partWithIdxs.indexOf('[');
            String name = partWithIdxs;
            if (idxOfFirstLeftBracket >= 0)
                name = name.substring(0, idxOfFirstLeftBracket);
            Selector nameSelector;
            if (lastSelector == null) {
                nameSelector = new ByNameSelector(lastSelector, null, name);
                currentSubPath = name;
            } else {
                nameSelector = lastSelector.byName(name);
                currentSubPath += nameSelector.getUnparsedSelector();
            }
            subPaths.add(currentSubPath);
            lastSelector = nameSelector;
            if (firstSelector == null)
                firstSelector = lastSelector;
            if (idxOfFirstLeftBracket >= 0) {
                String indexes = partWithIdxs.substring(idxOfFirstLeftBracket);
                while (indexes.length() > 0) {
                    if (indexes.charAt(0) != '[')
                        throw new RuntimeException("");//todo
                    int idxOfFirstRightbracket = indexes.indexOf(']');
                    if (idxOfFirstRightbracket < 1)
                        throw new RuntimeException("");//todo
                    String number = indexes.substring(1, idxOfFirstRightbracket);
                    int index = Integer.parseInt(number); //todo: rethrow properly
                    lastSelector = lastSelector.byIndex(index);
                    currentSubPath += lastSelector.getUnparsedSelector();
                    subPaths.add(currentSubPath);
                    indexes = indexes.substring(idxOfFirstRightbracket + 1);
                }
            }
        }
        Selector finalFirstSelector = firstSelector;
        return new UnboundPath() {
            @Override
            public String getPath() {
                return velvetPath;
            }

            @Override
            public List<String> getSuperPaths() {
                //todo: test this
                return subPaths;
            }

            @Override
            public Selector getRootSelector() {
                return finalFirstSelector;
            }
        };
    }
}

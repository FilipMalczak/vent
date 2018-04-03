package com.github.filipmalczak.vent.helper.resolver;

import lombok.Value;

import java.util.Map;

@Value(staticConstructor = "from")
class MemberOf implements ResolvedPath {
    private Map parent;
    private String memberName;

    @Override
    public boolean exists() {
        return parent.containsKey(memberName);
    }

    @Override
    public Object get() {
        return parent.get(memberName);
    }

    @Override
    public void set(Object o) {
        parent.put(memberName, o);
    }

    @Override
    public void delete() {
        parent.remove(memberName);
    }
}

package ru.sovcombank.rbs.core;

import org.jspecify.annotations.NonNull;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DbParams implements Iterable<DbParam> {
    private final Map<String, DbParam> items;

    public DbParams() {
        items = new LinkedCaseInsensitiveMap<>();
    }

    public DbParams(Collection<DbParam> params) {
        items = new LinkedCaseInsensitiveMap<>(params.size());
        params.forEach(dbParam -> items.put(dbParam.getName(), dbParam));
    }

    @Override
    @NonNull
    public Iterator<DbParam> iterator() {
        return items.values().iterator();
    }

    public int size() {
        return items.size();
    }


    public boolean isEmpty() {
        return items.isEmpty();
    }


    public boolean containsKey(Object key) {
        return items.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return items.containsValue(value);
    }


    public DbParam get(Object key) {
        return items.get(key);
    }

    private DbParam put(String key, DbParam value) {
        if (items.containsKey(key)) {
            throw new IllegalArgumentException("Параметр [" + key + "] уже добавлен");
        }
        return items.put(key, value);
    }


    public DbParam remove(Object key) {
        return items.remove(key);
    }


    public void putAll(@NonNull Map<? extends String, ? extends DbParam> m) {
        m.keySet().forEach(s -> {
            if (items.containsKey(s)) {
                throw new IllegalArgumentException("Параметр [" + s + "] уже добавлен");
            }
        });
        items.putAll(m);
    }

    public void clear() {
        items.clear();
    }

    @NonNull
    public Set<String> keySet() {
        return items.keySet();
    }

    @NonNull
    public Collection<DbParam> values() {
        return items.values();
    }

    @NonNull
    public Set<Map.Entry<String, DbParam>> entrySet() {
        return items.entrySet();
    }

    public DbParam add(@NonNull DbParam dbParam) {
        return put(dbParam.getName(), dbParam);
    }

    public DbParam add(@NonNull String name, ParamMode paramMode, DbTypes paramType, Object value) {
        DbParam param = new DbParam();
        param.setName(name);
        param.setParamType(paramType);
        param.setParamMode(paramMode);
        param.setValue(value);
        return put(name, param);
    }

    public void addAll(DbParams params) {
        items.putAll(params.items);
    }

    @NonNull
    public Iterable<DbParam> inParams() {
        return items.values().stream()
                .filter(dbParam -> dbParam.getParamMode() == ParamMode.IN ||
                        dbParam.getParamMode() == ParamMode.IN_OUT).toList();
    }

    @NonNull
    public Iterable<DbParam> outParams() {
        return items.values().stream()
                .filter(dbParam -> dbParam.getParamMode() == ParamMode.OUT ||
                        dbParam.getParamMode() == ParamMode.IN_OUT).toList();
    }
}

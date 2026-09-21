package com.veyron.base.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private record Handler(Object owner, Method method, Class<? extends Event> type) {
    }

    private final Map<Class<? extends Event>, List<Handler>> handlers = new ConcurrentHashMap<>();
    private final Map<Object, List<Handler>> byOwner = new ConcurrentHashMap<>();

    public void register(Object listener) {
        if (byOwner.containsKey(listener)) return;

        List<Handler> owned = new CopyOnWriteArrayList<>();

        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(EventHandler.class)) continue;
            if (method.getParameterCount() != 1) continue;

            Class<?> param = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(param)) continue;

            method.setAccessible(true);

            @SuppressWarnings("unchecked")
            Class<? extends Event> type = (Class<? extends Event>) param;

            Handler h = new Handler(listener, method, type);
            handlers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(h);
            owned.add(h);
        }

        if (!owned.isEmpty()) {
            byOwner.put(listener, owned);
        }
    }

    public void unregister(Object listener) {
        List<Handler> owned = byOwner.remove(listener);
        if (owned == null) return;
        for (Handler h : owned) {
            List<Handler> list = handlers.get(h.type());
            if (list != null) list.remove(h);
        }
    }

    public void post(Event event) {
        List<Handler> list = handlers.get(event.getClass());
        if (list == null || list.isEmpty()) return;

        for (Handler h : list) {
            try {
                h.method().invoke(h.owner(), event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("EventBus dispatch failed: " + h.method(), e);
            }
        }
    }
}

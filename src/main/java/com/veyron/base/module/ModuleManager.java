package com.veyron.base.module;

import com.veyron.base.event.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ModuleManager {
    private final List<Module> modules = new CopyOnWriteArrayList<>();
    private final EventBus eventBus;

    public ModuleManager(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Сюда добавляй свои модули. По умолчанию пусто (база без функций).
     * Пример:
     * <pre>
     *   register(new MyModule());
     * </pre>
     */
    public void load() {
        // modules are empty by design - add your own here
        // register(new ExampleModule());
    }

    public void register(Module module) {
        if (getByName(module.getName()) != null) return;
        modules.add(module);
    }

    public void unregister(Module module) {
        module.disable();
        modules.remove(module);
    }

    public List<Module> getModules() {
        return new ArrayList<>(modules);
    }

    public List<Module> getEnabled() {
        return modules.stream().filter(Module::isEnabled).collect(Collectors.toList());
    }

    public List<Module> getByCategory(Category category) {
        return modules.stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
    }

    public Module getByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }
}

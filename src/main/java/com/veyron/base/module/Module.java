package com.veyron.base.module;

import com.veyron.base.setting.Setting;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Базовый класс любого модуля. Функций внутри нет -
 * только включение/выключение, бинд и подписка на EventBus.
 *
 * Пример своего модуля:
 * <pre>
 * public class MyModule extends Module {
 *     public MyModule() {
 *         super("MyModule", "test", Category.MISC, GLFW.GLFW_KEY_R);
 *     }
 *
 *     {@literal @}Override
 *     protected void onEnable() {
 *         Veyron.getInstance().getEventBus().register(this);
 *     }
 *
 *     {@literal @}Override
 *     protected void onDisable() {
 *         Veyron.getInstance().getEventBus().unregister(this);
 *     }
 *
 *     {@literal @}EventHandler
 *     public void onTick(TickEvent e) {
 *         // логика
 *     }
 * }
 * </pre>
 */
public abstract class Module {
    private final String name;
    private final String description;
    private final Category category;

    private int key = GLFW.GLFW_KEY_UNKNOWN;
    private boolean enabled = false;
    protected final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String description, Category category) {
        this(name, description, category, GLFW.GLFW_KEY_UNKNOWN);
    }

    public Module(String name, String description, Category category, int key) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    protected <T extends Setting<?>> T addSetting(T setting) {
        settings.add(setting);
        return setting;
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void enable() {
        setEnabled(true);
    }

    public void disable() {
        setEnabled(false);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }
}

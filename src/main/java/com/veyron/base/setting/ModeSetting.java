package com.veyron.base.setting;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;

    public ModeSetting(String name, String def, String... modes) {
        super(name, def);
        this.modes = Arrays.asList(modes);
    }

    public boolean is(String mode) {
        return get().equalsIgnoreCase(mode);
    }

    public List<String> getModes() {
        return modes;
    }

    public void next() {
        int i = modes.indexOf(get());
        set(modes.get((i + 1) % modes.size()));
    }
}

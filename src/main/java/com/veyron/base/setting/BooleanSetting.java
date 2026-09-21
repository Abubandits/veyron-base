package com.veyron.base.setting;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, boolean value) {
        super(name, value);
    }

    public boolean isOn() {
        return get();
    }

    public void toggle() {
        set(!get());
    }
}

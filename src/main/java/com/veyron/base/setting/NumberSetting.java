package com.veyron.base.setting;

public class NumberSetting extends Setting<Float> {
    private final float min;
    private final float max;
    private final float step;

    public NumberSetting(String name, float value, float min, float max, float step) {
        super(name, value);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    public void set(Float value) {
        super.set(Math.max(min, Math.min(max, value)));
    }

    public float getFloat() {
        return get();
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getStep() {
        return step;
    }
}

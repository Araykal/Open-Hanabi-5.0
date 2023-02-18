package life.hanabi.core.values.values;

import life.hanabi.core.values.Value;

public class NumberValue<T extends Number> extends Value<T> {
    /** 单次可拖动的数值 */
    T inc;
    T min;
    T max;
    public boolean drag;

    public NumberValue(String name, T value, T min, T max, T inc, boolean... objects) {
        super(name);
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
        this.inc = inc;
        this.setHidden(objects.length != 0 && objects[0]);
    }

    public NumberValue(String name, String desc, T value, T min, T max, T inc, boolean... objects) {
        super(name);
        this.name = name;
        this.desc = desc;
        this.value = value;
        this.min = min;
        this.max = max;
        this.inc = inc;
        this.setHidden(objects.length != 0 && objects[0]);
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public T getInc() {
        return inc;
    }
}

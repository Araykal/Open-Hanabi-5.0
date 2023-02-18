package life.hanabi.core.values;

import life.hanabi.utils.math.AnimationUtils;

public class Value<T> {
    protected T value;
    public String name, desc;
    private boolean hidden;
    public AnimationUtils animationutil = new AnimationUtils();
    public float animation;

    public Value(String name) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}

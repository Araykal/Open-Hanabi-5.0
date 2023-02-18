package life.hanabi.core.values.values;

import life.hanabi.core.values.Value;
import life.hanabi.utils.math.AnimationUtils;

public class ModeValue extends Value<String> {
    String[] modes;

    boolean expanded;


    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public ModeValue(String name, String current, String... modes) {
        super(name);
        this.value = current;
        this.modes = modes;
    }

    public String getCurrent() {
        return value;
    }

    public void setCurrent(String current) {
        this.value = current;
    }

    @Override
    public String getValue() {
        return getCurrent();
    }

    public String[] getModes() {
        return modes;
    }

    public void setModes(String[] modes) {
        this.modes = modes;
    }

}

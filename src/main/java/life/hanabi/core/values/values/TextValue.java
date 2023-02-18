package life.hanabi.core.values.values;

import life.hanabi.core.values.Value;

public class TextValue extends Value<String> {
    public TextValue(String name, String value, boolean... objects) {
        super(name);
        this.name = name;
        this.value = value;
        this.setHidden(objects.length != 0 && objects[0]);
    }
    public TextValue(String name, String desc, String value, boolean... objects) {
        super(name);
        this.name = name;
        this.desc = desc;
        this.value = value;
        this.setHidden(objects.length != 0 && objects[0]);
    }
}

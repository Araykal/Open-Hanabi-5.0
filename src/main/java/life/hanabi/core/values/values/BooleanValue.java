package life.hanabi.core.values.values;

import life.hanabi.core.values.Value;

public class BooleanValue extends Value<Boolean> {
    public BooleanValue(String name, boolean value, boolean... objects){
        super(name);
        this.name = name;
        this.value = value;
        this.setHidden(objects.length != 0 && objects[0]);
    }
    public BooleanValue(String name, String desc, boolean value, boolean... objects){
        super(name);
        this.name = name;
        this.desc = desc;
        this.value = value;
        this.setHidden(objects.length != 0 && objects[0]);
    }
}

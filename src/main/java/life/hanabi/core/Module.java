package life.hanabi.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.realmsclient.gui.ChatFormatting;
import life.hanabi.Hanabi;
import life.hanabi.core.values.values.BooleanValue;
import life.hanabi.core.values.values.ColorValue;
import life.hanabi.core.values.values.ModeValue;
import life.hanabi.core.values.values.NumberValue;
import life.hanabi.gui.notification.Notification;
import life.hanabi.core.I18N.I18NUtils;
import life.hanabi.core.values.Value;
import life.hanabi.event.EventManager;
import life.hanabi.modules.render.ClickGui;
import life.hanabi.modules.render.NotificationModule;
import life.hanabi.utils.math.AnimationUtils;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Super
 */
public class Module {
    public String name;
    public String desc;
    public String displayName;
    private String suffix;
    private boolean hasSuffix;

    public boolean stage;
    public int key;
    public ModuleCategory type;
    public AnimationUtils optionAnimationUtils = new AnimationUtils();
    public float optionAnimationX;
    public float x, y, width, height, scale = 1;
    public static Minecraft mc = Minecraft.getMinecraft();
    public float valueAnimationY;
    public ArrayList<Value<?>> values = new ArrayList<>();
    public boolean canBeEnabled;
    public double arrayListAnim;
    public AnimationUtils arrayListA = new AnimationUtils();
    public double arrayListAnim2;
    public AnimationUtils arrayListB = new AnimationUtils();
    public double arrayListAnim3;
    public AnimationUtils arrayListC = new AnimationUtils();


    public ArrayList<Value<?>> getValues() {
        ArrayList<Value<?>> validValues = new ArrayList<>();
        values.forEach(value -> {
            if (!value.isHidden()) {
                validValues.add(value);
            }
        });
        return validValues;
    }

    public void addValues(Value<?>... vs) {
        values.addAll(Arrays.asList(vs));
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public Module(String name, ModuleCategory type) {
        this.name = name;
        this.type = type;
        this.canBeEnabled = true;
    }

    public Module(String name, String desc, ModuleCategory type) {
        this.name = name;
        this.desc = desc;
        this.type = type;
        this.canBeEnabled = true;
    }

    public void onGui() {

    }

    public void onEnable() {
    }

    public void onDisable() {

    }

    public void setStage(boolean stage) {
        if (!this.canBeEnabled) return;
        this.stage = stage;
        if (stage) {
            EventManager.register(this);
            if (mc.theWorld != null) {
                onEnable();
                mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "random.click", 1, 0.6f, false);
            }
            if (!(this instanceof ClickGui) && Hanabi.INSTANCE.notificationsManager != null && NotificationModule.showModuleToggle.getValue())
                Hanabi.INSTANCE.notificationsManager.add(new Notification(I18NUtils.getString(this.name) + ChatFormatting.GRAY + " Enabled", Notification.Type.Success));
        } else {
            EventManager.unregister(this);
            if (mc.theWorld != null) {
                onDisable();

                mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "random.click", 1, 1, false);
            }
            if (!(this instanceof ClickGui) && Hanabi.INSTANCE.notificationsManager != null && NotificationModule.showModuleToggle.getValue())
                Hanabi.INSTANCE.notificationsManager.add(new Notification(I18NUtils.getString(this.name) + ChatFormatting.GRAY + " Disabled", Notification.Type.Error));
        }
    }

    public String getDisplayName() {
        return hasSuffix ? displayName == null ? name + "\2477 " + suffix : displayName + "\2477 " + suffix : name;
    }

    public void setSuffix(String displayName) {
        this.displayName = displayName;
    }

    public void toggle() {
        setStage(!this.stage);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return 1f;
    }

    public void onRight() {

    }

    public void setValues(ArrayList<Value<?>> values) {
        try {
            for (Value<?> v : values) {
                for (Value<?> thisValue : this.values) {
                    if (thisValue.name.equals(v.name) && thisValue.getClass().equals(v.getClass())) {
                        Field field = thisValue.getClass().getSuperclass().getDeclaredField("value");
                        field.setAccessible(true);
                        field.set(thisValue, v.getValue());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(name + "  " + values.toString());
            e.printStackTrace();
        }
    }

    public void fromJson(JsonObject je) {
        if (je != null) {
            setStage(je.get("stage").getAsBoolean());
            setScale(je.get("scale").getAsFloat());
            setX(je.get("x").getAsFloat());
            setY(je.get("y").getAsFloat());
            setKey(je.get("key").getAsInt());
            for (Value<?> v : values) {
                try {
                    if (v instanceof BooleanValue) {
                        JsonPrimitive value = je.getAsJsonPrimitive(v.name);
                        ((BooleanValue) v).setValue(value.getAsBoolean());
                    } else if (v instanceof NumberValue) {
                        JsonPrimitive value = je.getAsJsonPrimitive(v.name);
                        ((NumberValue) v).setValue(value.getAsDouble());
                    } else if (v instanceof ModeValue) {
                        JsonPrimitive value = je.getAsJsonPrimitive(v.name);
                        ((ModeValue) v).setCurrent(value.getAsString());
                    } else if (v instanceof ColorValue) {
                        JsonObject value = je.getAsJsonObject(v.name);
                        Color color = new Color(value.get("red").getAsInt(), value.get("green").getAsInt(), value.get("blue").getAsInt(), value.get("alpha").getAsInt());
                        ((ColorValue) v).setValue(color);
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                    System.out.println("missing value " + v.name + " in " + name);
                }
            }
        }

    }
}

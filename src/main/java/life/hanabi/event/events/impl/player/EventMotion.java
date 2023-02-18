package life.hanabi.event.events.impl.player;

import life.hanabi.event.events.Event;

public class EventMotion implements Event {

    public double x;
    public double y;
    public double z;
    public boolean onGround;
    public float pitch;
    public float yaw;
    private float lastPitch;
    private float lastYaw;

    public EventType type;

    public EventMotion(EventType e, double x, double y, double z, boolean onGround, float yaw, float pitch, float lastYaw, float lastPitch) {
        this.type = e;
        this.x = x;
        this.y = y;
        this.z = z;
        this.onGround = onGround;
        this.pitch = pitch;
        this.yaw = yaw;
        this.lastPitch = lastPitch;
        this.lastYaw = lastYaw;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public boolean isPre() {
        return getType().equals(EventType.PRE);
    }

    public boolean isPost() {
        return getType().equals(EventType.POST);
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getLastPitch() {
        return this.lastPitch;
    }

    public void setLastPitch(float lastPitch) {
        this.lastPitch = lastPitch;
    }

    public float getLastYaw() {
        return this.lastYaw;
    }

    public void setLastYaw(float lastYaw) {
        this.lastYaw = lastYaw;
    }

}

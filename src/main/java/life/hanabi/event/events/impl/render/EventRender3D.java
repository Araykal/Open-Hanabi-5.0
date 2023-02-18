package life.hanabi.event.events.impl.render;

import life.hanabi.event.events.impl.player.EventType;
import life.hanabi.event.events.Event;

public class EventRender3D implements Event {
    public EventType type;
    public float partialTicks;

    public EventRender3D(EventType e, float partialTicks) {
        this.type = e;
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

}

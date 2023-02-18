package life.hanabi.event.events.impl.render;

import life.hanabi.event.events.Event;

public class EventRender2D implements Event {
    public float partialTicks;

    public EventRender2D(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}

package life.hanabi.event.events.impl.player;

import life.hanabi.event.events.Event;

public class EventSafeWalk implements Event {
    public boolean safeWalk;

    public void setSafe(boolean b) {
        safeWalk = b;
    }
}

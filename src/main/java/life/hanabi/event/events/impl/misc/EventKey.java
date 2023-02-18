package life.hanabi.event.events.impl.misc;

import life.hanabi.event.events.Event;

public class EventKey implements Event {
    public int key;

    public EventKey(int key) {
        this.key = key;
    }

}

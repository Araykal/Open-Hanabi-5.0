package life.hanabi.event.events.impl.player;

import life.hanabi.event.events.Cancellable;
import life.hanabi.event.events.Event;

public class EventNoSlow implements Event, Cancellable {

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean state) {
        cancelled = state;
    }

}

package life.hanabi.event.events.impl.client;

import life.hanabi.event.events.Event;

public class EventMouse implements Event {
    private final Button button;

    public EventMouse(Button button) {
        this.button = button;
    }
    public Button getButton() {
        return this.button;
    }


    public enum Button {
        Left,
        Right,
        Middle
    }
}

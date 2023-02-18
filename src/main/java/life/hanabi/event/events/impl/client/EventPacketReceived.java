package life.hanabi.event.events.impl.client;

import life.hanabi.event.events.Event;
import net.minecraft.network.Packet;

public class EventPacketReceived implements Event {
    private Packet<?> packet;
    private boolean cancel;

    public EventPacketReceived(Packet p) {
        packet = p;
    }

    public void cancel() {
        cancel = true;
    }

    public Packet getPacket() {
        return packet;
    }

    public boolean isCancel() {
        return cancel;
    }
}

package life.hanabi.event.events.impl.client;

import life.hanabi.event.events.Cancellable;
import life.hanabi.event.events.Event;
import net.minecraft.network.Packet;

public class EventPacketSend implements Event, Cancellable {
    public Packet packet;
    public boolean cancel = false;
    public EventPacketSend(Packet p){
        packet = p;
    }

    public Packet getPacket() {
        return packet;
    }


    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean state) {
        cancel = state;
    }
}

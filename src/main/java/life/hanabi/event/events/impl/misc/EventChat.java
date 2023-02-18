package life.hanabi.event.events.impl.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import life.hanabi.event.events.Cancellable;
import life.hanabi.event.events.Event;
import net.minecraft.util.IChatComponent;

/**
 * @description: CHATTTTTTTTTTTTTT
 * @author: QianXia
 * @create: 2021/08/19 00:54
 **/
public class EventChat implements Event, Cancellable {
    public IChatComponent chatMessage;
    private boolean cancelled;
    private Type type;

    public EventChat(IChatComponent chatMessage, Type type) {
        this.chatMessage = chatMessage;
        this.type = type;
    }

    public String getChatMessage() {
        return chatMessage.getFormattedText();
    }

    public void setChatMessage(IChatComponent chatMessage) {
        this.chatMessage = chatMessage;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean state) {
        cancelled = state;
    }

    public enum Type{
        RECEIVE, SEND
    }
}

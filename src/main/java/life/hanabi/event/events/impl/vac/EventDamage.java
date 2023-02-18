package life.hanabi.event.events.impl.vac;

import net.minecraft.util.DamageSource;
import life.hanabi.event.events.Event;

public class EventDamage implements Event {
    private final DamageSource damageSource;

    public EventDamage(DamageSource damageSource) {
        this.damageSource = damageSource;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }
}

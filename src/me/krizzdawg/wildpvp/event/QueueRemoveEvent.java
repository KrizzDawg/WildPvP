package me.krizzdawg.wildpvp.event;

import lombok.Getter;
import lombok.Setter;
import me.krizzdawg.wildpvp.queue.WildQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class QueueRemoveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private WildQueue queue;
    private @Deprecated Player host; // Host may be null after queue is removed.

    public QueueRemoveEvent(WildQueue queue) {
        this.host = queue.getHost();
        this.queue = queue;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

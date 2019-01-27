package me.krizzdawg.wildpvp.event;

import lombok.Getter;
import lombok.Setter;
import me.krizzdawg.wildpvp.queue.WildQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class QueueCreateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Player host;
    private WildQueue queue;
    private boolean cancelled;

    public QueueCreateEvent(Player host, WildQueue queue) {
        this.host = host;
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

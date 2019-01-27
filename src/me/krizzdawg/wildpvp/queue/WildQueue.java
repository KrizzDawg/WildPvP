package me.krizzdawg.wildpvp.queue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.util.TimeUtil;
import me.krizzdawg.wildpvp.WildPvPPlugin;
import me.krizzdawg.wildpvp.util.InvincibilityTimer;
import me.krizzdawg.wildpvp.util.KrizzUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class WildQueue {

    private Map<UUID, Location> previousLocationMap = Maps.newHashMap();
    private Location creationLocation;
    private InvincibilityTimer invincibilityTimer;
    private Player host;
    private Player opponent;
    private QueueState state;
    private long timeInitiated;
    private boolean hidden;

    public WildQueue(Player host) {
        this.host = host;
        this.creationLocation = host.getLocation();
        this.timeInitiated = System.currentTimeMillis();
        this.state = QueueState.IDLE;
    }

    public ItemStack getQueueDisplayItem(Player viewer) {
        // Faction & Relation
        FPlayer fviewer = FPlayers.getInstance().getByPlayer(viewer);
        FPlayer fhost = FPlayers.getInstance().getByPlayer(host);
        ChatColor relationColor = fviewer.getColorTo(fhost);
        // Item Lore
        List<String> lore = Lists.newArrayList();
        lore.add("&7" + TimeUtil.getTime(getUptime()));
        lore.add(StringUtils.EMPTY);
        lore.add("&a&lPlayer HP&a: &a&n" + new Double(host.getHealth()).intValue());
        lore.add(StringUtils.EMPTY);
        lore.add("&a&lFaction");
        lore.add(" &f" + fhost.getFaction().getTag(fviewer) + " &7(&a" + fhost.getFaction().getOnlinePlayers().size() + "&7 online)");
        lore.add(StringUtils.EMPTY);
        lore.add("&a&lNearby");
        lore.add(" &f" + KrizzUtil.getNearbyPlayers(host, 64).size() + " Player(s)");
        lore.add(StringUtils.EMPTY);
        lore.add("&7Left-Click to accept their request.");
        lore.add("&7Right-Click to view their inventory.");
        // Create Item
        return KrizzUtil.createPlayerSkull(host.getName(), relationColor.toString() + ChatColor.BOLD + host.getName(), lore);
    }

    public void addOpponentToQueue(Player opponent) {
        this.opponent = opponent;
        state = QueueState.INVINCIBILITY;
        invincibilityTimer = new InvincibilityTimer(this, 7);
        previousLocationMap.put(opponent.getUniqueId(), opponent.getLocation());
        opponent.teleport(creationLocation);

        opponent.sendMessage("");
        KrizzUtil.sendMessage(opponent, "&a&l(!)&a You have joined " + host.getName() + "'s Wild PvP Queue.");
        opponent.sendMessage("");

        host.sendMessage("");
        KrizzUtil.sendMessage(host, "&a&l(!) &f&l" + opponent.getName() + " &ahas joined &nYOUR&a Wild PvP Queue.\n");
        host.sendMessage("");
    }

    public void sendOpponentToPreviousLocation() {
        opponent.teleport(previousLocationMap.get(opponent.getUniqueId()));
        previousLocationMap.remove(opponent.getUniqueId());
    }

    public void removeOpponentAndRequeue() {
        KrizzUtil.sendMessage(host, "&c&l(!)&c&l " + opponent.getName() + "&c has left the &lWild PvP Queue&c.");
        KrizzUtil.sendMessage(host, "    &7&o(( You've been added back to the queue ))");
        opponent = null;
        state = QueueState.IDLE;
    }

    public boolean containsPlayer(Player player) {
        return host == player || opponent == player;
    }

    public List<Player> getBoth() {
        return Lists.newArrayList(host, opponent);
    }

    public void cancelQueue(Player whoCancelled) {
        removeInvincibility();
        getBoth().stream().filter(player -> player != null).forEach(player -> KrizzUtil.sendMessage(player, "&c&l(!) &c" + whoCancelled.getName() + " has left the pvp queue."));

        if (opponent != null) {
            sendOpponentToPreviousLocation();

            if (opponent == whoCancelled) {
                removeOpponentAndRequeue();
            }
        }

        if (host == whoCancelled) {
            host.teleport(WildPvPPlugin.getInstance().getSpawnLocation());
            WildPvPPlugin.getInstance().getQueueManager().removeExisitingQueue(this);
        }
    }

    public void removeInvincibility() {
        if (invincibilityTimer != null) {
            invincibilityTimer.remove();
            invincibilityTimer = null;
        }
    }

    public void hideChunkBorder() {
        KrizzUtil.getChunkBorderLocations(creationLocation, 3).forEach(loc -> loc.getBlock().getState().update());

    }

    public void showChunkBorder() {
        KrizzUtil.getChunkBorderLocations(creationLocation, 3).forEach(loc -> host.sendBlockChange(loc, Material.STAINED_GLASS, (byte) 14));
    }

    public int getUptime() {
        return (int) (System.currentTimeMillis() - timeInitiated) / 1000;
    }


    public void endSuccesfully() {
        state = QueueState.END;
        WildPvPPlugin.getInstance().getQueueManager().removeExisitingQueue(this);
    }

}

package me.krizzdawg.wildpvp.listener;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.event.LandClaimEvent;
import me.krizzdawg.wildpvp.WildPvPPlugin;
import me.krizzdawg.wildpvp.event.QueueCreateEvent;
import me.krizzdawg.wildpvp.event.QueueRemoveEvent;
import me.krizzdawg.wildpvp.queue.QueueState;
import me.krizzdawg.wildpvp.queue.WildQueue;
import me.krizzdawg.wildpvp.util.KrizzUtil;
import me.krizzdawg.wildpvp.util.PlayerInventoryDisplay;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class QueueListener implements Listener {

    private WildPvPPlugin plugin;

    public QueueListener(WildPvPPlugin plugin) {
        this.plugin = plugin;
    }


    //TODO
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
     /*
        Player player = event.getPlayer();
        Chunk fromChunk = event.getFrom().getChunk();
        Chunk toChunk = event.getTo().getChunk();

        Chunk queueChunk = player.getLocation().getChunk();
     */
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String cmd = event.getMessage().toLowerCase();
        WildQueue queue = plugin.getQueueManager().getQueueContainingPlayer(player);

        if (queue == null) {
            return;
        }

        if (cmd.contains("spawn") || cmd.contains("pvp leave")) {
            queue.cancelQueue(player);
            event.setCancelled(true);
            return;
        } else {
            KrizzUtil.sendMessage(player, "&c&l(!) Command Not Permitted (!)");
            KrizzUtil.sendMessage(player, "&7You must use &c/spawn&7 or &c/pvp leave&7 to exit.");
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onLandClaim(LandClaimEvent event) {
        FPlayer fPlayer = event.getfPlayer();
        Chunk chunk = event.getLocation().getChunk();

        if (plugin.getQueueManager().getQueueByChunk(chunk) != null) {
            KrizzUtil.sendMessage(fPlayer.getPlayer(), "&c&l(!)&c You cannot claim this land during a &lPvP Queue.");
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            WildQueue queue = plugin.getQueueManager().getQueueContainingPlayer(player);

            if (queue == null) {
                return;
            }

            if (queue.getState() == QueueState.INVINCIBILITY) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onQueueRemove(QueueRemoveEvent event) {
        WildQueue queue = event.getQueue();

        if (queue.getState() == QueueState.INVINCIBILITY) {
            queue.removeInvincibility();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        WildQueue queue = plugin.getQueueManager().getQueueContainingPlayer(player);

        if (queue == null) {
            return;
        }

        queue.cancelQueue(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onQueueCreate(QueueCreateEvent event) {
        Player player = event.getHost();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

        if (plugin.getQueueManager().getQueueByHost(player) != null) {
            KrizzUtil.sendMessage(player, "&c&l(!)&c You are already in the Wild PvP Queue.");
            KrizzUtil.sendMessage(player, "&7You may use &c/pvp leave&7 to exit the queue.");
            event.setCancelled(true);
            return;
        }

        if (!Board.getInstance().getFactionAt(fPlayer.getLastStoodAt()).isNone()) {
            KrizzUtil.sendMessage(player, "&c&l(!)&c You must be in &2Wilderness&c to create a queue.");
            event.setCancelled(true);
            return;
        }

        // Announce if the event isn't cancelled.

        Bukkit.broadcastMessage(KrizzUtil.color("&c&l>&f " + player.getName() + "&7 has created a &n/pvp match&7. &cDefeat them!"));
    }

    @EventHandler
    public void onQueueInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        Inventory inventory = event.getInventory();
        ItemStack item = event.getCurrentItem();

        if (!inventory.getTitle().endsWith(ChatColor.DARK_GRAY + "Wild PvP Queue") || inventory.getSize() != 9) {
            return;
        }
        event.setCancelled(true);

        // Clicked New Queue Item
        if (event.getRawSlot() == 8) {
            plugin.getQueueManager().createNewQueue(player);
            player.closeInventory();
            return;
        }

        WildQueue queue = plugin.getQueueManager().getQueueByItem(item);

        if (queue == null) {
            return;
        }

        // RIGHT CLICK to view inventory
        if (event.isRightClick()) {
            new PlayerInventoryDisplay(queue.getHost()).show(player);
            return;
        }

        if (plugin.getQueueManager().getQueueContainingPlayer(player) != null) {
            KrizzUtil.sendMessage(player, "&c&l(!)&c You are already in a pvp queue.");
            return;
        }

        if (!queue.getState().isJoinable()) {
            KrizzUtil.sendMessage(player, "&c&l(!)&c This queue currently cannot be joined.");
            return;
        }

        // LEFT CLICK to accept request
        queue.addOpponentToQueue(player);
    }

    @EventHandler
    public void onPreviewInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        if (inventory.getTitle().endsWith(ChatColor.GREEN + "'s Inventory")) {
            event.setCancelled(true);

            if (event.getRawSlot() == 45) {
                plugin.getQueueManager().openQueueInventory(player);
            }
        }
    }
}

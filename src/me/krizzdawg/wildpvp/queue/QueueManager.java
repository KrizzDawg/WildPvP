package me.krizzdawg.wildpvp.queue;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.krizzdawg.wildpvp.WildPvPPlugin;
import me.krizzdawg.wildpvp.event.QueueCreateEvent;
import me.krizzdawg.wildpvp.event.QueueRemoveEvent;
import me.krizzdawg.wildpvp.util.InventoryUpdateTask;
import me.krizzdawg.wildpvp.util.KrizzUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class QueueManager {

    private List<WildQueue> queues = Lists.newArrayList();
    private WildPvPPlugin plugin;

    public QueueManager(WildPvPPlugin plugin) {
        this.plugin = plugin;
    }

    public void openQueueInventory(Player player) {
        new InventoryUpdateTask(player, plugin.getQueueManager().getQueueInventory(player)) {

            @Override
            public ItemStack[] getUpdatedContents() {
                return plugin.getQueueManager().getQueueInventory(player).getContents();
            }
        };
        KrizzUtil.playSound(player, Sound.NOTE_PLING);
    }

    public Inventory getQueueInventory(Player viewer) {
        Inventory queueInventory = Bukkit.createInventory(null, 9, ChatColor.DARK_GRAY + "Wild PvP Queue");
        queues.stream().filter(queue -> queue.getHost() != viewer).forEach(queue -> queueInventory.addItem(queue.getQueueDisplayItem(viewer)));
        queueInventory.setItem(8, KrizzUtil.createItem(Material.STAINED_GLASS_PANE,
                "&a&lEnter Wild PvP Queue", 1, 5,
                "",
                "&7Click to create a &nWild PvP",
                "&7match at your current location.",
                "",
                "&a&lWhat is Wild PvP?",
                "&7Wild PvP is when a player or",
                "&7players fight inside Wilderness",
                "&7chunks (unclaimed land) to",
                "&7ensure the most fair fight.",
                "",
                "&7Once an opponent accepts your Wild PvP",
                "&7match they will be teleported to your",
                "&7location and you will each receive",
                "&77 seconds of immunity - then, you fight!"));
        return queueInventory;
    }

    public WildQueue getQueueByItem(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) {
            return null;
        }

        Player player = Bukkit.getPlayer(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()));

        if (player == null) {
            return null;
        }

        return getQueueByHost(player);
    }


    public WildQueue getQueueByChunk(Chunk chunk) {
        return queues.stream().filter(queue -> queue.getCreationLocation().getChunk().equals(chunk))
                .findFirst().orElse(null);
    }

    public WildQueue getQueueByHost(Player player) {
        return queues.stream().filter(queue -> queue.getHost().equals(player))
                .findFirst().orElse(null);
    }

    public WildQueue getQueueContainingPlayer(Player player) {
        return queues.stream().filter(queue -> queue.containsPlayer(player))
                .findFirst().orElse(null);
    }

    public void createNewQueue(Player host) {
        // Create Queue Object
        WildQueue queue = new WildQueue(host);
        queue.setTimeInitiated(System.currentTimeMillis());

        // Call Event & Handle Event
        QueueCreateEvent event = new QueueCreateEvent(host, queue);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }
        // Notify Player
        List<String> messages = Lists.newArrayList(
                "&a&l(!)&a You have entered into the Wild PvP queue!",
                "&7Please wait for an opponent to join.",
                StringUtils.EMPTY,
                "&c&lGlass Border &7- &cThe glass border displays the outside of the current chunk you are standing in," +
                        " this chunk will not be claimable until after player invincibility is removed.");
        KrizzUtil.sendMessage(host, messages);

        //  Generate border & add WildQueue to the List.
        queue.showChunkBorder();
        queues.add(queue);
    }

    public void removeExisitingQueue(WildQueue queue) {
        QueueRemoveEvent event = new QueueRemoveEvent(queue);
        Bukkit.getPluginManager().callEvent(event);
        queue.hideChunkBorder();
        queue.setHost(null);
        queue.setCreationLocation(null);
        queue.setOpponent(null);
        queue.setState(null);
        queue.setPreviousLocationMap(null);
        queues.remove(queue);
    }

}

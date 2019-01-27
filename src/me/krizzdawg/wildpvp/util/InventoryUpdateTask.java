package me.krizzdawg.wildpvp.util;

import me.krizzdawg.wildpvp.WildPvPPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class InventoryUpdateTask extends BukkitRunnable {

    private Player player;
    private Inventory inventory;

    public InventoryUpdateTask(Player player, Inventory inventory) {
        this.player = player;
        this.inventory = inventory;
        this.player.openInventory(inventory);
        this.runTaskTimer(WildPvPPlugin.getInstance(), 0, 20);
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            cancel();
            return;
        }

        if (!player.getOpenInventory().getTopInventory().getTitle().equals(inventory.getTitle())) {
            cancel();
            return;
        }

        inventory.setContents(getUpdatedContents());
    }

    public abstract ItemStack[] getUpdatedContents();

}

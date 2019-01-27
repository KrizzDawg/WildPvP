package me.krizzdawg.wildpvp.util;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ListIterator;

public class PlayerInventoryDisplay {

    private Inventory inventory;

    public PlayerInventoryDisplay(Player player) {
        inventory = Bukkit.createInventory(null, 54, ChatColor.GREEN + player
                .getName() + ChatColor.GREEN + "'s Inventory");

        inventory.setContents(player.getInventory().getContents());
        inventory.setItem(45, KrizzUtil.createItem(Material.ARROW, "&a&lReturn to Queue", "", "&7Click here to return to the", "&7Wild PvP queue menu."));

        ListIterator<Integer> slotIter = Lists.newArrayList(51, 50, 48, 47).listIterator();
        while (slotIter.hasNext()) {
            inventory.setItem(slotIter.next(), player.getInventory().getArmorContents()[slotIter.nextIndex() - 1]);
        }

    }

    public void show(Player viewer) {
        viewer.closeInventory();
        viewer.openInventory(inventory);
    }
}

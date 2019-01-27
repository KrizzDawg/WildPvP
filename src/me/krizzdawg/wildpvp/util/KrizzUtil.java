package me.krizzdawg.wildpvp.util;

import com.earth2me.essentials.Essentials;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KrizzUtil {

    private static final char COLOR_CHAR = '&';
    private static final String LOGGER_PREFIX = color("[AdvancedDuels] %level%:");

    public static ItemStack createItem(Material material, String name, int amount, int data, String... lore) {
        ItemStack item = new ItemStack(material, amount, (short) data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(name));
        meta.setLore(color(Arrays.asList(lore)));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, String name, String... lore) {
        return createItem(material, name, 1, 0, lore);
    }

    public static ItemStack createItem(Material material, String name, int amount, String... lore) {
        return createItem(material, name, amount, 1, lore);
    }

    public static ItemStack createItem(Material material, String name, int amount, int data, List<String> lore) {
        return createItem(material, name, amount, data, lore.toArray(new String[lore.size()]));
    }

    public static ItemStack createItem(Material material, String name, List<String> lore) {
        return createItem(material, name, lore.toArray(new String[lore.size()]));
    }

    public static ItemStack createPlayerSkull(String owner, String name, String... lore) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(owner);
        meta.setDisplayName(color(name));
        meta.setLore(color(Arrays.asList(lore)));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createPlayerSkull(String owner, String name, List<String> lore) {
        return createPlayerSkull(owner, name, lore.toArray(new String[lore.size()]));
    }

    public static ItemStack createEntitySkull(EntityType entityType, String name, String... lore) {
        return createPlayerSkull("MHF_" + entityType.name(), name, lore);
    }

    public static List<ItemStack> removeNulls(List<ItemStack> itemStacks) {
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack item : itemStacks) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            list.add(item);
        }
        return list;
    }

    public static List<ItemStack> removeMaterial(List<ItemStack> itemStacks, Material material) {
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack item : itemStacks) {
            if (item == null || item.getType() == material) {
                continue;
            }
            list.add(item);
        }
        return removeNulls(list);
    }

    public static List<ItemStack> removeMaterials(List<ItemStack> itemStacks, Material... materials) {
        List<ItemStack> list = itemStacks;
        for (Material mat : materials) {
            list = removeMaterial(list, mat);
        }
        return list;
    }

    public static int roundInventorySize(int count) {
        return (count + (9 - 1)) / 9 * 9;
    }

    public static String oridinate(String string) {
        return KrizzUtil.colorAndStrip(string.toLowerCase().replaceAll(" ", "").replace("_", ""));
    }

    public static String colorAndStrip(String message) {
        return ChatColor.stripColor(color(message));
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes(COLOR_CHAR, message);
    }

    public static List<String> color(List<String> messages) {
        return messages.stream().map(KrizzUtil::color).collect(Collectors.toList());
    }

    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1, 1);
    }

    public static void sendMessage(LivingEntity entity, String message) {
        entity.sendMessage(color(message));
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }

    public static void sendMessage(CommandSender sender, List<String> messages) {
        sender.sendMessage(KrizzUtil.color(messages).toArray(new String[messages.size()]));
    }

    public static void fillInventory(Inventory inventory, ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, item);
            }
        }
    }

    public static void fillInventory(Inventory inventory) {
        fillInventory(inventory, KrizzUtil.createItem(Material.STAINED_GLASS_PANE, "&7", 1, 15, "&7"));
    }

    public static boolean hasArmorOn(Player player) {
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item == null) {
                continue;
            }
            if (item != null && item.getType() != Material.AIR) {
                return true;
            }
        }
        return false;
    }

    public static void log(String message, LogLevel level) {
        Bukkit.getConsoleSender().sendMessage(color(level.getColor() + LOGGER_PREFIX.replace("%level%", level.name()) + " " + message));
    }

    public static String formatBoolean(boolean enabled) {
        return color(enabled ? "&a&lENABLED" : "&c&lDISABLED");
    }

    public static List<ItemStack> getAllContents(Player player) {
        List<ItemStack> contents = Lists.newArrayList(player.getInventory().getArmorContents());
        contents.addAll(Lists.newArrayList(player.getInventory().getContents()));
        return KrizzUtil.removeNulls(contents);
    }

    public static ItemStack findEssItem(String itemName) {
        try {
            Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            return ess.getItemDb().get(itemName);
        } catch (Exception e) {
            System.out.println("could not find item: " + itemName);
            return null;
        }
    }

    public static List<Player> getNearbyPlayers(Player player, int radius) {
        return player.getNearbyEntities(radius, radius, radius).stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity)
                .collect(Collectors.toList());

    }

    public static List<Location> getChunkBorderLocations(Location loc, int height) {
        List<Location> blocks = Lists.newArrayList();
        Chunk c = loc.getChunk();

        int maxX = 15;
        int maxY = loc.getBlockY() + height;
        int maxZ = 15;

        int minX = -16;
        int minY = loc.getBlockY() - height;
        int minZ = -16;

        for (int locX = minX; locX <= maxX; locX++) {
            for (int locZ = minZ; locZ <= maxZ; locZ++) {
                for (int locY = minY; locY <= maxY; locY++) {
                    Block block = c.getBlock(locX, locY, locZ);
                    if (locX == minX || locX == maxX || locZ == minZ || locZ == maxZ) {
                        blocks.add(block.getLocation());
                    }
                }
            }
        }

        return blocks;
    }

    @Getter
    public enum LogLevel {
        INFO(ChatColor.WHITE), ERROR(ChatColor.RED), SUCCESS(ChatColor.GREEN);

        private ChatColor color;

        LogLevel(ChatColor color) {
            this.color = color;
        }
    }


}

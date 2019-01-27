package me.krizzdawg.wildpvp.command;

import me.krizzdawg.wildpvp.WildPvPPlugin;
import me.krizzdawg.wildpvp.queue.WildQueue;
import me.krizzdawg.wildpvp.util.KrizzUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class WildPvPCommand implements CommandExecutor, Listener {

    private WildPvPPlugin plugin;

    public WildPvPCommand(WildPvPPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            KrizzUtil.sendMessage(sender, "&c&l(!)&c You must be a &lPlayer&c.");
            return true;
        }

        if (!sender.hasPermission("wildpvp.use")) {
            KrizzUtil.sendMessage(sender, "&c&l(!)&c You don't have permission to use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            plugin.getQueueManager().openQueueInventory(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            WildQueue queue = plugin.getQueueManager().getQueueByHost(player);

            if (queue == null) {
                KrizzUtil.sendMessage(player, "&c&l(!)&c You are not in the &lWild PvP Queue&c.");
                return true;
            }

            plugin.getQueueManager().removeExisitingQueue(queue);
            KrizzUtil.sendMessage(player, "&c&l(!)&c You have left the &lWild PvP Queue&c.");
            return true;
        }

        return true;
    }


}

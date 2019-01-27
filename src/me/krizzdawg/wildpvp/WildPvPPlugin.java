package me.krizzdawg.wildpvp;

import com.earth2me.essentials.spawn.EssentialsSpawn;
import lombok.Getter;
import me.krizzdawg.wildpvp.command.WildPvPCommand;
import me.krizzdawg.wildpvp.listener.QueueListener;
import me.krizzdawg.wildpvp.queue.QueueManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class WildPvPPlugin extends JavaPlugin {

    // WildPvp System:
    /*

    Create Queue - Display Glass Red Border With Packets
    Invincibility Granted - Both players will be invincible for 7 seconds, then you can do /pvp leave or /spawn
     */

    @Getter
    private static WildPvPPlugin instance;
    private QueueManager queueManager;

    public void onEnable() {
        instance = this;
        queueManager = new QueueManager(this);

        Bukkit.getPluginManager().registerEvents(new QueueListener(this), this);
        getCommand("wildpvp").setExecutor(new WildPvPCommand(this));
    }

    public Location getSpawnLocation() {
        Plugin spawnPlugin = getServer().getPluginManager().getPlugin("EssentialsSpawn");
        if (spawnPlugin != null) {
            EssentialsSpawn essentialsSpawn = (EssentialsSpawn) spawnPlugin;
            return essentialsSpawn.getSpawn("default");
        } else {
            if (Bukkit.getWorld("world") != null) {
                return Bukkit.getWorld("world").getSpawnLocation();
            }
        }
        return null;
    }
}

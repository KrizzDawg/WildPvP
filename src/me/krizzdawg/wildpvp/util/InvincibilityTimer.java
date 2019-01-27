package me.krizzdawg.wildpvp.util;

import me.krizzdawg.wildpvp.WildPvPPlugin;
import me.krizzdawg.wildpvp.queue.WildQueue;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class InvincibilityTimer extends BukkitRunnable {

    private WildQueue queue;
    private int duration;
    private int timeRemaining;

    public InvincibilityTimer(WildQueue queue, int duration) {
        this.queue = queue;
        this.duration = duration;
        this.timeRemaining = duration;
        runTaskTimer(WildPvPPlugin.getInstance(), 20, 20L);
    }

    @Override
    public void run() {
        if (timeRemaining == 0) {
            queue.getBoth().forEach(player -> {
                KrizzUtil.sendMessage(player, "&aInvincibility - &lEXPIRED");
                KrizzUtil.playSound(player, Sound.NOTE_BASS);
            });

            cancel();


            queue.endSuccesfully();

            remove();
            return;
        }
        queue.getBoth().forEach(player -> {
            KrizzUtil.sendMessage(player, "&aInvincibility - &lExpiring in " + timeRemaining + "s... &7(&C/spawn&7 to exit)");
            KrizzUtil.playSound(player, Sound.ORB_PICKUP);
        });
        timeRemaining--;
    }

    public void remove() {
        queue = null;
        cancel();
    }

}

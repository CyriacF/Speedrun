package com.github.speedrunshowdown.listeners;

import com.github.speedrunshowdown.SpeedrunShowdown;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.PortalType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PortalEnterListener implements Listener {
    @EventHandler
    public void onPortalEnter(PlayerPortalEvent event) {
        SpeedrunShowdown plugin = SpeedrunShowdown.getInstance();
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL && !plugin.isSuddenDeath()){
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            String command = "money give " + event.getPlayer().getName() + " 125";
            Bukkit.dispatchCommand(console, command);
        }
        System.out.println(event.getCause());

        // If plugin is running, give player portal invincibility
        if (plugin.isRunning()) {
            plugin.getLogger().info(event.getPlayer().getName());
            event.getPlayer().addPotionEffect(new PotionEffect(
                PotionEffectType.DAMAGE_RESISTANCE,
                plugin.getConfig().getInt("portal-invincibility") * 20,
                255
            ));
        }
    }
}
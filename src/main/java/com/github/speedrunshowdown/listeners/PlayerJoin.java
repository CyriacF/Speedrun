package com.github.speedrunshowdown.listeners;

import com.github.speedrunshowdown.SpeedrunShowdown;
import com.github.speedrunshowdown.commands.StartCommand;
import com.github.speedrunshowdown.gui.ScoreboardManager;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerJoin implements Listener {
    SpeedrunShowdown plugin = SpeedrunShowdown.getInstance();
    private StartCommand start;
    private ScoreboardManager scoreboardManager;

    public int count = 0;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        count++;
        if (!plugin.isRunning()){
            event.setJoinMessage("§7[§a+§7] " + ChatColor.GRAY + event.getPlayer().getName());
            player.setGameMode(GameMode.ADVENTURE);
            player.sendMessage("§aIl manque §c" + (4-count) +  " §ajoueur(s) pour démarrer la partie");
            if (count >= 4){
                if (!plugin.isStarting())
                autoStart();
            }
        }else{
            event.setJoinMessage(null);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        count--;
        if (!plugin.isRunning()){
            event.setQuitMessage("§7[§c-§7] " + ChatColor.GRAY + event.getPlayer().getName());
        }else{
        event.setQuitMessage(null);
        }
        if (count < 5){
            plugin.setIsStarting(false);
            for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                if (!plugin.isRunning()){
                players.sendTitle("Démmarage annulé", "", 1, 60, 1);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 100, 1);
                }
            }
        }
        if (count == 0){
            if (plugin.isRunning() || plugin.isSuddenDeath()){
                Bukkit.shutdown();
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        Entity entity = event.getEntity();
        if (!plugin.isRunning()){
            if (entity instanceof Player){
                event.setDamage(0.0);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void chatFormat(AsyncPlayerChatEvent event){
        Player p = event.getPlayer();
        event.setFormat(p.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.WHITE + event.getMessage());
    }


    public void autoStart(){
        int task = 0;
        int countdownTime = 50;
        plugin.setIsStarting(true);
        for (int i = 0; i <= countdownTime; i++) {
            final int seconds = i;
            int finalTask = task;
            task = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (seconds == 0) {
                    plugin.start();
                }
                if (count < 2){
                    Bukkit.getScheduler().cancelTask(finalTask);
                }
                else {
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        if (seconds ==  45 || seconds ==  30 || seconds ==  15 || seconds == 10 || seconds == 5 || seconds == 4 || seconds == 3 || seconds == 2 || seconds == 1){
                            player.sendTitle("§6Démmarage dans :", "" + seconds, 1, 20, 1);
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
                        }
                    }
                }
            }, 20L + (countdownTime - i) * 20L);
        }
    }

}
package com.github.speedrunshowdown.listeners;

import java.util.ArrayList;

import com.github.speedrunshowdown.SpeedrunShowdown;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Team;

public class PlayerDeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        SpeedrunShowdown plugin = SpeedrunShowdown.getInstance();

        // If plugin running and is sudden death
        if (plugin.isRunning() && plugin.isSuddenDeath()) {
            // Make player a spectator
            event.getEntity().setGameMode(GameMode.SPECTATOR);

            // Mark player death as a final kill
            String deathMessage = event.getDeathMessage();

            String[] words = deathMessage.split(" ");
            StringBuilder stringBuilder = new StringBuilder();
            for (String word : words) {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (word.equals(player.getName())) {
                        Team team = plugin.getSpeedrunShowdownScoreboard().getTeam(player);
                        if (team != null) {
                            word = team.getColor() + word + ChatColor.RESET;
                        }
                    }
                }

                stringBuilder.append(word + " ");
            }

            deathMessage = stringBuilder.toString();

            event.setDeathMessage("");
            plugin.getServer().broadcastMessage(deathMessage + ChatColor.AQUA + ChatColor.BOLD + "FINAL KILL!");


            // Play death sound to all players
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
            }

            // Check which teams are living
            ArrayList<Team> livingTeams = new ArrayList<>();
            for (Team team : plugin.getSpeedrunShowdownScoreboard().getScoreboard().getTeams()) {
                // If team has no entries, skip
                if (team.getSize() != 0) {
                    boolean teamAlive = false;
                    // If team contains a player in survival, mark team as alive
                    for (String entry : team.getEntries()) {
                        Player player = plugin.getServer().getPlayer(entry);
                        if (player != null && player.getGameMode() == GameMode.SURVIVAL) {
                            teamAlive = true;
                        }
                    }
                    
                    // If team not alive and contains dying player, announce team elimination
                    if (
                        !teamAlive &&
                        team.getName().equals(
                            plugin.getSpeedrunShowdownScoreboard().getTeam(event.getEntity()).getName()
                        )
                    ) {
                        // Broadcast team elimination
                        plugin.getServer().broadcastMessage("");
                        plugin.getServer().broadcastMessage(
                            "" + ChatColor.WHITE + ChatColor.BOLD + "TEAM ÉLIMINÉ > " +
                            team.getColor() + "La Team " + WordUtils.capitalize(team.getName()) +
                            ChatColor.RED + " vient d'être éliminé !"
                        );
                        plugin.getServer().broadcastMessage("");
                    }
                    // Else if team living, add team to living teams
                    else if (teamAlive) {
                        livingTeams.add(team);
                    }
                }
            }

            // If players need to kill the dragon to win and there's no teams left, respawn players
            if (
                plugin.getConfig().getBoolean("must-kill-dragon-to-win") &&
                livingTeams.size() == 0
            ){
                plugin.suddenDeath();
            }
            // Else if players don't need to kill the dragon and there's one team left, make team win
            else if (
                !plugin.getConfig().getBoolean("must-kill-dragon-to-win") &&
                livingTeams.size() == 1
            ) {
                plugin.win(
                    livingTeams.get(0),
                    deathMessage
                );
            }
        }
    }
}
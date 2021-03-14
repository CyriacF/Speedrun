package com.github.speedrunshowdown.listeners;

import com.github.speedrunshowdown.SpeedrunShowdown;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPing implements Listener {

    @EventHandler
    public void SetMOTD(ServerListPingEvent event){
        SpeedrunShowdown plugin = SpeedrunShowdown.getInstance();
        if (plugin.isRunning() || plugin.isSuddenDeath()){
            event.setMotd("INGAME");
        }else{
            event.setMotd("ONLINE");
        }
    }
}

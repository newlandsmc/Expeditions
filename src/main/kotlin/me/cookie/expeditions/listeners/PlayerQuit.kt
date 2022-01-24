package me.cookie.expeditions.listeners

import me.cookie.expeditions.lastLogoff
import me.cookie.expeditions.rewardItems
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerQuit(private val plugin: JavaPlugin): Listener {
    @EventHandler fun onPlayerDisconnect(event: PlayerQuitEvent){
        event.player.cleanup()
    }
    @EventHandler fun onPlayerKicked(event: PlayerKickEvent){
        event.player.cleanup()
    }

    private fun Player.cleanup(){
        this.lastLogoff = System.currentTimeMillis()
        if(!plugin.config.getBoolean("add-old-rewards")){
            this.rewardItems = listOf()
        }
    }
}
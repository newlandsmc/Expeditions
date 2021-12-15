package me.cookie.rejoinrewards.listeners

import me.cookie.rejoinrewards.lastLogoff
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuit: Listener {
    @EventHandler
    fun onPlayerDisconnect(event: PlayerQuitEvent){
        event.player.lastLogoff = System.currentTimeMillis()
    }
    @EventHandler
    fun onPlayerKicked(event: PlayerKickEvent){
        event.player.lastLogoff = System.currentTimeMillis()
    }
}
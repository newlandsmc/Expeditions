package me.cookie.rejoinrewards.listeners

import me.cookie.rejoinrewards.RejoinRewards
import me.cookie.rejoinrewards.lastLogoff
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerQuit: Listener {
    private val plugin = JavaPlugin.getPlugin(RejoinRewards::class.java)
    @EventHandler
    fun onPlayerDisconnect(event: PlayerQuitEvent){
        event.player.lastLogoff = System.currentTimeMillis()
    }
}
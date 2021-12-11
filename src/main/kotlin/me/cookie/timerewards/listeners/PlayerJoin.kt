package me.cookie.timerewards.listeners

import me.cookie.timerewards.TimeRewards
import me.cookie.timerewards.spawnRejoinReward
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class PlayerJoin: Listener {
    @EventHandler
    fun onPlayerSpawn(event: PlayerJoinEvent){
        val player = event.player
        event.joinMessage(
            Component.text("${player.name} has returned from their expedition!", NamedTextColor.YELLOW)
        )
        object : BukkitRunnable() {
            override fun run() {
                player.spawnRejoinReward()
            }
        }.runTaskLater(JavaPlugin.getPlugin(TimeRewards::class.java), 100 /* 5 seconds for debugging*/)
    }
}
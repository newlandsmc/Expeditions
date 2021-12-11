package me.cookie.timerewards.listeners

import me.cookie.timerewards.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerPreLoginEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class PlayerJoin: Listener {
    private val plugin = JavaPlugin.getPlugin(TimeRewards::class.java)
    @EventHandler
    fun onPlayerSpawn(event: PlayerJoinEvent){
        val player = event.player
        println("${player.lastLogoff} ${System.currentTimeMillis()}")
        event.joinMessage(
            Component.text(
                /* TODO: needs to be configurable */
                "${player.name} has returned from their expedition!",
                NamedTextColor.YELLOW
            )
        )

        plugin.logger.info(
            "${player.name} was gone for ${(System.currentTimeMillis() - player.lastLogoff!!) / 60000} minute(s)."
        )


        player.generateOfflineRewards()
        /*
        object : BukkitRunnable() {
            override fun run() {
                player.spawnRejoinReward()
            }
        }.runTaskLater(JavaPlugin.getPlugin(TimeRewards::class.java), 20 /* 5 seconds for debugging*/)

        We're not going to use this for now

        */

    }
}
package me.cookie.timerewards.listeners

import me.cookie.timerewards.RejoinRewards
import me.cookie.timerewards.generateOfflineRewards
import me.cookie.timerewards.lastLogoff
import me.cookie.timerewards.playerRewardMap
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerJoin: Listener {
    private val plugin = JavaPlugin.getPlugin(RejoinRewards::class.java)
    @EventHandler
    fun onPlayerSpawn(event: PlayerJoinEvent){
        val player = event.player

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


        playerRewardMap[player.uniqueId] = player.generateOfflineRewards()
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
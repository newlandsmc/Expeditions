package me.cookie.rejoinrewards.listeners

import me.cookie.rejoinrewards.*
import net.kyori.adventure.text.minimessage.MiniMessage
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
            MiniMessage.get().parse(
                plugin.config.getString("welcome-back")!!
                    .formatPlayerPlaceholders(event.player)
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
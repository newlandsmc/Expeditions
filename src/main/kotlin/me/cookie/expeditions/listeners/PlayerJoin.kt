package me.cookie.expeditions.listeners

import me.cookie.cookiecore.formatMinimessage
import me.cookie.cookiecore.formatPlayerPlaceholders
import me.cookie.cookiecore.message.messagequeueing.queueMessage
import me.cookie.expeditions.generateOfflineRewards
import me.cookie.expeditions.initIntoDB
import me.cookie.expeditions.lastLogoff
import me.cookie.expeditions.updateRewardItems
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerJoin(private val plugin: JavaPlugin): Listener {
    @EventHandler fun onPlayerJoin(event: PlayerJoinEvent){
        val player = event.player

        player.initIntoDB()

        val generatedItems = player.generateOfflineRewards(
            (System.currentTimeMillis() - player.lastLogoff) / 60000,
            plugin.config.getBoolean("add-old-items")
        )
        if(generatedItems.isNotEmpty()){
            player.queueMessage(
                plugin.config.getString("claim-reminder")!!
                    .formatPlayerPlaceholders(player)
                    .formatMinimessage(),
                5
            )
            player.updateRewardItems(generatedItems)
        }

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
package me.cookie.rejoinrewards.listeners

import me.cookie.rejoinrewards.RejoinRewards
import me.cookie.rejoinrewards.generateOfflineRewards
import me.cookie.rejoinrewards.initIntoDB
import me.cookie.rejoinrewards.updateRewardItems
import me.cookie.semicore.message.messagequeueing.queueMessage
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerJoin: Listener {
    private val plugin = JavaPlugin.getPlugin(RejoinRewards::class.java)
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent){
        val player = event.player

        //TODO to move out of here into join plugin
        /*event.joinMessage(
            MiniMessage.get().parse(
                plugin.config.getString("welcome-back")!!
                    .formatPlayerPlaceholders(event.player)
            )
        )*/

        player.initIntoDB()

        val generatedItems = player.generateOfflineRewards()
        if(generatedItems.isNotEmpty()){
            player.queueMessage(
                MiniMessage.get().parse(
                    plugin.config.getString("claim-reminder")!!
                ),
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
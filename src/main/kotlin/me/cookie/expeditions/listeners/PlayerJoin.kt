package me.cookie.expeditions.listeners

import me.cookie.cookiecore.formatMinimessage
import me.cookie.cookiecore.formatPlayerPlaceholders
import me.cookie.cookiecore.message.messagequeueing.queueMessage
import me.cookie.expeditions.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerJoin: Listener {
    private val plugin = JavaPlugin.getPlugin(Expeditions::class.java)
    @EventHandler fun onPlayerJoin(event: PlayerJoinEvent){
        val player = event.player

        player.initIntoDB()

        val generatedItems = player.generateOfflineRewards()
        if(generatedItems.isNotEmpty()){
            player.queueMessage(
                plugin.config.getString("claim-reminder")!!
                    .formatPlayerPlaceholders(player)
                    .formatMinimessage(),
                5
            )
            player.updateRewardItems(generatedItems)
        }

        val inventory = Bukkit.createInventory(null, 27, Component.text("CHEEZ"))
        inventory.setContents(*player.giveVoteRewards().toTypedArray())

        player.openInventory(inventory)

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
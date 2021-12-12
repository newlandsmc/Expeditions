package me.cookie.rejoinrewards

import me.cookie.rejoinrewards.commands.ClaimReward
import me.cookie.rejoinrewards.data.RewardConfig
import me.cookie.rejoinrewards.listeners.MenuHandler
import me.cookie.rejoinrewards.listeners.PlayerJoin
import me.cookie.rejoinrewards.listeners.PlayerQuit
import me.cookie.rejoinrewards.messagequeueing.MessageQueueing
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


class RejoinRewards: JavaPlugin() {
    lateinit var rewardsConfig: RewardConfig
    override fun onEnable() {
        registerCommands()
        registerEvents()
        rewardsConfig = RewardConfig()
        MessageQueueing().startRunnable()
        saveDefaultConfig()
    }

    private fun registerCommands(){
        getCommand("jrewards")!!.setExecutor(ClaimReward())
    }
    private fun registerEvents(){
        server.pluginManager.registerEvents(PlayerJoin(), this)
        server.pluginManager.registerEvents(PlayerQuit(), this)
        server.pluginManager.registerEvents(MenuHandler(), this)
    }

}

private val playerMenuUtilityMap = HashMap<Player, PlayerMenuUtility>()

val Player.playerMenuUtility: PlayerMenuUtility
get() {
    val playerMenuUtility: PlayerMenuUtility
    if (!(playerMenuUtilityMap.containsKey(this))) {
        playerMenuUtility = PlayerMenuUtility(this)
        playerMenuUtilityMap[this] = playerMenuUtility
        return playerMenuUtility
    }
    return playerMenuUtilityMap[this]!!
}
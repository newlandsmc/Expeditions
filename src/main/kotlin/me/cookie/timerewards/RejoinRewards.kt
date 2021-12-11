package me.cookie.timerewards

import me.cookie.timerewards.commands.ClaimReward
import me.cookie.timerewards.listeners.MenuHandler
import me.cookie.timerewards.listeners.PlayerJoin
import me.cookie.timerewards.listeners.PlayerQuit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


class RejoinRewards: JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(PlayerJoin(), this)
        server.pluginManager.registerEvents(PlayerQuit(), this)
        server.pluginManager.registerEvents(MenuHandler(), this)
        getCommand("jrewards")!!.setExecutor(ClaimReward())
        //TODO("Enable logic")
    }

    override fun onDisable() {
        //TODO("Disable logic")
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
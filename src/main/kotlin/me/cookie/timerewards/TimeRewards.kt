package me.cookie.timerewards

import me.cookie.timerewards.listeners.PlayerJoin
import org.bukkit.plugin.java.JavaPlugin

class TimeRewards: JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(PlayerJoin(), this)
        //TODO("Enable logic")
    }

    override fun onDisable() {
        //TODO("Disable logic")
    }
}
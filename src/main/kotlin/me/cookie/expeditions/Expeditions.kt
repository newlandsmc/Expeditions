package me.cookie.expeditions

import me.cookie.expeditions.commands.ClaimReward
import me.cookie.expeditions.commands.TestVote
import me.cookie.expeditions.data.RewardConfig
import me.cookie.expeditions.data.sql.database.H2Storage
import me.cookie.expeditions.listeners.PlayerJoin
import me.cookie.expeditions.listeners.PlayerQuit
import me.cookie.expeditions.listeners.PlayerVote
import org.bukkit.plugin.java.JavaPlugin


class Expeditions: JavaPlugin() {
    lateinit var rewardsConfig: RewardConfig
    lateinit var database: H2Storage
    override fun onEnable() {

        registerCommands()
        registerEvents()

        rewardsConfig = RewardConfig()

        saveDefaultConfig()

        database = H2Storage()

        database.connect()

        database.initTable(
            "playerData",
            listOf("UUID varchar(255)", "LOGOFF long", "ITEMS varchar"),
        )
    }

    override fun onDisable() {
        database.disconnect()
    }

    private fun registerCommands(){
        getCommand("spoils")!!.setExecutor(ClaimReward())
        getCommand("etv")!!.setExecutor(TestVote())
    }
    private fun registerEvents(){
        server.pluginManager.registerEvents(PlayerJoin(), this)
        server.pluginManager.registerEvents(PlayerQuit(), this)
        server.pluginManager.registerEvents(PlayerVote(), this)
    }
}


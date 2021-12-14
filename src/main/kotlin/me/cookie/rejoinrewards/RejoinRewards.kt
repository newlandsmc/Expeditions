package me.cookie.rejoinrewards

import me.cookie.rejoinrewards.commands.ClaimReward
import me.cookie.rejoinrewards.data.RewardConfig
import me.cookie.rejoinrewards.data.sql.database.H2Storage
import me.cookie.rejoinrewards.listeners.MenuHandler
import me.cookie.rejoinrewards.listeners.PlayerJoin
import me.cookie.rejoinrewards.listeners.PlayerQuit
import org.bukkit.plugin.java.JavaPlugin


class RejoinRewards: JavaPlugin() {
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
        getCommand("jrewards")!!.setExecutor(ClaimReward())
    }
    private fun registerEvents(){
        server.pluginManager.registerEvents(PlayerJoin(), this)
        server.pluginManager.registerEvents(PlayerQuit(), this)
        server.pluginManager.registerEvents(MenuHandler(), this)
    }
}


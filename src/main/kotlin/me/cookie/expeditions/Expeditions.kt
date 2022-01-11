package me.cookie.expeditions


import me.cookie.cookiecore.CookieCore
import me.cookie.cookiecore.data.sql.H2Storage
import me.cookie.expeditions.commands.ClaimReward
import me.cookie.expeditions.commands.RewardAdmin
import me.cookie.expeditions.commands.TestRewards
import me.cookie.expeditions.commands.TestVote
import me.cookie.expeditions.data.RewardConfig
import me.cookie.expeditions.listeners.PlayerInteract
import me.cookie.expeditions.listeners.PlayerJoin
import me.cookie.expeditions.listeners.PlayerQuit
import me.cookie.expeditions.listeners.PlayerVote
import org.bukkit.plugin.java.JavaPlugin


class Expeditions: JavaPlugin() {
    lateinit var rewardsConfig: RewardConfig
    lateinit var database: H2Storage
    private lateinit var cookieCore: JavaPlugin
    override fun onEnable() {
        cookieCore = getPlugin(CookieCore::class.java)

        registerCommands()
        registerEvents()

        rewardsConfig = RewardConfig()

        saveDefaultConfig()

        database = H2Storage(this as JavaPlugin, "playerData")

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
        getCommand("spoils")!!.setExecutor(ClaimReward(this))
        getCommand("etv")!!.setExecutor(TestVote())
        getCommand("testrewards")!!.setExecutor(TestRewards())
        getCommand("expeditions")!!.setExecutor(RewardAdmin())
    }
    private fun registerEvents(){
        server.pluginManager.registerEvents(PlayerJoin(this), this)
        server.pluginManager.registerEvents(PlayerInteract(this), this)
        server.pluginManager.registerEvents(PlayerQuit(), this)
        server.pluginManager.registerEvents(PlayerVote(), this)
    }
}


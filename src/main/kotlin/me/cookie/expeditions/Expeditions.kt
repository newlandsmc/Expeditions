package me.cookie.expeditions


import me.cookie.cookiecore.CookieCore
import me.cookie.cookiecore.data.sql.H2Storage
import me.cookie.expeditions.commands.ClaimReward
import me.cookie.expeditions.commands.RewardAdmin
import me.cookie.expeditions.commands.TestVote
import me.cookie.expeditions.data.RewardConfig
import me.cookie.expeditions.listeners.PlayerJoin
import me.cookie.expeditions.listeners.PlayerQuit
import me.cookie.expeditions.listeners.PlayerVoting
import org.bukkit.plugin.java.JavaPlugin


class Expeditions: JavaPlugin() {
    lateinit var rewardsConfig: RewardConfig
    lateinit var playerItems: H2Storage
    lateinit var playerVotes: H2Storage
    private lateinit var cookieCore: JavaPlugin
    override fun onEnable() {
        cookieCore = getPlugin(CookieCore::class.java)

        rewardsConfig = RewardConfig()

        saveDefaultConfig()

        playerItems = H2Storage(this as JavaPlugin, "playerItems")
        playerVotes = H2Storage(this as JavaPlugin, "playerVotes")

        playerItems.connect()
        playerVotes.connect()

        playerItems.initTable(
            "playerData",
            listOf("UUID varchar(255)", "LOGOFF long", "NORMALITEMS varchar", "INSTANTITEMS varchar")
        )
        playerVotes.initTable(
            "playerVotes",
            listOf("PLAYER varchar(255)", "SERVICE varchar(255)", "TIMESTAMP timestamp")
        )

        registerCommands()
        registerEvents()
    }

    override fun onDisable() {
        playerItems.disconnect()
    }

    private fun registerCommands(){
        getCommand("spoils")!!.setExecutor(ClaimReward(this))
        getCommand("etv")!!.setExecutor(TestVote(playerVotes))
        getCommand("expeditions")!!.setExecutor(RewardAdmin())
    }
    private fun registerEvents(){
        server.pluginManager.registerEvents(PlayerJoin(this), this)
        // server.pluginManager.registerEvents(PlayerInteract(this), this)  Saved for a rainy day
        server.pluginManager.registerEvents(PlayerQuit(this), this)
        server.pluginManager.registerEvents(PlayerVoting(this, playerVotes), this)
    }
}


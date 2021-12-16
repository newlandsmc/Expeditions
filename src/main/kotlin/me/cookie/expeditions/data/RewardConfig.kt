package me.cookie.expeditions.data

import me.cookie.expeditions.Expeditions
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException


class RewardConfig {
    private val plugin = JavaPlugin.getPlugin(Expeditions::class.java)
    private var customConfigFile: File? = null
    private var customConfig: FileConfiguration? = null

    init {
        createCustomConfig()
    }

    fun getCustomConfig(): FileConfiguration? {
        return customConfig
    }

    private fun createCustomConfig() {
        customConfigFile = File(plugin.dataFolder, "rewards.yml")
        if (!customConfigFile!!.exists()) {
            customConfigFile!!.parentFile.mkdirs()
            plugin.saveResource("rewards.yml", false)
        }
        customConfig = YamlConfiguration()
        try {
            (customConfig as YamlConfiguration).load(customConfigFile!!)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }
}
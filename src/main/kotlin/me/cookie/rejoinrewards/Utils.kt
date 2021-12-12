package me.cookie.rejoinrewards

import me.cookie.rejoinrewards.data.sql.database.Values
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.NumberConversions.toLong
import java.util.*

private val plugin = JavaPlugin.getPlugin(RejoinRewards::class.java)

// Component to plain string
fun Component.toString(): String = PlainTextComponentSerializer.plainText().serialize(this)



fun Player.initIntoDB(){
    val playerUUID = plugin.database.getRowsWhere(
        "playerTimes",
        "UUID",
        "UUID = '${this.uniqueId.cleanUp()}'",
    )
    // Doesnt exist in db
    if(playerUUID.isEmpty()){
        plugin.database.insertIntoTable(
            "playerTimes",
            listOf("UUID", "LOGOFF"),
            Values(this.uniqueId.cleanUp(), System.currentTimeMillis())
        )
    }
}

fun UUID.cleanUp(): String{
    return this.toString().replace("-", "")
}

// Last logoff stuff
var Player.lastLogoff: Long
    get() = toLong(plugin.database.getRowsWhere(
        "playerTimes",
        "LOGOFF",
        "UUID = '${this.uniqueId.cleanUp()}'",
        1,
    )[0].values[0])
    set(value) {
        plugin.database.updateColumnsWhere(
            "playerTimes",
            listOf("LOGOFF"),
            "UUID = '${this.uniqueId.cleanUp()}'",
            Values(value),
        )
    }

// Placeholder formatting
fun String.formatPlayerPlaceholders(player: Player): String {
    var formatted = this
    if(this.contains("(playerName)"))
        formatted = formatted.replace("(playerName)", player.name)
    if(this.contains("(playerHealth)"))
        formatted = formatted.replace("(playerHealth)", "${player.health}")
    return formatted
}
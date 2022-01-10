package me.cookie.expeditions

import me.cookie.cookiecore.data.Values
import me.cookie.cookiecore.cleanUp
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.NumberConversions.toLong
import kotlin.random.Random


private val plugin = JavaPlugin.getPlugin(Expeditions::class.java)
fun Int.randomTo(cap: Int): Int {
    return Random.nextInt((cap-this))+this

}
// Create player in database
fun Player.initIntoDB(){
    val playerUUID = plugin.database.getRowsWhere(
        "playerData",
        "UUID",
        "UUID = '${this.uniqueId.cleanUp()}'",
    )
    // Doesnt exist in db
    if(playerUUID.isEmpty()){
        plugin.database.insertIntoTable(
            "playerData",
            listOf("UUID", "LOGOFF", "ITEMS"),
            Values(this.uniqueId.cleanUp(), System.currentTimeMillis(), "")
        )
    }
}

// Last logoff stuff
var Player.lastLogoff: Long
    get() = toLong(plugin.database.getRowsWhere(
        "playerData",
        "LOGOFF",
        "UUID = '${this.uniqueId.cleanUp()}'",
        1,
    )[0].values[0])
    set(value) {
        plugin.database.updateColumnsWhere(
            "playerData",
            listOf("LOGOFF"),
            "UUID = '${this.uniqueId.cleanUp()}'",
            Values(value),
        )
    }

fun String.isNumber(): Boolean {
    return if (this.isEmpty()) false else this.all { Character.isDigit(it) }
}
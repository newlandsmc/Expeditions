package me.cookie.rejoinrewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import java.util.*

class Utils

fun Component.toString(): String = PlainTextComponentSerializer.plainText().serialize(this)
val playerLogoffMap = HashMap<UUID, Long>() /* TODO make storage persistent (database/file storage) */
var Player.lastLogoff
    get() = run {
        playerLogoffMap.putIfAbsent(this.uniqueId, System.currentTimeMillis())
        return@run playerLogoffMap[this.uniqueId]
    }
    set(value) {
        playerLogoffMap.putIfAbsent(this.uniqueId, System.currentTimeMillis())
        playerLogoffMap[this.uniqueId] = value!!
    }

fun String.formatPlayerPlaceholders(player: Player): String {
    var formatted = this
    if(this.contains("(playerName)"))
        formatted = formatted.replace("(playerName)", player.name)
    if(this.contains("(playerHealth)"))
        formatted = formatted.replace("(playerHealth)", "${player.health}")
    return formatted
}
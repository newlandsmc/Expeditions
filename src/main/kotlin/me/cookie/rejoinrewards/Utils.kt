package me.cookie.rejoinrewards

import me.cookie.rejoinrewards.data.sql.database.Values
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.NumberConversions.toLong
import java.util.*


private val plugin = JavaPlugin.getPlugin(RejoinRewards::class.java)

// Component to plain string
fun Component.toPlainString(): String = PlainTextComponentSerializer.plainText().serialize(this)

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

fun UUID.cleanUp(): String{
    return this.toString().replace("-", "")
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

// Placeholder formatting
fun String.formatPlayerPlaceholders(player: Player): String {
    var formatted = this
    if(this.contains("(playerName)"))
        formatted = formatted.replace("(playerName)", player.name)
    if(this.contains("(playerHealth)"))
        formatted = formatted.replace("(playerHealth)", "${player.health}")
    return formatted
}


fun updateRewardItems(items: List<ItemStack>, uuid: UUID){
    var serializedItems = ""
    if(items.isNotEmpty()){
        items.forEach {
            serializedItems += "${Base64.getEncoder().encodeToString(it.serializeAsBytes())},"
        }
        if(serializedItems.endsWith(",")){
            serializedItems.dropLast(1)
        }
        plugin.database.updateColumnsWhere(
            "playerData",
            listOf("ITEMS"),
            "UUID = '${uuid.cleanUp()}'",
            Values(serializedItems)
        )
        return
    }
    plugin.database.updateColumnsWhere(
        "playerData",
        listOf("ITEMS"),
        "UUID = '${uuid.cleanUp()}'",
        Values("")
    )
}

// Updates the items in their virtual reward chest
fun Player.updateRewardItems(items: List<ItemStack>){
    updateRewardItems(items, this.uniqueId)
}

fun getRewardItems(uuid: UUID): List<ItemStack>{
    val items = mutableListOf<ItemStack>()
    val row = plugin.database.getRowsWhere(
        "playerData",
        "ITEMS",
        "UUID = '${uuid.cleanUp()}'"
    )

    // Add old rewards
    if(row.isNotEmpty()){
        if(row[0].values.isNotEmpty()){
            if((row[0].values[0] as String).isEmpty()) return emptyList()
            val encodedArray = (row[0].values[0] as String).split(",")
            if(encodedArray.isEmpty()) return emptyList()
            encodedArray.dropLast(1).forEach {
                if(items.size >= 27) return items // hard limit, inventory is full
                items.add(ItemStack.deserializeBytes(Base64.getDecoder().decode(it)))
            }
        }
    }
    return items
}

var Player.rewardItems: List<ItemStack>
    get() = run {
        getRewardItems(this.uniqueId)
    }
    set(value) {
        this.updateRewardItems(value)
    }
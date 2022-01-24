package me.cookie.expeditions

import me.cookie.cookiecore.data.Values

import me.cookie.cookiecore.cleanUp
import me.cookie.cookiecore.compressSimilarItems
import me.cookie.cookiecore.formatMinimessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

private val plugin = JavaPlugin.getPlugin(Expeditions::class.java)

val rewardConfig = plugin.rewardsConfig.getCustomConfig()
val rewards = rewardConfig!!.getConfigurationSection("Rewards")!!.getKeys(false).toList()

fun Player.generateOfflineRewards(offlineMinutes: Long): List<ItemStack> {
    var items = mutableListOf<ItemStack>()

    val rewardKeys = rewardConfig!!.getConfigurationSection("Tiers")!!.getKeys(false)

    // Not eligible for new rewards.
    if (
        offlineMinutes
        <
        rewardKeys.toList()[0].toInt()
    ) {
        return items
    }

    var weight = 0
    var previousSection = "0"
    // Gets the weight for the offline tier
    for((i, section) in rewardKeys.withIndex()){
        // Only for last tier
        if(
            offlineMinutes
            >
            previousSection.toInt()
            &&
            offlineMinutes
            >=
            section.toInt()
            &&
            i == rewardKeys.size-1
        ){
            weight = rewardConfig.getInt(
                "Tiers.$section.weight"
            )
            break
        }
        // Check if minutes is more than previous but less than current, if so, correct tier was found.
        if(
            offlineMinutes
            >=
            previousSection.toInt()
            &&
            offlineMinutes
            <
            section.toInt()
        ){
            weight = rewardConfig.getInt(
                "Tiers.$previousSection.weight"
            )
            break
        }
        previousSection = section
    }

    val newItems = calcRewards(weight)
    newItems.forEach{
        if(items.size >= 27) return items
        items.add(it)
        items = items.compressSimilarItems()
    }
    return items
}

fun calcRewards(weight: Int): List<ItemStack>{
    rewardConfig!!
    var items = mutableListOf<ItemStack>()
    var tmpWeight = weight

    // I know while loops the main thread, but let's hope for the best :)
    // Keeps looping until out of weight
    while(tmpWeight > 0){
        val reward = rewards[Random().nextInt(rewards.size)]
        val rewardWeight = rewardConfig.getInt("Rewards.$reward.weight")
        val rewardChanceAgain = rewardConfig.getInt("Rewards.$reward.chance-again")
        val rewardWeightAgain = rewardConfig.getInt("Rewards.$reward.weight-again")

        if(rewardWeight > tmpWeight){ // Player doesn't have enough weight for this reward
            continue
        }else{
            tmpWeight -= rewardWeight
            items.add(
                ItemStack(Material.valueOf(reward))
            )
            items = items.compressSimilarItems()

            if(items.size >= 27) return items // hard limit, inventory is full

            if(rewardChanceAgain < 0.randomTo(100) &&
                rewardWeightAgain < tmpWeight){

                tmpWeight -= rewardWeightAgain

                items.add(
                    ItemStack(Material.valueOf(reward))
                )
                items = items.compressSimilarItems()

                if(items.size >= 27) return items // hard limit, inventory is full
            }
        }

    }
    items = items.compressSimilarItems()
    return items
}

fun Player.generateVoteRewards(): List<ItemStack> {
    return calcRewards(rewardConfig!!.getInt("VoteTier.weight"))
}

fun Player.spawnOfflineReward() {
    if(!this.hasPlayedBefore()) return
    this.sendMessage(
        Component.text(
            /* TODO: needs to be configurable */
            "You found treasure while you were away! Look for a chest nearby.",
            NamedTextColor.GREEN
        )
    )
    plugin.logger.info(
        "${this.name} was gone for ${(System.currentTimeMillis() - this.lastLogoff) / 60000} minute(s)."
    )
}

private fun updateRewardItems(items: List<ItemStack>, uuid: UUID){
    if(items.isNotEmpty()){
        plugin.playerItems.updateColumnsWhere(
            "playerData",
            listOf("NORMALITEMS"),
            "UUID = '${uuid.cleanUp()}'",
            Values(items.serialize())
        )
        return
    }
    plugin.playerItems.updateColumnsWhere(
        "playerData",
        listOf("NORMALITEMS"),
        "UUID = '${uuid.cleanUp()}'",
        Values("")
    )
}

private fun updateInstantRewardItems(items: List<ItemStack>, uuid: UUID){
    if(items.isNotEmpty()){
        plugin.playerItems.updateColumnsWhere(
            "playerData",
            listOf("INSTANTITEMS"),
            "UUID = '${uuid.cleanUp()}'",
            Values(items.serialize())
        )
        return
    }
    plugin.playerItems.updateColumnsWhere(
        "playerData",
        listOf("INSTANTITEMS"),
        "UUID = '${uuid.cleanUp()}'",
        Values("")
    )
}

fun List<ItemStack>.serialize(): String {
    var serializedItems = ""
    this.forEach {
        serializedItems += "${Base64.getEncoder().encodeToString(it.serializeAsBytes())},"
    }
    if(serializedItems.endsWith(",")){
        serializedItems.dropLast(1)
    }
    return serializedItems
}

fun Player.addRewards(items: List<ItemStack>){
    var oldItems = this.rewardItems.toMutableList()
    items.forEach{
        if(oldItems.size >= 27) {
            this.rewardItems = oldItems
            return
        }
        oldItems.add(it)
        oldItems = oldItems.compressSimilarItems()
    }
    this.rewardItems = oldItems
}

fun Player.addInstantRewards(items: List<ItemStack>){
    var oldItems = this.instantRewardItems.toMutableList()
    items.forEach{
        if(oldItems.size >= 27) {
            this.instantRewardItems = oldItems
            return
        }
        oldItems.add(it)
        oldItems = oldItems.compressSimilarItems()
    }
    this.instantRewardItems = oldItems
}

var Player.rewardItems: List<ItemStack>
    get() = run {
        var items = mutableListOf<ItemStack>()
        val row = plugin.playerItems.getRowsWhere(
            "playerData",
            "NORMALITEMS",
            "UUID = '${this.uniqueId.cleanUp()}'"
        )

        if(row.isNotEmpty()){
            if(row[0].values.isNotEmpty()){
                if((row[0].values[0] as String).isEmpty()) return emptyList()
                val encodedArray = (row[0].values[0] as String).split(",")
                if(encodedArray.isEmpty()) return emptyList()
                encodedArray.dropLast(1).forEach {
                    if(items.size >= 27) return items // hard limit, inventory is full
                    items.add(ItemStack.deserializeBytes(Base64.getDecoder().decode(it)))
                    items = items.compressSimilarItems()
                }
            }
        }
        items = items.compressSimilarItems()
        return items
    }
    set(value) {
        updateRewardItems(value, this.uniqueId)
    }

var Player.instantRewardItems: List<ItemStack>
    get() = run {
        var items = mutableListOf<ItemStack>()
        val row = plugin.playerItems.getRowsWhere(
            "playerData",
            "INSTANTITEMS",
            "UUID = '${this.uniqueId.cleanUp()}'"
        )

        if(row.isNotEmpty()){
            if(row[0].values.isNotEmpty()){
                if((row[0].values[0] as String).isEmpty()) return emptyList()
                val encodedArray = (row[0].values[0] as String).split(",")
                if(encodedArray.isEmpty()) return emptyList()
                encodedArray.dropLast(1).forEach {
                    if(items.size >= 27) return items // hard limit, inventory is full
                    items.add(ItemStack.deserializeBytes(Base64.getDecoder().decode(it)))
                    items = items.compressSimilarItems()
                }
            }
        }
        items = items.compressSimilarItems()
        return items
    }
    set(value) {
        updateInstantRewardItems(value, this.uniqueId)
    }

val COOKIE_OF_LOVE = run {
    val item = ItemStack(Material.COOKIE)
    val meta = item.itemMeta
    val lore = listOf(
        Component.text("A piece of my heart to you, literally", NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
        Component.empty(),
        Component.text("(Plugin made with <3 by Cookie)", NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
    )
    meta.displayName(
        Component.empty().decoration( // Wow, such great api design
            TextDecoration.ITALIC, TextDecoration.State.FALSE
        ).append("<b><color:#be32ed>Cookie of LOVE </b><color:#ff0090>❤".formatMinimessage())
    )
    meta.lore(lore)
    meta.persistentDataContainer.set(
        NamespacedKey(plugin, "item_id"),
        PersistentDataType.STRING, "COOKIE_OF_LOVE"
    )
    item.itemMeta = meta
    item
}
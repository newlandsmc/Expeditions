package me.cookie.expeditions

import me.cookie.expeditions.data.sql.database.Values

import me.cookie.cookiecore.cleanUp
import me.cookie.cookiecore.compressSimilarItems
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

private val plugin = JavaPlugin.getPlugin(Expeditions::class.java)

val rewardConfig = plugin.rewardsConfig.getCustomConfig()
val rewards = rewardConfig!!.getConfigurationSection("Rewards")!!.getKeys(false).toList()

fun Player.generateOfflineRewards(): List<ItemStack> {
    val offlineMinutes = (System.currentTimeMillis() - this.lastLogoff) / 60000
    var items = mutableListOf<ItemStack>()

    items.addAll(this.rewardItems)

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
    // I know while loops the main thread, but let's hope for the best :)
    // Keeps looping until out of weight
    while(weight > 0){
        // Player doesn't have enough weight for this reward
        val reward = rewards[Random().nextInt(rewards.size)]
        val rewardWeight = rewardConfig.getInt("Rewards.$reward.weight")
        if(rewardWeight > weight) {
            continue
        } else{
            weight -= rewardWeight
            items.add(
                ItemStack(Material.valueOf(reward))
            )
            items = items.compressSimilarItems()

            if(items.size >= 27) return items // hard limit, inventory is full
        }

    }

    return items
}

fun Player.giveVoteRewards(): List<ItemStack> {
    var items = mutableListOf<ItemStack>()
    var weight = rewardConfig!!.getInt("VoteTier.weight")
    while(weight > 0){

        // Player doesn't have enough weight for this reward
        val reward = rewards[Random().nextInt(rewards.size)]
        val rewardWeight = rewardConfig.getInt("Rewards.$reward.weight")
        if(rewardWeight > weight) {
            continue
        } else{
            weight -= rewardWeight
            items.add(
                ItemStack(Material.valueOf(reward))
            )
            if(items.size >= 27){
                items = items.compressSimilarItems() // Compress and check if inv is full
                if(items.size >= 27){
                    return items
                }
            }
        }

    }
    items = items.compressSimilarItems()
    return items
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
    var items = mutableListOf<ItemStack>()
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
                items = items.compressSimilarItems()
            }
        }
    }
    items = items.compressSimilarItems()
    return items
}

var Player.rewardItems: List<ItemStack>
    get() = run {
        getRewardItems(this.uniqueId)
    }
    set(value) {
        this.updateRewardItems(value)
    }
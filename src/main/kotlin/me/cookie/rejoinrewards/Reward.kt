package me.cookie.rejoinrewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

private val plugin = JavaPlugin.getPlugin(RejoinRewards::class.java)

/* TODO make storage persistent (database/file storage) */
val playerRewardMap = HashMap<UUID, List<ItemStack>>()
val rewardConfig = plugin.rewardsConfig.getCustomConfig()
val rewards = rewardConfig!!.getConfigurationSection("Rewards")!!.getKeys(false).toList()

fun Player.generateOfflineRewards(): List<ItemStack> {
    val offlineMinutes = (System.currentTimeMillis() - this.lastLogoff!!) / 60000
    val items = mutableListOf<ItemStack>()

    // Add old rewards
    if(playerRewardMap[this.uniqueId] != null){
        playerRewardMap[this.uniqueId]!!.forEach {
            if(items.size >= 27) return items // hard limit, inventory is full
            items.add(it)
        }
    }
    // Not eligible for new rewards.
    if (
        offlineMinutes
        <
        rewardConfig!!.getConfigurationSection("Tiers")!!.getKeys(false).toList()[0].toInt()
    ) {
        return items
    }

    var weight = 0
    var previousSection = "0"
    // Gets the weight for the offline tier
    for(section in rewardConfig.getConfigurationSection("Tiers")!!.getKeys(false)){
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
            if(items.size >= 27) return items // hard limit, inventory is full
        }

    }

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
        "${this.name} was gone for ${(System.currentTimeMillis() - this.lastLogoff!!) / 60000} minute(s)."
    )
}
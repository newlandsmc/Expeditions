package me.cookie.rejoinrewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class Reward {
    // TODO calculate the reward to give to the player

}

/* TODO make storage persistent (database/file storage) */
val playerRewardMap = HashMap<UUID, List<ItemStack>>()
private val plugin = JavaPlugin.getPlugin(RejoinRewards::class.java)

fun Player.generateOfflineRewards(): List<ItemStack> {
    val items = mutableListOf<ItemStack>()

    // Add old rewards
    if(playerRewardMap[this.uniqueId] != null){
        playerRewardMap[this.uniqueId]!!.forEach {
            if(items.size >= 26) return items // hard limit, inventory is full
            items.add(it)
        }
    }
    // Test item 1
    val item = ItemStack(Material.STICK)
    val meta = item.itemMeta
    meta.displayName(Component.text("test"))
    item.itemMeta = meta

    // Test item 2
    val item1 = ItemStack(Material.STICK)
    val meta1 = item1.itemMeta
    meta1.displayName(Component.text("test1"))
    item1.itemMeta = meta1

    items.add(item)
    items.add(item1)

    // TODO calculate items from config


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
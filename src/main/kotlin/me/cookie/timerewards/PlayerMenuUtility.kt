package me.cookie.timerewards

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


class PlayerMenuUtility(val owner: Player) {
    // items that remained in the gui
    var rewards: List<ItemStack>? = null
}


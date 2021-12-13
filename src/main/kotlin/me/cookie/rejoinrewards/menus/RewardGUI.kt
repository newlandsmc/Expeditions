package me.cookie.rejoinrewards.menus

import me.cookie.rejoinrewards.rewardItems
import me.cookie.rejoinrewards.updateRewardItems
import me.cookie.semicore.PlayerMenuUtility
import me.cookie.semicore.gui.Menu
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack


class RewardGUI(playerMenuUtility: PlayerMenuUtility) : Menu(playerMenuUtility) {
    override val menuName: Component
        get() = Component.text("Rewards")
    override val slots: Int
        get() = 27

    override fun handleClick(e: InventoryClickEvent?) {
        e!!.inventory.contents!!.asList()
    }

    override fun handleClose(e: InventoryCloseEvent) {
        val contents = mutableListOf<ItemStack>()
        e.inventory.contents!!.forEach {
            if(it == null) return@forEach
            if(it.type != Material.AIR) contents.add(it)
        }
        playerMenuUtility.owner.updateRewardItems(contents.toList())

    }

    override fun setMenuItems() {
        _inventory!!.setContents(playerMenuUtility.owner.rewardItems.toTypedArray())
    }
}
package me.cookie.rejoinrewards.gui.menus

import me.cookie.rejoinrewards.PlayerMenuUtility
import me.cookie.rejoinrewards.generateOfflineRewards
import me.cookie.rejoinrewards.gui.Menu
import me.cookie.rejoinrewards.playerRewardMap
import net.kyori.adventure.text.Component
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
        playerRewardMap[e.player.uniqueId] = e.inventory.contents!!.toList() as List<ItemStack>
    }

    override fun setMenuItems() {
        _inventory!!.setContents(playerRewardMap[playerMenuUtility.owner.uniqueId]!!.toTypedArray())
    }
}
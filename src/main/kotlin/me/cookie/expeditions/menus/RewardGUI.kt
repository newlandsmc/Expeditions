package me.cookie.expeditions.menus

import me.cookie.expeditions.rewardItems
import me.cookie.cookiecore.PlayerMenuUtility
import me.cookie.cookiecore.compressSimilarItems
import me.cookie.cookiecore.gui.Menu
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack


class RewardGUI(playerMenuUtility: PlayerMenuUtility) : Menu(playerMenuUtility) {
    override val menuName: Component
        get() = Component.text("Rewards")
    override val slots: Int
        get() = 27

    override fun handleClick(e: InventoryClickEvent) {
        if(e.clickedInventory == null) return
        if(e.clickedInventory!!.type == InventoryType.PLAYER){
            // Players are not allowed to put items in the gui
            e.isCancelled = true
        }
    }


    override fun handleClose(e: InventoryCloseEvent) {
        val contents = mutableListOf<ItemStack>()
        e.inventory.contents!!.forEach {
            if(it == null) return@forEach
            if(it.type != Material.AIR) contents.add(it) // Clean inventory output
        }
        val compressed = contents.compressSimilarItems()
        playerMenuUtility.player.rewardItems = compressed

    }

    override fun setMenuItems() {
        cInventory!!.setContents(playerMenuUtility.player.rewardItems.compressSimilarItems().toTypedArray())
    }
}
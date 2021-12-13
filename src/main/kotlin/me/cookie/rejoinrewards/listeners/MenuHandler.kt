package me.cookie.rejoinrewards.listeners

import me.cookie.semicore.gui.Menu
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent


class MenuHandler: Listener {
    @EventHandler
    fun onMenuClick(e: InventoryClickEvent) {
        val holder = e.inventory.holder
        if (holder is Menu) {
            if (e.currentItem == null) {
                return
            }
            val menu: Menu = holder
            menu.handleClick(e)
        }
    }
    @EventHandler
    fun onMenuClose(e: InventoryCloseEvent){
        val holder = e.inventory.holder
        if (holder is Menu) {
            val menu: Menu = holder
            menu.handleClose(e)
        }
    }
}
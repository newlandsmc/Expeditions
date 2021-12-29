package me.cookie.expeditions.menus

import com.vexsoftware.votifier.model.Vote
import com.vexsoftware.votifier.model.VotifierEvent
import me.cookie.cookiecore.PlayerMenuUtility
import me.cookie.cookiecore.gui.Menu
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

class TestVoteGUI(playerMenuUtility: PlayerMenuUtility) : Menu(playerMenuUtility) {
    override val menuName: Component
        get() = Component.text("test votes")
    override val slots: Int
        get() = 9

    override fun handleClick(e: InventoryClickEvent) {
        val clickedItem = e.currentItem ?: return
        e.isCancelled = true
        when(clickedItem.type){
            Material.RED_WOOL -> {
                Bukkit.getPluginManager().callEvent(VotifierEvent(
                    Vote(
                       "Test 1",
                       e.whoClicked.name,
                       "0.0.0.0",
                        System.currentTimeMillis().toString()
                    )
                ))
            }
            Material.YELLOW_WOOL -> {
                Bukkit.getPluginManager().callEvent(VotifierEvent(
                    Vote(
                        "Test 2",
                        e.whoClicked.name,
                        "0.0.0.0",
                        System.currentTimeMillis().toString()
                    )
                ))
            }
            Material.GREEN_WOOL -> {
                Bukkit.getPluginManager().callEvent(VotifierEvent(
                    Vote(
                        "Test 3",
                        e.whoClicked.name,
                        "0.0.0.0",
                        System.currentTimeMillis().toString()
                    )
                ))
            }
            Material.BLUE_WOOL -> {
                Bukkit.getPluginManager().callEvent(VotifierEvent(
                    Vote(
                        "Test 4",
                        e.whoClicked.name,
                        "0.0.0.0",
                        System.currentTimeMillis().toString()
                    )
                ))
            }
            else -> {
                return
            }
        }

    }

    override fun handleClose(e: InventoryCloseEvent) {
        // Do nothing
    }

    override fun setMenuItems() {
        inventory.setItem(
            0,
            ItemStack(Material.RED_WOOL)
        )
        inventory.setItem(
            1,
            ItemStack(Material.YELLOW_WOOL)
        )
        inventory.setItem(
            2,
            ItemStack(Material.GREEN_WOOL)
        )
        inventory.setItem(
            3,
            ItemStack(Material.BLUE_WOOL)
        )
    }
}
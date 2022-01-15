package me.cookie.expeditions.menus

import com.vexsoftware.votifier.model.Vote
import com.vexsoftware.votifier.model.VotifierEvent
import me.cookie.cookiecore.PlayerMenuUtility
import me.cookie.cookiecore.gui.Menu
import me.cookie.expeditions.generateVoteRewards
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class TestVoteGUI(playerMenuUtility: PlayerMenuUtility) : Menu(playerMenuUtility) {
    override val menuName: Component
        get() = Component.text("test votes")
    override val slots: Int
        get() = 9

    override fun handleClick(e: InventoryClickEvent) {
        val clickedItem = e.currentItem ?: return
        val player = e.whoClicked as Player
        e.isCancelled = true
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss Z")
        when(clickedItem.type){
            Material.RED_WOOL -> {
                Bukkit.getPluginManager().callEvent(VotifierEvent(
                    Vote(
                       "Test1",
                        player.name,
                       "0.0.0.0",
                        ZonedDateTime.now().format(formatter)
                    )
                ))
            }
            Material.YELLOW_WOOL -> {
                Bukkit.getPluginManager().callEvent(VotifierEvent(
                    Vote(
                        "Test2",
                         player.name,
                        "0.0.0.0",
                        ZonedDateTime.now().format(formatter)
                    )
                ))
            }
            Material.GREEN_WOOL -> {
                Bukkit.getPluginManager().callEvent(VotifierEvent(
                    Vote(
                        "Test3",
                         player.name,
                        "0.0.0.0",
                        ZonedDateTime.now().format(formatter)
                    )
                ))
            }
            Material.BLUE_WOOL -> {
                Bukkit.getPluginManager().callEvent(VotifierEvent(
                    Vote(
                        "Test4",
                         player.name,
                        "0.0.0.0",
                        ZonedDateTime.now().format(formatter)
                    )
                ))
            }
            Material.GRAY_WOOL -> {
                val inventory = Bukkit.createInventory(null, 27, Component.text("CHEEZ"))
                inventory.setContents(player.generateVoteRewards().toTypedArray())

                player.openInventory(inventory)
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
        inventory.setItem(
            8,
            ItemStack(Material.GRAY_WOOL)
        )
    }
}
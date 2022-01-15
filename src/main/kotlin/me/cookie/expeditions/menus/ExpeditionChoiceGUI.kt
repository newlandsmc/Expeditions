package me.cookie.expeditions.menus

import me.cookie.cookiecore.ItemStackBuilder
import me.cookie.cookiecore.PlayerMenuUtility
import me.cookie.cookiecore.formatMinimessage
import me.cookie.cookiecore.gui.Menu
import me.cookie.expeditions.instantRewardItems
import me.cookie.expeditions.rewardItems
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemFlag

class ExpeditionChoiceGUI(playerMenuUtility: PlayerMenuUtility) : Menu(playerMenuUtility) {
    override val menuName: Component
        get() = Component.text("Choose expedition type to claim")
    override val slots: Int
        get() = 27

    override fun handleClick(e: InventoryClickEvent) {
        e.isCancelled = true
        e.currentItem ?: return
        if(e.currentItem!!.type == Material.AIR) return
        if(e.currentItem!!.type == Material.GRAY_DYE) {
            if(e.slot == 12){
                playerMenuUtility.player.sendMessage(
                    "<red>You don't have any expeditions to claim!"
                        .formatMinimessage()
                )
                return
            }
            if(e.slot == 14){
                playerMenuUtility.player.sendMessage(
                    "<red>You don't have any instant expeditions to claim!"
                        .formatMinimessage()
                )
                return
            }
        }
        when(e.slot){
            12 -> {
                RewardGUI(playerMenuUtility).open()
                return
            }
            14 -> {
                InstantRewardGUI(playerMenuUtility).open()
            }
            else -> {
                return
            }
        }
        return
    }

    override fun handleClose(e: InventoryCloseEvent) {
        return
    }

    override fun setMenuItems() {
        if(playerMenuUtility.player.rewardItems.isNotEmpty()){
            inventory.setItem(
                12,
                ItemStackBuilder(Material.DIAMOND_SWORD)
                    .withName(
                        Component.empty().decoration( // Wow, such great api design
                            TextDecoration.ITALIC, TextDecoration.State.FALSE
                        ).append("<aqua>Normal Expeditions".formatMinimessage())
                    )
                    .withLore(
                        Component.empty().decoration( // Wow, such great api design
                            TextDecoration.ITALIC, TextDecoration.State.FALSE
                        ).append("<white>Obtained when you are offline for some time.".formatMinimessage())
                    )
                    .withFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .build()
            )
        }else{
            inventory.setItem(
                12,
                ItemStackBuilder(Material.GRAY_DYE)
                    .withName(
                        Component.empty().decoration( // Wow, such great api design
                            TextDecoration.ITALIC, TextDecoration.State.FALSE
                        ).append("<aqua>Normal Expeditions".formatMinimessage())
                    )
                    .withLore(
                        Component.empty().decoration( // Wow, such great api design
                            TextDecoration.ITALIC, TextDecoration.State.FALSE
                        ).append("<white>Obtained when you are offline for some time.".formatMinimessage()),
                        Component.empty().decoration( // Wow, such great api design
                            TextDecoration.ITALIC, TextDecoration.State.FALSE
                        ).append("<gray>You dont have any rewards here.".formatMinimessage()),
                        Component.empty().decoration( // Wow, such great api design
                            TextDecoration.ITALIC, TextDecoration.State.FALSE
                        ).append("<gray>Come back later!".formatMinimessage())
                    )
                    .withFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .build()
            )
        }
        if(playerMenuUtility.player.instantRewardItems.isNotEmpty()){
            inventory.setItem(
                14,
                ItemStackBuilder(Material.NETHER_STAR)
                    .withName(
                        Component.empty().decoration( // Wow, such great api design
                            TextDecoration.ITALIC, TextDecoration.State.FALSE
                        ).append("<gold>Instant Expeditions".formatMinimessage())
                    )
                    .withLore(
                        Component.empty().decoration( // Wow, such great api design
                            TextDecoration.ITALIC, TextDecoration.State.FALSE
                        ).append("<white>Obtained by voting or purchasing instant expeditions!".formatMinimessage()),
                        Component.empty().decoration( // Wow, such great api design
                            TextDecoration.ITALIC, TextDecoration.State.FALSE
                        ).append("<white>instant expeditions!".formatMinimessage())
                    )
                    .build()
            )
        }else{
            inventory.setItem(
                14,
                ItemStackBuilder(Material.GRAY_DYE)
                    .withName(
                        Component.empty().decoration( // Wow, such great api design
                            TextDecoration.ITALIC, TextDecoration.State.FALSE
                        ).append("<gold>Instant Expeditions".formatMinimessage())
                    )
                    .withLore(
                        Component.empty().decoration( // Wow, such great api design
                            TextDecoration.ITALIC, TextDecoration.State.FALSE
                        ).append("<white>Obtained by voting or purchasing instant expeditions!".formatMinimessage()),
                        Component.empty().decoration( // Wow, such great api design
                            TextDecoration.ITALIC, TextDecoration.State.FALSE
                        ).append("<white>instant expeditions!".formatMinimessage()),
                        Component.empty().decoration( // Wow, such great api design
                            TextDecoration.ITALIC, TextDecoration.State.FALSE
                        ).append("<gray>You dont have any rewards here.".formatMinimessage()),
                        Component.empty().decoration( // Wow, such great api design
                            TextDecoration.ITALIC, TextDecoration.State.FALSE
                        ).append("<gray>Come back later!".formatMinimessage())
                    )
                    .build()
            )
        }

    }
}
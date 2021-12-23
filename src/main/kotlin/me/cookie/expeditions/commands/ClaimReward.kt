package me.cookie.expeditions.commands

import me.cookie.expeditions.Expeditions
import me.cookie.expeditions.getRewardItems
import me.cookie.expeditions.menus.RewardGUI
import me.cookie.expeditions.updateRewardItems
import me.cookie.cookiecore.CookieCore
import me.cookie.cookiecore.formatPlayerPlaceholders
import me.cookie.cookiecore.playerMenuUtility
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.plugin.java.JavaPlugin

class ClaimReward: CommandExecutor {
    private val plugin = JavaPlugin.getPlugin(Expeditions::class.java)
    private val semiCore = JavaPlugin.getPlugin(CookieCore::class.java)
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player){
            plugin.logger.info("Only players are allowed to execute this command")
            return true
        }
        val player = sender

        if(args.isEmpty()){
            if(!player.hasPermission("expeditions.spoils.claim")){
                player.sendMessage(
                    MiniMessage.get().parse(
                        semiCore.config.getString("no-permission")!!
                            .formatPlayerPlaceholders(player)
                    )
                )
                return true
            }
            val rewardGUI = RewardGUI(player.playerMenuUtility)
            rewardGUI.open()
            return true
        }

        if(!listOf("view", "clear").contains(args[0].toLowerCase()) /* Check if sub command exists */){
            player.sendMessage(
                MiniMessage.get().parse(
                    semiCore.config.getString("invalid-usage")!!
                        .formatPlayerPlaceholders(player)
                )
            )
            return true
        }

        when(args[0]){
            "view" -> {
                if(!player.hasPermission("expeditions.spoils.view")){
                    player.sendMessage(
                        MiniMessage.get().parse(
                            semiCore.config.getString("no-permission")!!
                                .formatPlayerPlaceholders(player)
                        )
                    )
                    return true
                }
                if(args.size < 2){
                    player.sendMessage(
                        MiniMessage.get().parse(
                            semiCore.config.getString("invalid-usage")!!
                                .formatPlayerPlaceholders(player)
                        )
                    )
                    return true
                }

                val viewing = Bukkit.getPlayer(args[1])

                if(viewing == null) {
                    player.sendMessage(Component.text("That player does not exist.", NamedTextColor.RED))
                    return true
                }

                val inventory = Bukkit.createInventory(
                    player,
                    InventoryType.CHEST,
                    Component.text("Viewing ${viewing.name}'s rewards.")
                )

                inventory.setContents(getRewardItems(viewing.uniqueId).toTypedArray()) // Fill the inventory with their rewards

                player.openInventory(inventory)
                return true
            }
            "clear" -> {
                if(!player.hasPermission("expeditions.spoils.clear")){
                    player.sendMessage(
                        MiniMessage.get().parse(
                            semiCore.config.getString("no-permission")!!
                                .formatPlayerPlaceholders(player)
                        )
                    )
                    return true
                }
                if(args.size < 2){
                    player.sendMessage(
                        MiniMessage.get().parse(
                            semiCore.config.getString("invalid-usage")!!
                                .formatPlayerPlaceholders(player)
                        )
                    )
                    return true
                }

                val clearing = Bukkit.getPlayer(args[1])

                clearing!!.updateRewardItems(listOf())  // Set to empty list
                return true
            }
        }

        return true
    }
}
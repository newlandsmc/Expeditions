package me.cookie.rejoinrewards.commands

import me.cookie.rejoinrewards.RejoinRewards
import me.cookie.rejoinrewards.gui.menus.RewardGUI
import me.cookie.rejoinrewards.playerMenuUtility
import me.cookie.rejoinrewards.playerRewardMap
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.plugin.java.JavaPlugin

class ClaimReward: CommandExecutor {
    private val plugin = JavaPlugin.getPlugin(RejoinRewards::class.java)
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player){
            plugin.logger.info("Only players are allowed to execute this command")
            return true
        }
        val player = sender

        if(args.isEmpty()){
            player.sendMessage(
                Component.text("I think you meant /jrewards claim", NamedTextColor.RED)
            )
            return true
        }

        if(!player.hasPermission("jrewards.${args[0].lowercase()}")){
            player.sendMessage(
                //TODO make configurable
                Component.text("You have no permission", NamedTextColor.RED)
            )
            return true
        }

        when(args[0]){
            "claim" -> {
                val rewardGUI = RewardGUI(player.playerMenuUtility)
                rewardGUI.open()
            }
            "view" -> {
                if(args[1].isEmpty()){
                    player.sendMessage(
                        Component.text("Please specify a player name")
                    )
                    return true
                }

                val viewing = Bukkit.getPlayer(args[1])

                val inventory = Bukkit.createInventory(
                    player,
                    InventoryType.CHEST,
                    Component.text("Viewing ${viewing!!.name}'s rewards.")
                )

                inventory.setContents(playerRewardMap[viewing.uniqueId]!!.toTypedArray()) // Fill the inventory with their rewards

                player.openInventory(inventory)
                return true
            }
            "clear" -> {
                if(args[1].isEmpty()){
                    player.sendMessage(
                        Component.text("Please specify a player name")
                    )
                    return true
                }

                val clearing = Bukkit.getPlayer(args[1])

                playerRewardMap[clearing!!.uniqueId] = listOf() // Set to empty list
                return true
            }
        }

        return true
    }
}
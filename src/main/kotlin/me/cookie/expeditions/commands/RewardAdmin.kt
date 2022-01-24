package me.cookie.expeditions.commands

import me.cookie.cookiecore.INVALID_USAGE
import me.cookie.cookiecore.NO_PERMISSION
import me.cookie.expeditions.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import java.util.*

class RewardAdmin: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(args.isEmpty()){
            sender.sendMessage(INVALID_USAGE)
            return true
        }
        if(!sender.hasPermission("expeditions.admin")){
            sender.sendMessage(
                NO_PERMISSION
            )
            return true
        }
        // Check if sub command exists
        if(!listOf("view", "clear", "add", "set").contains(args[0].lowercase(Locale.getDefault()))){
            sender.sendMessage(
                INVALID_USAGE
            )
            return true
        }

        // Players or console can execute
        if(args.size >= 2) {
            val target = Bukkit.getPlayer(args[1])
            when(args[0].lowercase(Locale.getDefault())){
                "clear" -> {
                    if(!sender.hasPermission("expeditions.spoils.clear")){
                        sender.sendMessage(
                            NO_PERMISSION
                        )
                        return true
                    }

                    target!!.rewardItems = listOf()  // Set to empty list
                    return true
                }
            }

            if(args.size == 4){
                if(target == null){
                    sender.sendMessage(INVALID_USAGE)
                    return true
                }
                when(args[0].lowercase(Locale.getDefault())){
                    "add" -> {
                        when(args[2].lowercase(Locale.getDefault())){
                            "weight" -> {
                                if(args[3].isNumber()){
                                    target.addRewards(calcRewards(args[3].toInt()))
                                }else{
                                    sender.sendMessage(INVALID_USAGE)
                                }
                                return true
                            }
                            "time" -> {
                                if(args[3].isNumber()){
                                    target.addRewards(target.generateOfflineRewards(
                                        args[3].toLong()
                                    ))
                                }else{
                                    sender.sendMessage(INVALID_USAGE)
                                }
                                return true
                            }
                        }
                    }
                    "set" -> {
                        when(args[2].lowercase(Locale.getDefault())){
                            "weight" -> {
                                if(args[3].isNumber()){
                                    target.rewardItems = calcRewards(args[3].toInt())
                                }else{
                                    sender.sendMessage(INVALID_USAGE)
                                }
                                return true
                            }
                            "time" -> {
                                if(args[3].isNumber()){
                                    target.rewardItems = target.generateOfflineRewards(
                                        args[3].toLong())

                                }else{
                                    sender.sendMessage(INVALID_USAGE)
                                }
                                return true
                            }
                        }
                    }
                }
                // Only console can execute
                if(sender !is Player){
                    // Nothing yet
                    return true
                }
                // Only players can execute
                when(args[0]){
                    "view" -> {
                        if(!sender.hasPermission("expeditions.spoils.view")){
                            sender.sendMessage(
                                NO_PERMISSION
                            )
                            return true
                        }

                        val inventory = Bukkit.createInventory(
                            sender,
                            InventoryType.CHEST,
                            Component.text("Viewing ${target.name}'s rewards.")
                        )

                        inventory.setContents(target.rewardItems.toTypedArray()) // Fill the inventory with their rewards

                        sender.openInventory(inventory)
                        return true
                    }
                }
            }
            return true
        }
        return true
    }
}
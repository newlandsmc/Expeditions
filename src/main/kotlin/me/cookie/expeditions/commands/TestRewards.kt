package me.cookie.expeditions.commands

import me.cookie.expeditions.calcRewards
import me.cookie.expeditions.generateOfflineRewards
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class TestRewards: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player) return true

        val inventory = Bukkit.createInventory(null, 27)
        if(args.size < 2) return true
        when(args[0].lowercase(Locale.getDefault())){
            "weight" -> {
                inventory.setContents(calcRewards(args[1].toInt()).toTypedArray())
            }
            "time" -> {
                inventory.setContents(
                    sender.generateOfflineRewards(args[1].toLong(), false).toTypedArray()
                )
            }
        }

        sender.openInventory(inventory)
        return true
    }
}
package me.cookie.expeditions.commands

import me.cookie.cookiecore.NO_PERMISSION
import me.cookie.cookiecore.playerMenuUtility
import me.cookie.expeditions.menus.ExpeditionChoiceGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class ClaimReward(private val plugin: JavaPlugin): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player){
            plugin.logger.info("Only players are allowed to execute this command")
            return true
        }
        val player = sender

        if(args.isEmpty()){
            if(!player.hasPermission("expeditions.spoils.claim")){
                player.sendMessage(
                    NO_PERMISSION
                )
                return true
            }

            ExpeditionChoiceGUI(player.playerMenuUtility).open()
            return true
        }
        return true
    }
}
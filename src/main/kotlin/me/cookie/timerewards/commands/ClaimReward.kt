package me.cookie.timerewards.commands

import me.cookie.timerewards.TimeRewards
import me.cookie.timerewards.gui.menus.RewardGUI
import me.cookie.timerewards.playerMenuUtility
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class ClaimReward: CommandExecutor {
    private val plugin = JavaPlugin.getPlugin(TimeRewards::class.java)
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player){
            plugin.logger.info("Only players are allowed to execute this command")
            return true
        }
        val player = sender as Player

        if(!player.hasPermission("jrewards.claim")){
            player.sendMessage(
                //TODO make configurable
                "You have no permission"
            )
        }

        val rewardGUI = RewardGUI(player.playerMenuUtility)

        rewardGUI.open()

        return true
    }
}
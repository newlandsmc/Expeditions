package me.cookie.expeditions.commands

import me.cookie.cookiecore.data.sql.H2Storage
import me.cookie.cookiecore.playerMenuUtility
import me.cookie.expeditions.COOKIE_OF_LOVE
import me.cookie.expeditions.menus.TestVoteGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TestVote(val playerVotes: H2Storage): CommandExecutor{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player) return true
        if(args.isEmpty()){
            TestVoteGUI(sender.playerMenuUtility).open()
            return true
        }
        sender.inventory.addItem(COOKIE_OF_LOVE)
        return true
    }

}
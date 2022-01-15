// TODO: Move voting to CookieCore if needed

package me.cookie.expeditions.listeners

import com.vexsoftware.votifier.model.Vote
import com.vexsoftware.votifier.model.VotifierEvent
import me.cookie.cookiecore.data.Values
import me.cookie.cookiecore.data.sql.H2Storage
import me.cookie.cookiecore.formatMinimessage
import me.cookie.cookiecore.formatPlayerPlaceholders
import me.cookie.expeditions.addInstantRewards
import me.cookie.expeditions.generateVoteRewards
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class PlayerVoting(private val plugin: JavaPlugin, private val playerVotes: H2Storage): Listener {
    private var services: List<String> = plugin.config.getStringList("vote-services")
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
    @EventHandler fun onVote(event: VotifierEvent) {
        if(!services.contains(event.vote.serviceName)){
            plugin.logger.info("Vote received from a non permitted service. Ignoring")
            return
        }
        val offlinePlayer = Bukkit.getOfflinePlayer(event.vote.username)
        if(!offlinePlayer.hasPlayedBefore()){
            plugin.logger.info("Vote received from a never seen player. Ignoring")
            return
        }

        val onlinePlayer = offlinePlayer.player
        onlinePlayer!!

        onlinePlayer.getVotes(ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.systemDefault())).forEach { votes ->
            votes.values.forEach { vote ->
                if(vote == event.vote.serviceName){
                    if(offlinePlayer.isOnline){
                        onlinePlayer.sendMessage(
                            Component.text("You already voted with that service today.", NamedTextColor.RED)
                        )
                    }
                    return
                }
            }
        }
        addVote(event.vote)
        val votes = onlinePlayer.getVotes(ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.systemDefault()))
        if(offlinePlayer.isOnline){
            onlinePlayer.sendMessage(
                plugin.config.getString("vote-incomplete-message")!!
                    .formatServerPlaceholders(onlinePlayer)
                    .formatPlayerPlaceholders(onlinePlayer)
                    .formatMinimessage()
            )
            if(votes.size == services.size){
                onlinePlayer.sendMessage(
                    plugin.config.getString("vote-complete-message")!!
                        .formatServerPlaceholders(onlinePlayer)
                        .formatPlayerPlaceholders(onlinePlayer)
                        .formatMinimessage()
                )
                if(onlinePlayer.hasPermission("expeditions.instantrewards")){
                    onlinePlayer.sendMessage(
                        plugin.config.getString("instant-package-message")!!
                            .formatPlayerPlaceholders(onlinePlayer)
                            .formatMinimessage()
                    )
                }else{
                    onlinePlayer.addInstantRewards(onlinePlayer.generateVoteRewards())
                }
            }
        }

    }

    private fun addVote(playerVote: Vote){
        playerVotes.insertIntoTable(
            "playerVotes",
            listOf("PLAYER", "SERVICE", "TIMESTAMP"),
            Values(playerVote.username.lowercase(Locale.getDefault()), playerVote.serviceName,
                ZonedDateTime.now().format(formatter))
        )
    }
    fun Player.getVotes(now: ZonedDateTime): List<Values>{
        return playerVotes.getRowsWhere(
            "playerVotes",
            "SERVICE",
            "TIMESTAMP >= '${now.format(formatter).replace("12:00:00", "00:00:00")}' AND " +
                    "TIMESTAMP < '${now.plusDays(1).format(formatter).replace("12:00:00", "00:00:00")}' " +
                    "AND PLAYER = '${this.name.lowercase(Locale.getDefault())}'",
            20
        )
    }
    fun String.formatServerPlaceholders(player: Player): String {
        var formatted = this
        if(formatted.contains("(voteServices)")){
            formatted = formatted.replace(
                "(voteServices)",
                plugin.config.getStringList("vote-services").size.toString()
            )
        }

        if(formatted.contains("(playerVotesToday)")){
            formatted = formatted.replace(
                "(playerVotesToday)",
                player.getVotes(ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.systemDefault())).size.toString()
            )
        }
        return formatted
    }

}



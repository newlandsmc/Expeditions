package me.cookie.rejoinrewards.messagequeueing

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class QueuedMessage(
    var message: Component = Component.text("UNIMPLEMENTED"),
    var receiver: MessageReceiver = MessageReceiver.GLOBAL,
    var whenToSend: Long =  System.currentTimeMillis(),
    var playerToSend: Player? = null,
    val playersToSend: List<Player>? = null
){
    init {
        when(receiver){
            MessageReceiver.PLAYER -> {
                if(playerToSend == null) {
                    throw NullPointerException("You forgot to add a player to QueuedMessage")
                }
            }
            MessageReceiver.PLAYERS -> {
                if(playersToSend == null) {
                    throw NullPointerException("You forgot to add a player list to QueuedMessage")
                }
            }
            else -> {
                // Everything is fine, GLOBAL receiver does not need additional variables
            }
        }
    }
}
package me.cookie.rejoinrewards.messagequeueing

import me.cookie.rejoinrewards.RejoinRewards
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

private val messageQueue = mutableListOf<QueuedMessage>()

class MessageQueueing {


    private val plugin = JavaPlugin.getPlugin(RejoinRewards::class.java)

    private val garbage = mutableListOf<QueuedMessage>()

    fun startRunnable(){
        object: BukkitRunnable() {
            override fun run() {
                if(messageQueue.isEmpty()) return
                messageQueue.forEach { message ->
                    // Check if its time for a message to be sent
                    if(!(System.currentTimeMillis() >= message.whenToSend)) return@forEach

                    when(message.receiver){
                        MessageReceiver.GLOBAL -> {
                            Bukkit.broadcast(message.message)
                        }
                        MessageReceiver.PLAYER -> {
                            message.playerToSend!!.sendMessage(message.message)
                        }
                        MessageReceiver.PLAYERS -> {
                            message.playersToSend!!.forEach { player ->
                                player.sendMessage(message.message)
                            }
                        }
                    }
                    garbage.add(message)
                }
                messageQueue.removeAll(garbage)
                garbage.clear()
            }

        }.runTaskTimer(plugin, 40, 10 /* half a second delay, for accuracy */)
    }

    companion object{
        @JvmStatic
        fun Player.queueMessage(message: Component, seconds: Int){
            messageQueue.add(
                QueuedMessage(
                    message = message,
                    MessageReceiver.PLAYER,
                    whenToSend = System.currentTimeMillis() + (seconds.toLong() * 1000),
                    this
                )
            )
        }
    }
}


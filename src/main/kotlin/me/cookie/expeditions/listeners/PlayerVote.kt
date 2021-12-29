package me.cookie.expeditions.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import com.vexsoftware.votifier.model.VotifierEvent

class PlayerVote: Listener {
    @EventHandler fun onVote(event: VotifierEvent){
        println(event.vote.username)
        println(event.vote.address)
        println(event.vote.serviceName)
        println(event.vote.timeStamp)
        println(event.vote.additionalData)
    }
}
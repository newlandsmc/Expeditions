package me.cookie.timerewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

class Reward {
}

fun Player.spawnRejoinReward() {
    if(!this.hasPlayedBefore()) return
    this.sendMessage(
        Component.text(
            /* TODO: needs to be configurable */
            "You found treasure while you were away! Look for a chest nearby.",
            NamedTextColor.GREEN
        )
    )

}
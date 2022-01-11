package me.cookie.expeditions.listeners

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class PlayerInteract(val plugin: JavaPlugin): Listener {

    @EventHandler fun onPlayerInteract(event: PlayerInteractEvent){
        val player = event.player
        val item = event.item ?: return
        if(item.type == Material.AIR) return
        if(!item.hasItemMeta()) return
        if(!item.itemMeta.persistentDataContainer.has(NamespacedKey(plugin, "item_id"), PersistentDataType.STRING))
            return
        if(item.itemMeta.persistentDataContainer.get(
                NamespacedKey(plugin, "item_id"), PersistentDataType.STRING) == "COOKIE_OF_LOVE"){
            item.amount = item.amount-1
            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.REGENERATION,
                    100,
                    0
                )
            )
            player.playSound(Sound.sound(
                Key.key("entity.generic.eat"),
                Sound.Source.PLAYER,
                1f,
                1f
            ))
        }
    }
}
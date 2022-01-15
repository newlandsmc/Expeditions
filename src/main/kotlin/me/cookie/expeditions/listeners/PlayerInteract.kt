package me.cookie.expeditions.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerInteract(private val plugin: JavaPlugin): Listener {

    @EventHandler fun onPlayerInteract(event: PlayerInteractEvent){
        // Saved for a rainy day

        /*val player = event.player
        val item = event.item ?: return
        if(item.type == Material.AIR) return
        if(!item.hasItemMeta()) return
        if(!item.itemMeta.persistentDataContainer.has(NamespacedKey(plugin, "item_id"), PersistentDataType.STRING))
            return
        when(item.itemMeta.persistentDataContainer.get(
            NamespacedKey(plugin, "item_id"), PersistentDataType.STRING)){

            "COOKIE_OF_LOVE" -> {
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

        }*/
    }
}
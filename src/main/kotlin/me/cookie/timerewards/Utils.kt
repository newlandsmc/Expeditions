package me.cookie.timerewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

class Utils {
}

fun Component.toString(): String = PlainTextComponentSerializer.plainText().serialize(this)

package com.ioki.midiserver

import java.util.concurrent.ConcurrentHashMap

val AVAILABLE_COLORS = ConcurrentHashMap.newKeySet<String>().apply {
    arrayOf(
        "red",
        "green",
        "blue",
        "orange",
        "salmon",
        "black",
        "firebrick",
        "aqua",
        "blueviolet",
        "coral",
        "deeppink",
        "greenyellow",
        "lightseagreen",
        "navy",
        "seagreen",
        "silver"
    ).forEach { add(it) }
}


class ColorManager {
    fun getColor(): String {
        val randomColor = AVAILABLE_COLORS.random()
        AVAILABLE_COLORS.remove(randomColor)
        return randomColor
    }

    fun releaseColor(color: String) {
        AVAILABLE_COLORS.add(color)
    }
}
package com.ioki.midiserver

val colors = arrayOf(
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
)

fun getColorName(): String {
    return colors.random()
}
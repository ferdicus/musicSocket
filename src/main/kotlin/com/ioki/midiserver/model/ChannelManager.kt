package com.ioki.midiserver.model

import java.util.concurrent.ConcurrentHashMap

val channels = ConcurrentHashMap.newKeySet<Int>().apply {
    addAll(arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15))
}

fun getAvailableChannel(): Int {
    val channel = channels.random()
    channels.remove(channel)
    return channel
}

fun releaseChannel(channel: Int) {
    channels.add(channel)
}
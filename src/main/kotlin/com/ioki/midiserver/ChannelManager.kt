package com.ioki.midiserver

import java.util.concurrent.ConcurrentHashMap

private val AVAILABLE_CHANNELS = ConcurrentHashMap.newKeySet<Int>().apply {
    for (i in 0..15) {
        add(i)
    }
}

class ChannelManager {
    fun getAvailableChannel(): Int {
        val channel = AVAILABLE_CHANNELS.random()
        AVAILABLE_CHANNELS.remove(channel)
        return channel
    }

    fun releaseChannel(channel: Int) {
        AVAILABLE_CHANNELS.add(channel)
    }
}

package com.ioki.midiserver.model

import io.javalin.websocket.WsSession

data class BandMember(
    val id: String,
    val name: String,
    @Transient
    val instrument: Int,
    val instrumentName: String,
    val channel: Int,
    val color: String,
    @Transient val session: WsSession
)